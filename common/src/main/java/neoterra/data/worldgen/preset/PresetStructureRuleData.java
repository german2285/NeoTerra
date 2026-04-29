package neoterra.data.worldgen.preset;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.registries.NTRegistries;
import neoterra.tags.NTStructureTags;
import neoterra.world.worldgen.cell.terrain.TerrainType;
import neoterra.world.worldgen.structure.rule.StructureRule;
import neoterra.world.worldgen.structure.rule.StructureRules;

public class PresetStructureRuleData {
	public static final ResourceKey<StructureRule> CELL_TEST = createKey("cell_test");

	public static void bootstrap(Preset preset, BootstrapContext<StructureRule> ctx) {
		NTCommon.debug("PresetStructureRuleData.bootstrap: starting");
		HolderGetter<Structure> structures = ctx.lookup(Registries.STRUCTURE);
		HolderSet<Structure> applyTo = structures.getOrThrow(NTStructureTags.APPLY_CELL_TEST);
		ctx.register(CELL_TEST, StructureRules.cellTest(applyTo, 0.225F, TerrainType.MOUNTAIN_CHAIN, TerrainType.MOUNTAINS_1, TerrainType.MOUNTAINS_2, TerrainType.MOUNTAINS_3));
		NTCommon.debug("PresetStructureRuleData.bootstrap: registered 1 structure rule (cell_test) applied to #neoterra:apply_cell_test");
	}

	private static ResourceKey<StructureRule> createKey(String name) {
        return ResourceKey.create(NTRegistries.STRUCTURE_RULE, NTCommon.location(name));
	}
}
