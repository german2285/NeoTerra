package neoterra.neoforge.mixin;

import java.util.concurrent.CompletableFuture;

import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.levelgen.RandomState;
import neoterra.NTCommon;
import neoterra.world.worldgen.GeneratorContext;
import neoterra.world.worldgen.NTRandomState;
import net.minecraft.world.level.chunk.status.WorldGenContext;

@Mixin(ChunkStatusTasks.class)
public class MixinChunkStatusTasks {
	@Unique
	private static boolean neoterra$logged_generateStructureStarts;
	@Unique
	private static boolean neoterra$logged_generateFeatures;

	@Inject(
		at = @At("HEAD"),
		method = { "generateStructureStarts" },
		require = 1
	)
	private static void generateStructureStarts(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> staticCache2D, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> callback) {
		RandomState randomState = worldGenContext.level().getChunkSource().randomState();
		if((Object) randomState instanceof NTRandomState rtfRandomState) {
			ChunkPos chunkPos = chunkAccess.getPos();
			@org.jetbrains.annotations.Nullable
			GeneratorContext context = rtfRandomState.generatorContext();

			if(context != null) {
				if(!neoterra$logged_generateStructureStarts) {
					neoterra$logged_generateStructureStarts = true;
					NTCommon.debug("NeoForge MixinChunkStatusTasks.generateStructureStarts: first call queueing tile at chunk ({}, {})", chunkPos.x, chunkPos.z);
				}
				context.cache.queueAtChunk(chunkPos.x, chunkPos.z);
			}
		}
	}

	@Inject(
		at = @At("TAIL"),
		method = { "generateFeatures" },
		require = 1
	)
	private static void generateFeatures(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> staticCache2D, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> callback) {
		RandomState randomState = worldGenContext.level().getChunkSource().randomState();
		if((Object) randomState instanceof NTRandomState rtfRandomState) {
			ChunkPos chunkPos = chunkAccess.getPos();
			@Nullable
			GeneratorContext context = rtfRandomState.generatorContext();

			if(context != null) {
				if(!neoterra$logged_generateFeatures) {
					neoterra$logged_generateFeatures = true;
					NTCommon.debug("NeoForge MixinChunkStatusTasks.generateFeatures: first call dropping tile at chunk ({}, {})", chunkPos.x, chunkPos.z);
				}
				context.cache.dropAtChunk(chunkPos.x, chunkPos.z);
			}
		}
	}
}
