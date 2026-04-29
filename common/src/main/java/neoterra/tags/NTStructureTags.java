package neoterra.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import neoterra.NTCommon;

public final class NTStructureTags {
	public static final TagKey<Structure> APPLY_CELL_TEST = resolve("apply_cell_test");

	private static TagKey<Structure> resolve(String path) {
		return TagKey.create(Registries.STRUCTURE, NTCommon.location(path));
	}
}
