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

	@WrapOperation(method = "getGenerationSettings",
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/world/level/biome/Biome;generationSettings:Lnet/minecraft/world/level/biome/BiomeGenerationSettings;",
			opcode = Opcodes.GETFIELD))
	private BiomeGenerationSettings neoterra$useModifiedSettings(Biome instance, Operation<BiomeGenerationSettings> original) {
		BiomeGenerationSettings modified = this.neoterra$modifiedSettings;
		return modified != null ? modified : original.call(instance);
	}

	@Override
	public void neoterra$applyPatches(BiomeFeaturePatches patches, Holder<Biome> self, HolderSet<Biome> overworldFallback) {
		this.neoterra$modifiedSettings = null;

		MixinBiomeGenerationSettings origAccessor = (MixinBiomeGenerationSettings) (Object) this.generationSettings;
		List<HolderSet<PlacedFeature>> origFeatures = origAccessor.getFeatures();

		List<List<Holder<PlacedFeature>>> features = new ArrayList<>(origFeatures.size());
		for (HolderSet<PlacedFeature> step : origFeatures) {
			List<Holder<PlacedFeature>> mutableStep = new ArrayList<>();
			step.forEach(mutableStep::add);
			features.add(mutableStep);
		}

		boolean modified = false;

		for (PatchReplace replace : patches.replaces()) {
			HolderSet<Biome> targetBiomes = replace.biomes().orElse(overworldFallback);
			if (!holderInSet(self, targetBiomes)) continue;
			int stepIdx = replace.step().ordinal();
			if (stepIdx >= features.size()) continue;
			List<Holder<PlacedFeature>> stepList = features.get(stepIdx);

			List<Holder<PlacedFeature>> toAdd = new ArrayList<>();
			for (Map.Entry<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> e : replace.replacements().entrySet()) {
				if (stepList.removeIf(h -> h.is(e.getKey()))) {
					if (!toAdd.contains(e.getValue())) {
						toAdd.add(e.getValue());
					}
				}
			}
			if (!toAdd.isEmpty()) {
				stepList.addAll(toAdd);
				modified = true;
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
			if (toInsert.isEmpty()) continue;

			if (add.order() == Order.PREPEND) {
				stepList.addAll(0, toInsert);
			} else {
				stepList.addAll(toInsert);
			}
			modified = true;
		}

		if (!modified) {
			NTCommon.debug("applyPatches: biome {} unchanged", self.unwrapKey().map(Object::toString).orElse("<unkeyed>"));
			return;
		}

		List<HolderSet<PlacedFeature>> rebuilt = features.stream()
			.<HolderSet<PlacedFeature>>map(HolderSet::direct)
			.toList();

		Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers = origAccessor.getCarvers();
		this.neoterra$modifiedSettings = new BiomeGenerationSettings(carvers, rebuilt);
		NTCommon.debug("applyPatches: biome {} modified — features now have {} steps",
			self.unwrapKey().map(Object::toString).orElse("<unkeyed>"), rebuilt.size());
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
