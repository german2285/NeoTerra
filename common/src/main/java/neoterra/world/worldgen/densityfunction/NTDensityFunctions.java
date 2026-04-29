package neoterra.world.worldgen.densityfunction;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;
import neoterra.world.worldgen.noise.module.Noise;

public class NTDensityFunctions {
	private static int registeredCount = 0;

	public static void bootstrap() {
		NTCommon.debug("NTDensityFunctions.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		registeredCount = 0;
		register("noise", NoiseFunction.Marker.CODEC);
		register("cell", CellSampler.Marker.CODEC);
		register("clamp_to_nearest_unit", ClampToNearestUnit.CODEC);
		register("linear_spline", LinearSplineFunction.CODEC);
		NTCommon.debug("NTDensityFunctions.bootstrap: registered {} density function types in {} ms", registeredCount, System.currentTimeMillis() - t0);
	}
	
	public static NoiseFunction.Marker noise(Holder<Noise> noise) {
		return new NoiseFunction.Marker(noise);
	}
	
	public static CellSampler.Marker cell(CellSampler.Field field) {
		return new CellSampler.Marker(field);
	}
	
	public static ClampToNearestUnit clampToNearestUnit(DensityFunction function, int resolution) {
		return new ClampToNearestUnit(function, resolution);
	}
	
	private static void register(String name, MapCodec<? extends DensityFunction> type) {
		RegistryUtil.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, type);
		registeredCount++;
	}
}
