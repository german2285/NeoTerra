package neoterra.data.worldgen.preset.biomepatch;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

public record Filter(HolderSet<Biome> biomes, Behavior behavior) {

	public enum Behavior {
		WHITELIST,
		BLACKLIST;
	}
}
