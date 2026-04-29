package neoterra.world.worldgen.heightproviders;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;

public class NTHeightProviderTypes {
	public static final HeightProviderType<LegacyCarverHeight> LEGACY_CARVER = register("legacy_carver", LegacyCarverHeight.CODEC);

	public static void bootstrap() {
		NTCommon.debug("NTHeightProviderTypes.bootstrap: starting (1 height provider type registered via static init: legacy_carver)");
	}
	
	private static <T extends HeightProvider> HeightProviderType<T> register(String name, MapCodec<T> codec) {
		HeightProviderType<T> type = () -> codec;
		RegistryUtil.register(BuiltInRegistries.HEIGHT_PROVIDER_TYPE, name, type);
		return type;
	}
}
