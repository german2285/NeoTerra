package neoterra.fabric.mixin;

import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import neoterra.world.worldgen.biome.IInvalidatableFeaturesPerStep;
import neoterra.world.worldgen.biome.InvalidatableSupplier;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator implements IInvalidatableFeaturesPerStep {

	@Shadow @Final
	private Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;

	@WrapOperation(
		method = "<init>(Lnet/minecraft/world/level/biome/BiomeSource;Ljava/util/function/Function;)V",
		at = @At(value = "INVOKE",
			target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;",
			remap = false))
	private static <T> com.google.common.base.Supplier<T> neoterra$wrapMemoize(
			com.google.common.base.Supplier<T> arg, Operation<com.google.common.base.Supplier<T>> original) {
		return new InvalidatableSupplier<>(arg);
	}

	@Override
	public void neoterra$invalidateFeaturesPerStep() {
		if (this.featuresPerStep instanceof InvalidatableSupplier<?> inv) {
			inv.invalidate();
		}
	}
}
