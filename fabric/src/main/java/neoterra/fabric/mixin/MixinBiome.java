package neoterra.fabric.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.biomepatch.BiomeFeaturePatches;
import neoterra.data.worldgen.preset.biomepatch.Filter;
import neoterra.data.worldgen.preset.biomepatch.Order;
import neoterra.data.worldgen.preset.biomepatch.PatchAdd;
import neoterra.data.worldgen.preset.biomepatch.PatchReplace;
import neoterra.mixin.MixinBiomeGenerationSettings;
import neoterra.world.worldgen.biome.IModifiableBiome;

@Mixin(Biome.class)
public abstract class MixinBiome implements IModifiableBiome {

	@Shadow @Final
	private BiomeGenerationSettings generationSettings;

	@Unique
	private BiomeGenerationSettings neoterra$modifiedSettings;

	@Unique
	private boolean neoterra$wrapLogged;

	@Unique
	private String neoterra$lastAppliedBiomeKey;

	@WrapOperation(method = "getGenerationSettings",
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/world/level/biome/Biome;generationSettings:Lnet/minecraft/world/level/biome/BiomeGenerationSettings;",
			opcode = Opcodes.GETFIELD))
	private BiomeGenerationSettings neoterra$useModifiedSettings(Biome instance, Operation<BiomeGenerationSettings> original) {
		BiomeGenerationSettings modified = this.neoterra$modifiedSettings;
		if (!this.neoterra$wrapLogged) {
			this.neoterra$wrapLogged = true;
			NTCommon.debug("MixinBiome wrap: first getGenerationSettings call for biome={} — returning {}",
				this.neoterra$lastAppliedBiomeKey != null ? this.neoterra$lastAppliedBiomeKey : "<unknown>",
				modified != null ? "MODIFIED" : "ORIGINAL");
		}
		return modified != null ? modified : original.call(instance);
	}

	@Override
	public void neoterra$applyPatches(BiomeFeaturePatches patches, Holder<Biome> self, HolderSet<Biome> overworldFallback) {
		String biomeKey = self.unwrapKey().map(Object::toString).orElse("<unkeyed>");
		this.neoterra$lastAppliedBiomeKey = biomeKey;
		this.neoterra$modifiedSettings = null;
		this.neoterra$wrapLogged = false;

		MixinBiomeGenerationSettings origAccessor = (MixinBiomeGenerationSettings) (Object) this.generationSettings;
		List<HolderSet<PlacedFeature>> origFeatures = origAccessor.getFeatures();
		NTCommon.debug("applyPatches[{}]: original features have {} steps", biomeKey, origFeatures.size());

		List<List<Holder<PlacedFeature>>> features = new ArrayList<>(origFeatures.size());
		for (HolderSet<PlacedFeature> step : origFeatures) {
			List<Holder<PlacedFeature>> mutableStep = new ArrayList<>();
			step.forEach(mutableStep::add);
			features.add(mutableStep);
		}

		boolean modified = false;
		int replacesApplied = 0;
		int addsApplied = 0;

		for (PatchReplace replace : patches.replaces()) {
			HolderSet<Biome> targetBiomes = replace.biomes().orElse(overworldFallback);
			if (!holderInSet(self, targetBiomes)) continue;
			int stepIdx = replace.step().ordinal();
			if (stepIdx >= features.size()) {
				NTCommon.debug("applyPatches[{}]: replace {} skipped — step {} >= features.size {}",
					biomeKey, replace.id(), stepIdx, features.size());
				continue;
			}
			List<Holder<PlacedFeature>> stepList = features.get(stepIdx);

			List<Holder<PlacedFeature>> toAdd = new ArrayList<>();
			int removedCount = 0;
			for (Map.Entry<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> e : replace.replacements().entrySet()) {
				int sizeBefore = stepList.size();
				if (stepList.removeIf(h -> h.is(e.getKey()))) {
					removedCount += sizeBefore - stepList.size();
					if (!toAdd.contains(e.getValue())) {
						toAdd.add(e.getValue());
					}
				}
			}
			if (!toAdd.isEmpty()) {
				stepList.addAll(toAdd);
				modified = true;
				replacesApplied++;
				NTCommon.debug("applyPatches[{}]: replace {} step={} removed={} added={}",
					biomeKey, replace.id(), replace.step(), removedCount, toAdd.size());
			} else {
				NTCommon.debug("applyPatches[{}]: replace {} step={} no-op (none of {} keys matched)",
					biomeKey, replace.id(), replace.step(), replace.replacements().size());
			}
		}

		for (PatchAdd add : patches.adds()) {
			if (!matchesFilter(self, add.filter(), overworldFallback)) continue;
			int stepIdx = add.step().ordinal();
			while (features.size() <= stepIdx) {
				features.add(new ArrayList<>());
			}
			List<Holder<PlacedFeature>> stepList = features.get(stepIdx);

			List<Holder<PlacedFeature>> toInsert = new ArrayList<>();
			add.features().forEach(toInsert::add);
			if (toInsert.isEmpty()) {
				NTCommon.debug("applyPatches[{}]: add {} step={} empty feature set, skipping",
					biomeKey, add.id(), add.step());
				continue;
			}

			if (add.order() == Order.PREPEND) {
				stepList.addAll(0, toInsert);
			} else {
				stepList.addAll(toInsert);
			}
			modified = true;
			addsApplied++;
			NTCommon.debug("applyPatches[{}]: add {} step={} order={} inserted={} (step now has {} features)",
				biomeKey, add.id(), add.step(), add.order(), toInsert.size(), stepList.size());
		}

		if (!modified) {
			NTCommon.debug("applyPatches[{}]: unchanged (0 replaces, 0 adds matched)", biomeKey);
			return;
		}

		List<HolderSet<PlacedFeature>> rebuilt = features.stream()
			.<HolderSet<PlacedFeature>>map(HolderSet::direct)
			.toList();

		Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers = origAccessor.getCarvers();
		this.neoterra$modifiedSettings = new BiomeGenerationSettings(carvers, rebuilt);
		NTCommon.debug("applyPatches[{}]: DONE — {} replaces + {} adds applied, rebuilt {} steps",
			biomeKey, replacesApplied, addsApplied, rebuilt.size());
	}

	@Unique
	private static boolean holderInSet(Holder<Biome> holder, HolderSet<Biome> set) {
		if (set.contains(holder)) return true;
		Optional<ResourceKey<Biome>> key = holder.unwrapKey();
		if (key.isEmpty()) return false;
		ResourceKey<Biome> k = key.get();
		for (Holder<Biome> h : set) {
			if (h.is(k)) return true;
		}
		return false;
	}

	@Unique
	private static boolean matchesFilter(Holder<Biome> holder, Optional<Filter> filterOpt, HolderSet<Biome> fallback) {
		if (filterOpt.isEmpty()) return holderInSet(holder, fallback);
		Filter filter = filterOpt.get();
		boolean inSet = holderInSet(holder, filter.biomes());
		return filter.behavior() == Filter.Behavior.WHITELIST ? inSet : !inSet;
	}
}
