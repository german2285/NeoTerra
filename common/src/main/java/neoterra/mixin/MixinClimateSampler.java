package neoterra.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Climate;
import neoterra.world.worldgen.biome.NTClimateSampler;

@Mixin(Climate.Sampler.class)
@Implements(@Interface(iface = NTClimateSampler.class, prefix = "neoterra$NTClimateSampler$"))
class MixinClimateSampler {
	private BlockPos spawnSearchCenter = BlockPos.ZERO;
	
	public void neoterra$NTClimateSampler$setSpawnSearchCenter(BlockPos spawnSearchCenter) {
		this.spawnSearchCenter = spawnSearchCenter;
	}
	
	public BlockPos neoterra$NTClimateSampler$getSpawnSearchCenter() {
		return this.spawnSearchCenter;
	}
}
