package neoterra.fabric.biome;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.PresetBiomeModifierData;
import neoterra.data.worldgen.preset.biomepatch.BiomeFeaturePatches;
import neoterra.data.worldgen.preset.biomepatch.PatchAdd;
import neoterra.data.worldgen.preset.biomepatch.PatchReplace;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.biome.IInvalidatableFeaturesPerStep;
import neoterra.world.worldgen.biome.IModifiableBiome;

public final class FabricBiomeModifierApplier {

	public static void register() {
		NTCommon.debug("FabricBiomeModifierApplier: register() called, hooking ServerLifecycleEvents.SERVER_STARTING");
		ServerLifecycleEvents.SERVER_STARTING.register(FabricBiomeModifierApplier::onServerStarting);
	}

	private static void onServerStarting(MinecraftServer server) {
		NTCommon.debug("FabricBiomeModifierApplier: onServerStarting fired (server class={})", server.getClass().getName());
		RegistryAccess registries = server.registryAccess();

		Optional<HolderLookup.RegistryLookup<Preset>> presetLookup = registries.lookup(NTRegistries.PRESET);
		if (presetLookup.isEmpty()) {
			NTCommon.debug("FabricBiomeModifierApplier: PRESET registry absent, skipping");
			return;
		}
		long presetCount = presetLookup.get().listElements().count();
		NTCommon.debug("FabricBiomeModifierApplier: PRESET registry has {} entries", presetCount);

		Optional<Preset> active = presetLookup.get().get(Preset.KEY).map(Holder.Reference::value);
		if (active.isEmpty()) {
			NTCommon.debug("FabricBiomeModifierApplier: no active NeoTerra preset under key {}, skipping", Preset.KEY);
			return;
		}
		NTCommon.debug("FabricBiomeModifierApplier: active preset loaded, miscSettings={}", active.get().miscellaneous());

		HolderLookup.RegistryLookup<PlacedFeature> placedFeatures = registries.lookupOrThrow(Registries.PLACED_FEATURE);
		HolderLookup.RegistryLookup<Biome> biomes = registries.lookupOrThrow(Registries.BIOME);
		HolderSet<Biome> overworld = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
		NTCommon.debug("FabricBiomeModifierApplier: registries — placed_features={} biomes={} overworld_tag_size={}",
			placedFeatures.listElements().count(), biomes.listElements().count(), overworld.size());

		BiomeFeaturePatches patches = PresetBiomeModifierData.collectPatches(active.get(), placedFeatures, biomes);
		NTCommon.debug("FabricBiomeModifierApplier: collected {} adds and {} replaces", patches.adds().size(), patches.replaces().size());

		for (PatchReplace replace : patches.replaces()) {
			NTCommon.debug("  REPLACE id={} step={} biomes={} replacements={}",
				replace.id(),
				replace.step(),
				replace.biomes().map(s -> s.size() + " explicit").orElse("overworld fallback"),
				replace.replacements().size());
			replace.replacements().forEach((oldKey, newHolder) ->
				NTCommon.debug("    {} -> {}", oldKey.location(), newHolder.unwrapKey().map(k -> k.location().toString()).orElse("<direct>"))
			);
		}
		for (PatchAdd add : patches.adds()) {
			NTCommon.debug("  ADD id={} step={} order={} filter={} features={}",
				add.id(),
				add.step(),
				add.order(),
				add.filter().map(f -> f.behavior() + "(" + f.biomes().size() + ")").orElse("overworld fallback"),
				add.features().size());
		}

		AtomicInteger appliedCount = new AtomicInteger();
		AtomicInteger totalCount = new AtomicInteger();
		biomes.listElements().forEach(holder -> {
			Biome biome = holder.value();
			totalCount.incrementAndGet();
			Object castable = (Object) biome;
			if (!(castable instanceof IModifiableBiome modifiable)) {
				NTCommon.debug("FabricBiomeModifierApplier: biome {} is NOT IModifiableBiome — MixinBiome not applied!",
					holder.unwrapKey().map(Object::toString).orElse("<unkeyed>"));
				return;
			}
			modifiable.neoterra$applyPatches(patches, holder, overworld);
			appliedCount.incrementAndGet();
		});
		NTCommon.debug("FabricBiomeModifierApplier: ran applyPatches on {}/{} biomes",
			appliedCount.get(), totalCount.get());

		// Сбрасываем мемоизированный ChunkGenerator.featuresPerStep — иначе он
		// останется построенным по немодифицированным биомам, и applyBiomeDecoration
		// упадёт с IndexOutOfBoundsException на новых фичах.
		// На SERVER_STARTING server.getAllLevels() ещё пустой; ChunkGenerator'ы
		// доступны только через Registries.LEVEL_STEM.
		HolderLookup.RegistryLookup<LevelStem> stems = registries.lookupOrThrow(Registries.LEVEL_STEM);
		NTCommon.debug("FabricBiomeModifierApplier: LEVEL_STEM registry has {} entries", stems.listElements().count());
		AtomicInteger invalidated = new AtomicInteger();
		AtomicInteger notInvalidatable = new AtomicInteger();
		stems.listElements().forEach(stemHolder -> {
			LevelStem stem = stemHolder.value();
			Object generator = stem.generator();
			String stemKey = stemHolder.unwrapKey().map(k -> k.location().toString()).orElse("<unkeyed>");
			NTCommon.debug("FabricBiomeModifierApplier: levelStem {} → generator class {}",
				stemKey, generator.getClass().getName());
			if (generator instanceof IInvalidatableFeaturesPerStep inv) {
				inv.neoterra$invalidateFeaturesPerStep();
				invalidated.incrementAndGet();
			} else {
				NTCommon.debug("FabricBiomeModifierApplier: generator {} is NOT IInvalidatableFeaturesPerStep — MixinChunkGenerator not applied!",
					generator.getClass().getName());
				notInvalidatable.incrementAndGet();
			}
		});
		NTCommon.debug("FabricBiomeModifierApplier: DONE — invalidated featuresPerStep on {} generators ({} not invalidatable)",
			invalidated.get(), notInvalidatable.get());
	}

	private FabricBiomeModifierApplier() {}
}
