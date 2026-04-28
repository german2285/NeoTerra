package neoterra.data.worldgen.preset;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.cell.terrain.TerrainType;
import neoterra.world.worldgen.structure.rule.StructureRule;
import neoterra.world.worldgen.structure.rule.StructureRules;

public class PresetStructureRuleData {
	public static final ResourceKey<StructureRule> CELL_TEST = createKey("cell_test");
	
	public static void bootstrap(Preset preset, BootstrapContext<StructureRule> ctx) {
		ctx.register(CELL_TEST, StructureRules.cellTest(0.225F, TerrainType.MOUNTAIN_CHAIN, TerrainType.MOUNTAINS_1, TerrainType.MOUNTAINS_2, TerrainType.MOUNTAINS_3));
	}
	
	private static ResourceKey<StructureRule> createKey(String name) {
        return ResourceKey.create(NTRegistries.STRUCTURE_RULE, NTCommon.location(name));
	}
}
