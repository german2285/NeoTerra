package neoterra.data.worldgen.preset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.biomepatch.BiomeFeaturePatches;
import neoterra.data.worldgen.preset.biomepatch.Filter;
import neoterra.data.worldgen.preset.biomepatch.Order;
import neoterra.data.worldgen.preset.biomepatch.PatchAdd;
import neoterra.data.worldgen.preset.biomepatch.PatchReplace;
import neoterra.data.worldgen.preset.settings.MiscellaneousSettings;
import neoterra.data.worldgen.preset.settings.Preset;

public class PresetBiomeModifierData {
	public static final ResourceLocation ADD_EROSION = id("add_erosion");
	public static final ResourceLocation ADD_SNOW_PROCESSING = id("add_snow_processing");
	public static final ResourceLocation ADD_SWAMP_SURFACE = id("add_swamp_surface");

	public static final ResourceLocation REPLACE_PLAINS_TREES = id("replace_plains_trees");
	public static final ResourceLocation REPLACE_FOREST_TREES = id("replace_forest_trees");
	public static final ResourceLocation REPLACE_FLOWER_FOREST_TREES = id("replace_flower_forest_trees");
	public static final ResourceLocation REPLACE_BIRCH_TREES = id("replace_birch_trees");
	public static final ResourceLocation REPLACE_DARK_FOREST_TREES = id("replace_dark_forest_trees");
	public static final ResourceLocation REPLACE_SAVANNA_TREES = id("replace_savanna_trees");
	public static final ResourceLocation REPLACE_SWAMP_TREES = id("replace_swamp_trees");
	public static final ResourceLocation REPLACE_MEADOW_TREES = id("replace_meadow_trees");
	public static final ResourceLocation REPLACE_FIR_TREES = id("replace_fir_trees");
	public static final ResourceLocation REPLACE_WINDSWEPT_HILLS_FIR_TREES = id("replace_windswept_hills_fir_trees");
	public static final ResourceLocation REPLACE_PINE_TREES = id("replace_pine_trees");
	public static final ResourceLocation REPLACE_SPRUCE_TREES = id("replace_spruce_trees");
	public static final ResourceLocation REPLACE_SPRUCE_TUNDRA_TREES = id("replace_spruce_tundra_trees");
	public static final ResourceLocation REPLACE_REDWOOD_TREES = id("replace_redwood_trees");
	public static final ResourceLocation REPLACE_JUNGLE_TREES = id("replace_jungle_trees");
	public static final ResourceLocation REPLACE_JUNGLE_EDGE_TREES = id("replace_jungle_edge_trees");
	public static final ResourceLocation REPLACE_BADLANDS_TREES = id("replace_badlands_trees");
	public static final ResourceLocation REPLACE_WOODED_BADLANDS_TREES = id("replace_wooded_badlands_trees");

	public static final ResourceLocation ADD_MARSH_BUSH = id("add_marsh_bush");
	public static final ResourceLocation ADD_PLAINS_BUSH = id("add_plains_bush");
	public static final ResourceLocation ADD_STEPPE_BUSH = id("add_stepps_bush");
	public static final ResourceLocation ADD_COLD_STEPPE_BUSH = id("add_cold_steppe_bush");
	public static final ResourceLocation ADD_TAIGA_SCRUB_BUSH = id("add_taiga_scrub_bush");

	public static final ResourceLocation ADD_FOREST_GRASS = id("add_forest_grass");
	public static final ResourceLocation ADD_BIRCH_FOREST_GRASS = id("add_birch_forest_grass");

	public static BiomeFeaturePatches collectPatches(Preset preset, HolderGetter<PlacedFeature> placedFeatures, HolderGetter<Biome> biomes) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		NTCommon.debug("collectPatches: customBiomeFeatures={}, erosionDecorator={}, naturalSnowDecorator={}, smoothLayerDecorator={}",
			miscellaneous.customBiomeFeatures, miscellaneous.erosionDecorator,
			miscellaneous.naturalSnowDecorator, miscellaneous.smoothLayerDecorator);
		if(!miscellaneous.customBiomeFeatures) {
			NTCommon.debug("collectPatches: customBiomeFeatures=false -> skipping {} replace patches (plains/forest/flower_forest/birch/dark_forest/savanna/swamp/meadow/fir/windswept_hills_fir/pine/spruce/spruce_tundra/redwood/jungle/jungle_edge/badlands/wooded_badlands trees) and {} add patches (marsh/plains/steppe/cold_steppe/taiga_scrub bush, forest/birch_forest grass)", 18, 7);
		}
		if(!miscellaneous.erosionDecorator) {
			NTCommon.debug("collectPatches: erosionDecorator=false -> skipping ADD_EROSION patch");
		}
		if(!(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator)) {
			NTCommon.debug("collectPatches: naturalSnowDecorator=false && smoothLayerDecorator=false -> skipping ADD_SNOW_PROCESSING patch");
		}

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

		NTCommon.debug("collectPatches: produced {} adds, {} replaces", adds.size(), replaces.size());
		return new BiomeFeaturePatches(adds, replaces);
	}

	private static ResourceLocation id(String name) {
		return NTCommon.location(name);
	}
}
