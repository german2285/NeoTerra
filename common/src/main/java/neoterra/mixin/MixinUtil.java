package neoterra.mixin;

import java.util.concurrent.ExecutorService;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.Util;
import neoterra.NTCommon;
import neoterra.concurrent.ThreadPools;
import neoterra.concurrent.cache.Cache;

@Mixin(Util.class)
public class MixinUtil {

	@Inject(method = "shutdownExecutors()V", at = @At("TAIL"))
	private static void shutdownExecutors(CallbackInfo callback) {
		NTCommon.debug("MixinUtil.shutdownExecutors: shutting down ThreadPools.WORLD_GEN and Cache.SCHEDULER");
		shutdownExecutor(ThreadPools.WORLD_GEN);
		shutdownExecutor(Cache.SCHEDULER);
	}

	@Shadow
    private static void shutdownExecutor(ExecutorService executorService) {
    	throw new UnsupportedOperationException();
    }
}
