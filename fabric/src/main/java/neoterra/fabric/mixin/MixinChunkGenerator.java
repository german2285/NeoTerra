package neoterra.fabric.mixin;

import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.biome.FeatureSorter;
import neoterra.world.worldgen.biome.IInvalidatableFeaturesPerStep;
import neoterra.world.worldgen.biome.InvalidatableSupplier;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator implements IInvalidatableFeaturesPerStep {

	@Shadow @Final
	private Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;

	@ModifyExpressionValue(method = "<init>",
		at = @At(value = "INVOKE",
			target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;"))
	private com.google.common.base.Supplier<List<FeatureSorter.StepFeatureData>> neoterra$wrapMemoize(
			com.google.common.base.Supplier<List<FeatureSorter.StepFeatureData>> original) {
		return new InvalidatableSupplier<>(original);
	}

	@Override
	public void neoterra$invalidateFeaturesPerStep() {
		if (this.featuresPerStep instanceof InvalidatableSupplier<?> inv) {
			inv.invalidate();
		}
	}
}
