package neoterra.world.worldgen.biome;

import net.minecraft.core.BlockPos;

public interface NTClimateSampler {
	void setSpawnSearchCenter(BlockPos center);
	
	BlockPos getSpawnSearchCenter();
}
