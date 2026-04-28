package neoterra.world.worldgen.biome.modifier;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.registries.NTBuiltInRegistries;

// TODO миграция на нативные API: NeoForge BiomeModifier + Fabric BiomeModifications.
// План в TODO.md в корне репо. Главная причина — рефлексионный mixin
// MixinBiomeModificationImpl на Fabric. На NeoForge кастомные impl уже являются
// нативными NeoForge BiomeModifier'ами и работают штатно.
public interface BiomeModifier {
    public static final Codec<BiomeModifier> CODEC = NTBuiltInRegistries.BIOME_MODIFIER_TYPE.byNameCodec().dispatch(BiomeModifier::codec, Function.identity());
	
	MapCodec<? extends BiomeModifier> codec();
}
