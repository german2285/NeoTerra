package neoterra.world.worldgen.feature.chance;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;

public class NTChanceModifiers {

	public static void bootstrap() {
		register("elevation", ElevationChanceModifier.CODEC);
		register("biome_edge", BiomeEdgeChanceModifier.CODEC);
	}
	
	public static ElevationChanceModifier elevation(float from, float to) {
		return elevation(from, to, false);
	}
	
	public static ElevationChanceModifier elevation(float from, float to, boolean exclusive) {
		return new ElevationChanceModifier(from, to, exclusive);
	}
	
	public static BiomeEdgeChanceModifier biomeEdge(float from, float to) {
		return biomeEdge(from, to, false);
	}
	
	public static BiomeEdgeChanceModifier biomeEdge(float from, float to, boolean exclusive) {
		return new BiomeEdgeChanceModifier(from, to, exclusive);
	}
	
	private static void register(String name, MapCodec<? extends ChanceModifier> placement) {
		RegistryUtil.register(NTBuiltInRegistries.CHANCE_MODIFIER_TYPE, name, placement);
	}
}
