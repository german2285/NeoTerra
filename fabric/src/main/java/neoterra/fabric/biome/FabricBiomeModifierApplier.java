package neoterra.fabric.biome;

import java.util.Optional;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.PresetBiomeModifierData;
import neoterra.data.worldgen.preset.biomepatch.BiomeFeaturePatches;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.biome.IInvalidatableFeaturesPerStep;
import neoterra.world.worldgen.biome.IModifiableBiome;

public final class FabricBiomeModifierApplier {

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTING.register(FabricBiomeModifierApplier::onServerStarting);
	}

	private static void onServerStarting(MinecraftServer server) {
		RegistryAccess registries = server.registryAccess();

		Optional<HolderLookup.RegistryLookup<Preset>> presetLookup = registries.lookup(NTRegistries.PRESET);
		if (presetLookup.isEmpty()) {
			NTCommon.debug("FabricBiomeModifierApplier: PRESET registry absent, skipping");
			return;
		}
		Optional<Preset> active = presetLookup.get().get(Preset.KEY).map(Holder.Reference::value);
		if (active.isEmpty()) {
			NTCommon.debug("FabricBiomeModifierApplier: no active NeoTerra preset, skipping");
			return;
		}

		HolderLookup.RegistryLookup<PlacedFeature> placedFeatures = registries.lookupOrThrow(Registries.PLACED_FEATURE);
		HolderLookup.RegistryLookup<Biome> biomes = registries.lookupOrThrow(Registries.BIOME);
		HolderSet<Biome> overworld = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

		BiomeFeaturePatches patches = PresetBiomeModifierData.collectPatches(active.get(), placedFeatures, biomes);
		NTCommon.debug("FabricBiomeModifierApplier: applying {} adds and {} replaces",
			patches.adds().size(), patches.replaces().size());

		biomes.listElements().forEach(holder -> {
			Biome biome = holder.value();
			((IModifiableBiome) (Object) biome).neoterra$applyPatches(patches, holder, overworld);
		});

		// Сбрасываем мемоизированный ChunkGenerator.featuresPerStep — иначе он
		// останется построенным по немодифицированным биомам, и applyBiomeDecoration
		// упадёт с IndexOutOfBoundsException на новых фичах.
		final int[] invalidated = {0};
		final int[] notInvalidatable = {0};
		server.getAllLevels().forEach(level -> {
			Object generator = level.getChunkSource().getGenerator();
			if (generator instanceof IInvalidatableFeaturesPerStep inv) {
				inv.neoterra$invalidateFeaturesPerStep();
				invalidated[0]++;
			} else {
				NTCommon.debug("FabricBiomeModifierApplier: generator {} is not IInvalidatableFeaturesPerStep, mixin not applied?",
					generator.getClass().getName());
				notInvalidatable[0]++;
			}
		});
		NTCommon.debug("FabricBiomeModifierApplier: invalidated featuresPerStep on {} generators ({} not invalidatable)",
			invalidated[0], notInvalidatable[0]);
	}

	private FabricBiomeModifierApplier() {}
}
