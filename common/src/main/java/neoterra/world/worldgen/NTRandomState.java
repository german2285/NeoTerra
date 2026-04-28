package neoterra.world.worldgen;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.DensityFunction;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.world.worldgen.noise.module.Noise;

public interface NTRandomState {
	void initialize(RegistryAccess registries);
	
	@Nullable
	Preset preset();

	@Nullable
	GeneratorContext generatorContext();
	
	DensityFunction wrap(DensityFunction function);

	Noise seed(Noise noise);
}
