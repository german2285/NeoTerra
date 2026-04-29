package neoterra.world.worldgen.structure.rule;

import java.util.Optional;

import com.google.common.collect.ImmutableSet;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.structure.Structure;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;
import neoterra.world.worldgen.cell.terrain.Terrain;

public class StructureRules {
	private static int registeredCount = 0;

	public static void bootstrap() {
		NTCommon.debug("StructureRules.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		registeredCount = 0;
		register("cell_test", CellTest.CODEC);
		NTCommon.debug("StructureRules.bootstrap: registered {} structure rule types in {} ms", registeredCount, System.currentTimeMillis() - t0);
	}

	public static CellTest cellTest(HolderSet<Structure> structures, float cutoff, Terrain... terrainTypeBlacklist) {
		return new CellTest(Optional.of(structures), cutoff, ImmutableSet.copyOf(terrainTypeBlacklist));
	}

	private static void register(String name, MapCodec<? extends StructureRule> value) {
		RegistryUtil.register(NTBuiltInRegistries.STRUCTURE_RULE_TYPE, name, value);
		registeredCount++;
	}
}
