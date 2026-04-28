package neoterra.world.worldgen.biome.modifier;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.registries.NTBuiltInRegistries;

// theres other worldgen libraries we can use for this that aren't so janky
@Deprecated(forRemoval = true)
public interface BiomeModifier {
    public static final Codec<BiomeModifier> CODEC = NTBuiltInRegistries.BIOME_MODIFIER_TYPE.byNameCodec().dispatch(BiomeModifier::codec, Function.identity());
	
	MapCodec<? extends BiomeModifier> codec();
}
