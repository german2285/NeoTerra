package neoterra.data.worldgen.preset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.biomepatch.BiomeFeaturePatches;
import neoterra.data.worldgen.preset.biomepatch.PatchAdd;
import neoterra.data.worldgen.preset.biomepatch.PatchReplace;
import neoterra.data.worldgen.preset.settings.MiscellaneousSettings;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.biome.modifier.BiomeModifier;
import neoterra.world.worldgen.biome.modifier.BiomeModifiers;
import neoterra.world.worldgen.biome.modifier.Filter;
import neoterra.world.worldgen.biome.modifier.Order;

//TODO organize all of this stuff cause god damn
public class PresetBiomeModifierData {
	public static final ResourceKey<BiomeModifier> ADD_EROSION = createKey("add_erosion");
	public static final ResourceKey<BiomeModifier> ADD_SNOW_PROCESSING = createKey("add_snow_processing");
	public static final ResourceKey<BiomeModifier> ADD_SWAMP_SURFACE = createKey("add_swamp_surface");

	public static final ResourceKey<BiomeModifier> REPLACE_PLAINS_TREES = createKey("replace_plains_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FOREST_TREES = createKey("replace_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FLOWER_FOREST_TREES = createKey("replace_flower_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_BIRCH_TREES = createKey("replace_birch_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_DARK_FOREST_TREES = createKey("replace_dark_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SAVANNA_TREES = createKey("replace_savanna_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SWAMP_TREES = createKey("replace_swamp_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_MEADOW_TREES = createKey("replace_meadow_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FIR_TREES = createKey("replace_fir_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_WINDSWEPT_HILLS_FIR_TREES = createKey("replace_windswept_hills_fir_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_PINE_TREES = createKey("replace_pine_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SPRUCE_TREES = createKey("replace_spruce_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SPRUCE_TUNDRA_TREES = createKey("replace_spruce_tundra_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_REDWOOD_TREES = createKey("replace_redwood_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_JUNGLE_TREES = createKey("replace_jungle_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_JUNGLE_EDGE_TREES = createKey("replace_jungle_edge_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_BADLANDS_TREES = createKey("replace_badlands_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_WOODED_BADLANDS_TREES = createKey("replace_wooded_badlands_trees");

	public static final ResourceKey<BiomeModifier> ADD_MARSH_BUSH = createKey("add_marsh_bush");
	public static final ResourceKey<BiomeModifier> ADD_PLAINS_BUSH = createKey("add_plains_bush");
	public static final ResourceKey<BiomeModifier> ADD_STEPPE_BUSH = createKey("add_stepps_bush");
	public static final ResourceKey<BiomeModifier> ADD_COLD_STEPPE_BUSH = createKey("add_cold_steppe_bush");
	public static final ResourceKey<BiomeModifier> ADD_TAIGA_SCRUB_BUSH = createKey("add_taiga_scrub_bush");

	public static final ResourceKey<BiomeModifier> ADD_FOREST_GRASS = createKey("add_forest_grass");
	public static final ResourceKey<BiomeModifier> ADD_BIRCH_FOREST_GRASS = createKey("add_birch_forest_grass");

	public static void bootstrap(Preset preset, BootstrapContext<BiomeModifier> ctx) {
		HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);

		BiomeFeaturePatches patches = collectPatches(preset, placedFeatures, biomes);

		for (PatchAdd patch : patches.adds()) {
			ctx.register(patch.id(), toBiomeModifier(patch));
		}
		for (PatchReplace patch : patches.replaces()) {
			ctx.register(patch.id(), toBiomeModifier(patch));
		}
	}

	public static BiomeFeaturePatches collectPatches(Preset preset, HolderGetter<PlacedFeature> placedFeatures, HolderGetter<Biome> biomes) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();

		HolderSet<Biome> swamps = HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP));
		HolderSet<Biome> plains = HolderSet.direct(biomes.getOrThrow(Biomes.RIVER), biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS));
		HolderSet<Biome> forests = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST));
		HolderSet<Biome> flowerForests = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.FLOWER_FOREST));
		HolderSet<Biome> birchForests = HolderSet.direct(biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST));
		HolderSet<Biome> darkForests = HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST));
		HolderSet<Biome> savannas = HolderSet.direct(biomes.getOrThrow(Biomes.SAVANNA), biomes.getOrThrow(Biomes.SAVANNA_PLATEAU));
		HolderSet<Biome> meadows = HolderSet.direct(biomes.getOrThrow(Biomes.MEADOW));
		HolderSet<Biome> firForests = HolderSet.direct(biomes.getOrThrow(Biomes.GROVE), biomes.getOrThrow(Biomes.WINDSWEPT_FOREST));
		HolderSet<Biome> windsweptHills = HolderSet.direct(biomes.getOrThrow(Biomes.WINDSWEPT_HILLS), biomes.getOrThrow(Biomes.WINDSWEPT_GRAVELLY_HILLS));
		HolderSet<Biome> pineForests = HolderSet.direct(biomes.getOrThrow(Biomes.TAIGA), biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA));
		HolderSet<Biome> spruceForests = HolderSet.direct(biomes.getOrThrow(Biomes.SNOWY_TAIGA));
		HolderSet<Biome> spruceTundras = HolderSet.direct(biomes.getOrThrow(Biomes.SNOWY_PLAINS));
		HolderSet<Biome> redwoodForests = HolderSet.direct(biomes.getOrThrow(Biomes.OLD_GROWTH_PINE_TAIGA));
		HolderSet<Biome> jungles = HolderSet.direct(biomes.getOrThrow(Biomes.JUNGLE), biomes.getOrThrow(Biomes.BAMBOO_JUNGLE));
		HolderSet<Biome> jungleEdges = HolderSet.direct(biomes.getOrThrow(Biomes.SPARSE_JUNGLE));
		HolderSet<Biome> badlands = HolderSet.direct(biomes.getOrThrow(Biomes.WINDSWEPT_SAVANNA));
		HolderSet<Biome> woodedBadlands = HolderSet.direct(biomes.getOrThrow(Biomes.WOODED_BADLANDS));

		HolderSet<Biome> marshBushBiomes = HolderSet.direct();
		HolderSet<Biome> plainsBushBiomes = HolderSet.direct(biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST), biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS), biomes.getOrThrow(Biomes.WINDSWEPT_HILLS), biomes.getOrThrow(Biomes.MEADOW));
		HolderSet<Biome> steppeBushBiomes = HolderSet.direct(biomes.getOrThrow(Biomes.SAVANNA), biomes.getOrThrow(Biomes.WINDSWEPT_SAVANNA), biomes.getOrThrow(Biomes.SAVANNA_PLATEAU));
		HolderSet<Biome> coldSteppeBiomes = HolderSet.direct();
		HolderSet<Biome> taigaScrubBiomes = HolderSet.direct(biomes.getOrThrow(Biomes.SNOWY_PLAINS), biomes.getOrThrow(Biomes.TAIGA), biomes.getOrThrow(Biomes.WINDSWEPT_FOREST), biomes.getOrThrow(Biomes.WINDSWEPT_GRAVELLY_HILLS));

		HolderSet<Biome> forestsWithGrass = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.DARK_FOREST));

		List<PatchAdd> adds = new ArrayList<>();
		List<PatchReplace> replaces = new ArrayList<>();

		if (miscellaneous.customBiomeFeatures) {
			Holder<PlacedFeature> plainsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PLAINS_TREES);
			Holder<PlacedFeature> forestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_TREES);
			Holder<PlacedFeature> flowerForestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FLOWER_FOREST_TREES);
			Holder<PlacedFeature> birchTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_TREES);
			Holder<PlacedFeature> darkForestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.DARK_FOREST_TREES);
			Holder<PlacedFeature> savannaTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SAVANNA_TREES);
			Holder<PlacedFeature> swampTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_TREES);
			Holder<PlacedFeature> meadowTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.MEADOW_TREES);
			Holder<PlacedFeature> firTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FIR_TREES);
			Holder<PlacedFeature> windsweptHillsFirTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.WINDSWEPT_HILLS_FIR_TREES);
			Holder<PlacedFeature> pineTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PINE_TREES);
			Holder<PlacedFeature> spruceTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_TREES);
			Holder<PlacedFeature> spruceTundraTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_TUNDRA_TREES);
			Holder<PlacedFeature> redwoodTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.REDWOOD_TREES);
			Holder<PlacedFeature> jungleTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_TREES);
			Holder<PlacedFeature> jungleEdgeTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_EDGE_TREES);
			Holder<PlacedFeature> badlandsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.BADLANDS_TREES);
			Holder<PlacedFeature> woodedBadlandsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.WOODED_BADLANDS_TREES);

			replaces.add(new PatchReplace(REPLACE_PLAINS_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(plains), Map.of(
				VegetationPlacements.TREES_PLAINS, plainsTrees
			)));
			replaces.add(new PatchReplace(REPLACE_FOREST_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(forests), Map.of(
				VegetationPlacements.TREES_BIRCH_AND_OAK, forestTrees
			)));
			replaces.add(new PatchReplace(REPLACE_FLOWER_FOREST_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(flowerForests), Map.of(
				VegetationPlacements.TREES_FLOWER_FOREST, flowerForestTrees
			)));
			replaces.add(new PatchReplace(REPLACE_BIRCH_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(birchForests), Map.of(
				VegetationPlacements.TREES_BIRCH, birchTrees,
				VegetationPlacements.BIRCH_TALL, birchTrees
			)));
			replaces.add(new PatchReplace(REPLACE_DARK_FOREST_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(darkForests), Map.of(
				VegetationPlacements.DARK_FOREST_VEGETATION, darkForestTrees
			)));
			replaces.add(new PatchReplace(REPLACE_SAVANNA_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(savannas), Map.of(
				VegetationPlacements.TREES_SAVANNA, savannaTrees
			)));
			replaces.add(new PatchReplace(REPLACE_SWAMP_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(swamps), Map.of(
				VegetationPlacements.TREES_SWAMP, swampTrees
			)));
			replaces.add(new PatchReplace(REPLACE_MEADOW_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(meadows), Map.of(
				VegetationPlacements.TREES_MEADOW, meadowTrees
			)));
			replaces.add(new PatchReplace(REPLACE_FIR_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(firForests), Map.of(
				VegetationPlacements.TREES_GROVE, firTrees,
				VegetationPlacements.TREES_WINDSWEPT_FOREST, firTrees
			)));
			replaces.add(new PatchReplace(REPLACE_WINDSWEPT_HILLS_FIR_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(windsweptHills), Map.of(
				VegetationPlacements.TREES_WINDSWEPT_HILLS, windsweptHillsFirTrees
			)));
			replaces.add(new PatchReplace(REPLACE_PINE_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(pineForests), Map.of(
				VegetationPlacements.TREES_TAIGA, pineTrees,
				VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA, pineTrees
			)));
			replaces.add(new PatchReplace(REPLACE_SPRUCE_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(spruceForests), Map.of(
				VegetationPlacements.TREES_TAIGA, spruceTrees
			)));
			replaces.add(new PatchReplace(REPLACE_SPRUCE_TUNDRA_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(spruceTundras), Map.of(
				VegetationPlacements.TREES_SNOWY, spruceTundraTrees
			)));
			replaces.add(new PatchReplace(REPLACE_REDWOOD_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(redwoodForests), Map.of(
				VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA, redwoodTrees
			)));
			replaces.add(new PatchReplace(REPLACE_JUNGLE_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(jungles), Map.of(
				VegetationPlacements.TREES_JUNGLE, jungleTrees,
				VegetationPlacements.BAMBOO_VEGETATION, jungleTrees
			)));
			replaces.add(new PatchReplace(REPLACE_JUNGLE_EDGE_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(jungleEdges), Map.of(
				VegetationPlacements.TREES_SPARSE_JUNGLE, jungleEdgeTrees
			)));
			replaces.add(new PatchReplace(REPLACE_BADLANDS_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(badlands), Map.of(
				VegetationPlacements.TREES_BADLANDS, badlandsTrees,
				VegetationPlacements.TREES_WINDSWEPT_SAVANNA, badlandsTrees
			)));
			replaces.add(new PatchReplace(REPLACE_WOODED_BADLANDS_TREES, GenerationStep.Decoration.VEGETAL_DECORATION, Optional.of(woodedBadlands), Map.of(
				VegetationPlacements.TREES_BADLANDS, woodedBadlandsTrees
			)));

			adds.add(new PatchAdd(ADD_MARSH_BUSH, Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION,
				Optional.of(new Filter(marshBushBiomes, Filter.Behavior.WHITELIST)),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.MARSH_BUSH))));
			adds.add(new PatchAdd(ADD_PLAINS_BUSH, Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION,
				Optional.of(new Filter(plainsBushBiomes, Filter.Behavior.WHITELIST)),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.PLAINS_BUSH))));
			adds.add(new PatchAdd(ADD_STEPPE_BUSH, Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION,
				Optional.of(new Filter(steppeBushBiomes, Filter.Behavior.WHITELIST)),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.STEPPE_BUSH))));
			adds.add(new PatchAdd(ADD_COLD_STEPPE_BUSH, Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION,
				Optional.of(new Filter(coldSteppeBiomes, Filter.Behavior.WHITELIST)),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.COLD_STEPPE_BUSH))));
			adds.add(new PatchAdd(ADD_TAIGA_SCRUB_BUSH, Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION,
				Optional.of(new Filter(taigaScrubBiomes, Filter.Behavior.WHITELIST)),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.TAIGA_SCRUB_BUSH))));

			adds.add(new PatchAdd(ADD_FOREST_GRASS, Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION,
				Optional.of(new Filter(forestsWithGrass, Filter.Behavior.WHITELIST)),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_GRASS))));
			adds.add(new PatchAdd(ADD_BIRCH_FOREST_GRASS, Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION,
				Optional.of(new Filter(birchForests, Filter.Behavior.WHITELIST)),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_FOREST_GRASS))));
		}

		if (miscellaneous.erosionDecorator) {
			adds.add(new PatchAdd(ADD_EROSION, Order.PREPEND, GenerationStep.Decoration.RAW_GENERATION,
				Optional.empty(),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.ERODE))));
		}
		if (miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			adds.add(new PatchAdd(ADD_SNOW_PROCESSING, Order.APPEND, GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
				Optional.empty(),
				HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.DECORATE_SNOW))));
		}

		adds.add(new PatchAdd(ADD_SWAMP_SURFACE, Order.PREPEND, GenerationStep.Decoration.RAW_GENERATION,
			Optional.of(new Filter(swamps, Filter.Behavior.WHITELIST)),
			HolderSet.direct(placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_SURFACE))));

		return new BiomeFeaturePatches(adds, replaces);
	}

	private static BiomeModifier toBiomeModifier(PatchAdd patch) {
		if (patch.filter().isPresent()) {
			Filter filter = patch.filter().get();
			return BiomeModifiers.add(patch.order(), patch.step(), filter.behavior(), filter.biomes(), patch.features());
		}
		return BiomeModifiers.add(patch.order(), patch.step(), patch.features());
	}

	private static BiomeModifier toBiomeModifier(PatchReplace patch) {
		return BiomeModifiers.replace(patch.step(), patch.biomes(), patch.replacements());
	}

	private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(NTRegistries.BIOME_MODIFIER, NTCommon.location(name));
	}
}
