package neoterra.world.worldgen.noise.function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;

public class CurveFunctions {
	private static int registeredCount = 0;

	public static void bootstrap() {
		NTCommon.debug("CurveFunctions.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		registeredCount = 0;
		register("interpolation", Interpolation.CODEC);
		register("scurve", SCurveFunction.CODEC);
		NTCommon.debug("CurveFunctions.bootstrap: registered {} curve function types in {} ms", registeredCount, System.currentTimeMillis() - t0);
	}

	public static CurveFunction scurve(float lower, float upper) {
		return new SCurveFunction(lower, upper);
	}

	private static void register(String name, MapCodec<? extends CurveFunction> value) {
		RegistryUtil.register(NTBuiltInRegistries.CURVE_FUNCTION_TYPE, name, value);
		registeredCount++;
	}
}
