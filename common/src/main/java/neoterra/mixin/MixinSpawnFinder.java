package neoterra.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.Sampler;
import neoterra.NTCommon;
import neoterra.world.worldgen.cell.biome.spawn.NTSpawnFinder;

@Mixin(Climate.class)
class MixinSpawnFinder {

	@Inject(at = @At("HEAD"), method = "findSpawnPosition", cancellable = true)
    private static void findSpawnPosition(List<ParameterPoint> list, Sampler sampler, CallbackInfoReturnable<BlockPos> callback) {
    	NTCommon.debug("MixinSpawnFinder.findSpawnPosition: redirecting to NTSpawnFinder over {} parameter points", list.size());
    	BlockPos result = new NTSpawnFinder(list, sampler).result.location();
    	NTCommon.debug("MixinSpawnFinder.findSpawnPosition: NTSpawnFinder result = {}", result);
    	callback.setReturnValue(result);
    }
}
