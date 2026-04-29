package neoterra.fabric.biome;

import java.util.Map;
import java.util.Set;

import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.PresetPlacedFeatures;

// Заменяет рефлексионный MixinBiomeModificationImpl, который раньше читал кастомный
// forge:biome_modifier registry и пропихивал модификаторы в BiomeModificationImpl$ModifierRecord
// через Constructor.newInstance. Теперь — официальный Fabric BiomeModifications API.
//
// Trade-off'ы (подробнее в TODO.md, Этап 3):
// — Регистрация в onInitialize, до загрузки datapack'а с пресетом, поэтому используется
//   статический набор модификаторов NT default preset; флаги MiscellaneousSettings
//   (customBiomeFeatures, naturalSnowDecorator и т.д.) больше не отключают модификаторы.
// — Fabric BiomeModifications кладёт фичи в конец фазы; Order.PREPEND теряется.
// — Fabric API не даёт BiomeSelectionContext доступ к placed feature registry, поэтому
//   активность NT-датапака определяется через proxy `hasPlacedFeature(ERODE)` в фазе
//   REPLACEMENTS (фаза ADDITIONS уже отработала, и если NT активна, add_erosion
//   успел положить ERODE в биом). Для ADDITIONS — try/catch вокруг addFeature
//   (там нет destructive remove перед добавлением, так что промах в catch безопасен).
//   Без этого checking'а в фазе REPLACEMENTS removeFeature(step, vanillaKey) удалял
//   бы ванильные фичи в любом мире с установленным модом, даже без NT-датапака.
public final class FabricBiomePatches {
	private static final GenerationStep.Decoration VEG = GenerationStep.Decoration.VEGETAL_DECORATION;
	private static final GenerationStep.Decoration RAW = GenerationStep.Decoration.RAW_GENERATION;
	private static final GenerationStep.Decoration TOP = GenerationStep.Decoration.TOP_LAYER_MODIFICATION;

	public static void register() {
		NTCommon.LOGGER.debug("FabricBiomePatches.register: registering replace and add modifiers via Fabric BiomeModifications API");
		registerReplaces();
		registerAdds();
		NTCommon.LOGGER.debug("FabricBiomePatches.register: done");
	}

	private static void registerReplaces() {
		replace("replace_plains_trees", VEG, Set.of(Biomes.RIVER, Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS),
			Map.of(VegetationPlacements.TREES_PLAINS, PresetPlacedFeatures.PLAINS_TREES));
		replace("replace_forest_trees", VEG, Set.of(Biomes.FOREST),
			Map.of(VegetationPlacements.TREES_BIRCH_AND_OAK, PresetPlacedFeatures.FOREST_TREES));
		replace("replace_flower_forest_trees", VEG, Set.of(Biomes.FOREST, Biomes.FLOWER_FOREST),
			Map.of(VegetationPlacements.TREES_FLOWER_FOREST, PresetPlacedFeatures.FLOWER_FOREST_TREES));
		replace("replace_birch_trees", VEG, Set.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST),
			Map.of(VegetationPlacements.TREES_BIRCH, PresetPlacedFeatures.BIRCH_TREES,
				VegetationPlacements.BIRCH_TALL, PresetPlacedFeatures.BIRCH_TREES));
		replace("replace_dark_forest_trees", VEG, Set.of(Biomes.DARK_FOREST),
			Map.of(VegetationPlacements.DARK_FOREST_VEGETATION, PresetPlacedFeatures.DARK_FOREST_TREES));
		replace("replace_savanna_trees", VEG, Set.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU),
			Map.of(VegetationPlacements.TREES_SAVANNA, PresetPlacedFeatures.SAVANNA_TREES));
		replace("replace_swamp_trees", VEG, Set.of(Biomes.SWAMP),
			Map.of(VegetationPlacements.TREES_SWAMP, PresetPlacedFeatures.SWAMP_TREES));
		replace("replace_meadow_trees", VEG, Set.of(Biomes.MEADOW),
			Map.of(VegetationPlacements.TREES_MEADOW, PresetPlacedFeatures.MEADOW_TREES));
		replace("replace_fir_trees", VEG, Set.of(Biomes.GROVE, Biomes.WINDSWEPT_FOREST),
			Map.of(VegetationPlacements.TREES_GROVE, PresetPlacedFeatures.FIR_TREES,
				VegetationPlacements.TREES_WINDSWEPT_FOREST, PresetPlacedFeatures.FIR_TREES));
		replace("replace_windswept_hills_fir_trees", VEG, Set.of(Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS),
			Map.of(VegetationPlacements.TREES_WINDSWEPT_HILLS, PresetPlacedFeatures.WINDSWEPT_HILLS_FIR_TREES));
		replace("replace_pine_trees", VEG, Set.of(Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
			Map.of(VegetationPlacements.TREES_TAIGA, PresetPlacedFeatures.PINE_TREES,
				VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA, PresetPlacedFeatures.PINE_TREES));
		replace("replace_spruce_trees", VEG, Set.of(Biomes.SNOWY_TAIGA),
			Map.of(VegetationPlacements.TREES_TAIGA, PresetPlacedFeatures.SPRUCE_TREES));
		replace("replace_spruce_tundra_trees", VEG, Set.of(Biomes.SNOWY_PLAINS),
			Map.of(VegetationPlacements.TREES_SNOWY, PresetPlacedFeatures.SPRUCE_TUNDRA_TREES));
		replace("replace_redwood_trees", VEG, Set.of(Biomes.OLD_GROWTH_PINE_TAIGA),
			Map.of(VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA, PresetPlacedFeatures.REDWOOD_TREES));
		replace("replace_jungle_trees", VEG, Set.of(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE),
			Map.of(VegetationPlacements.TREES_JUNGLE, PresetPlacedFeatures.JUNGLE_TREES,
				VegetationPlacements.BAMBOO_VEGETATION, PresetPlacedFeatures.JUNGLE_TREES));
		replace("replace_jungle_edge_trees", VEG, Set.of(Biomes.SPARSE_JUNGLE),
			Map.of(VegetationPlacements.TREES_SPARSE_JUNGLE, PresetPlacedFeatures.JUNGLE_EDGE_TREES));
		replace("replace_badlands_trees", VEG, Set.of(Biomes.WINDSWEPT_SAVANNA),
			Map.of(VegetationPlacements.TREES_BADLANDS, PresetPlacedFeatures.BADLANDS_TREES,
				VegetationPlacements.TREES_WINDSWEPT_SAVANNA, PresetPlacedFeatures.BADLANDS_TREES));
		replace("replace_wooded_badlands_trees", VEG, Set.of(Biomes.WOODED_BADLANDS),
			Map.of(VegetationPlacements.TREES_BADLANDS, PresetPlacedFeatures.WOODED_BADLANDS_TREES));
	}

	private static void registerAdds() {
		// add_marsh_bush: empty WHITELIST → не применяется ни к одному биому, в legacy коде это disabled-модификатор. Пропускаем.
		add("add_plains_bush", VEG, Set.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.PLAINS,
			Biomes.SUNFLOWER_PLAINS, Biomes.WINDSWEPT_HILLS, Biomes.MEADOW), PresetPlacedFeatures.PLAINS_BUSH);
		add("add_steppe_bush", VEG, Set.of(Biomes.SAVANNA, Biomes.WINDSWEPT_SAVANNA, Biomes.SAVANNA_PLATEAU),
			PresetPlacedFeatures.STEPPE_BUSH);
		// add_cold_steppe_bush: empty WHITELIST → пропускаем.
		add("add_taiga_scrub_bush", VEG, Set.of(Biomes.SNOWY_PLAINS, Biomes.TAIGA, Biomes.WINDSWEPT_FOREST,
			Biomes.WINDSWEPT_GRAVELLY_HILLS), PresetPlacedFeatures.TAIGA_SCRUB_BUSH);
		add("add_forest_grass", VEG, Set.of(Biomes.FOREST, Biomes.DARK_FOREST), PresetPlacedFeatures.FOREST_GRASS);
		add("add_birch_forest_grass", VEG, Set.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST),
			PresetPlacedFeatures.BIRCH_FOREST_GRASS);

		addOverworld("add_erosion", RAW, PresetPlacedFeatures.ERODE);
		addOverworld("add_snow_processing", TOP, PresetPlacedFeatures.DECORATE_SNOW);

		add("add_swamp_surface", RAW, Set.of(Biomes.SWAMP), PresetPlacedFeatures.SWAMP_SURFACE);
	}

	private static void replace(String id, GenerationStep.Decoration step, Set<ResourceKey<Biome>> biomes,
			Map<ResourceKey<PlacedFeature>, ResourceKey<PlacedFeature>> replacements) {
		BiomeModifications.create(NTCommon.location(id))
			.add(ModificationPhase.REPLACEMENTS,
				BiomeSelectors.includeByKey(biomes),
				(selCtx, modCtx) -> {
					if (!isNeoTerraActive(selCtx)) return;
					BiomeModificationContext.GenerationSettingsContext gen = modCtx.getGenerationSettings();
					replacements.forEach((oldKey, newKey) -> {
						if (gen.removeFeature(step, oldKey)) {
							gen.addFeature(step, newKey);
						}
					});
				});
	}

	private static boolean isNeoTerraActive(BiomeSelectionContext selCtx) {
		return selCtx.hasPlacedFeature(PresetPlacedFeatures.ERODE);
	}

	private static void add(String id, GenerationStep.Decoration step, Set<ResourceKey<Biome>> biomes,
			ResourceKey<PlacedFeature> feature) {
		BiomeModifications.create(NTCommon.location(id))
			.add(ModificationPhase.ADDITIONS,
				BiomeSelectors.includeByKey(biomes),
				(selCtx, modCtx) -> safeAddFeature(modCtx, step, feature));
	}

	private static void addOverworld(String id, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature) {
		BiomeModifications.create(NTCommon.location(id))
			.add(ModificationPhase.ADDITIONS,
				BiomeSelectors.foundInOverworld(),
				(selCtx, modCtx) -> safeAddFeature(modCtx, step, feature));
	}

	private static void safeAddFeature(BiomeModificationContext modCtx, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature) {
		try {
			modCtx.getGenerationSettings().addFeature(step, feature);
		} catch (IllegalArgumentException e) {
			// NT placed feature ResourceKey is not registered → datapack not active.
		}
	}

	private FabricBiomePatches() {}
}
