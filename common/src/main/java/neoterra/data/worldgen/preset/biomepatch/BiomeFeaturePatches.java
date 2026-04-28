package neoterra.data.worldgen.preset.biomepatch;

import java.util.List;

public record BiomeFeaturePatches(List<PatchAdd> adds, List<PatchReplace> replaces) {
	public BiomeFeaturePatches {
		adds = List.copyOf(adds);
		replaces = List.copyOf(replaces);
	}
}
