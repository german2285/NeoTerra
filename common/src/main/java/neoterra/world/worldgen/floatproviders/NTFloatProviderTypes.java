package neoterra.world.worldgen.floatproviders;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;

public class NTFloatProviderTypes {
	public static final FloatProviderType<LegacyCanyonYScale> LEGACY_CANYON_Y_SCALE = register("legacy_canyon_y_scale", LegacyCanyonYScale.CODEC);

	public static void bootstrap() {
		NTCommon.debug("NTFloatProviderTypes.bootstrap: starting (1 float provider type registered via static init: legacy_canyon_y_scale)");
	}
	
	private static <T extends FloatProvider> FloatProviderType<T> register(String name, MapCodec<T> codec) {
		FloatProviderType<T> type = () -> codec;
		RegistryUtil.register(BuiltInRegistries.FLOAT_PROVIDER_TYPE, name, type);
		return type;
	}
}
