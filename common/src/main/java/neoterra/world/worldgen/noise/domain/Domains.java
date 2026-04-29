package neoterra.world.worldgen.noise.domain;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.noise.module.Noises;

public class Domains {
	private static int registeredCount = 0;

	public static void bootstrap() {
		NTCommon.debug("Domains.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		registeredCount = 0;
		register("domain", DomainWarp.CODEC);
		register("direction", DirectionWarp.CODEC);
		register("compound", CompoundWarp.CODEC);
		register("add", AddWarp.CODEC);
		register("direct", DirectWarp.CODEC);
		NTCommon.debug("Domains.bootstrap: registered {} domain types in {} ms", registeredCount, System.currentTimeMillis() - t0);
	}

	public static Domain domainPerlin(int seed, int scale, int octaves, float strength) {
		return domain(
			Noises.perlin(seed, scale, octaves), 
			Noises.perlin(seed + 1, scale, octaves), 
			Noises.constant(strength)
		);
	}
	
	public static Domain domainSimplex(int seed, int scale, int octaves, float strength) {
		return domain(
			Noises.simplex(seed, scale, octaves), 
			Noises.simplex(seed + 1, scale, octaves), 
			Noises.constant(strength)
		);
	}
	
	public static Domain domain(Noise x, Noise z, Noise distance) {
		return new DomainWarp(x, z, distance);
	}
	
	public static Domain direction(Noise direction, Noise distance) {
		return new DirectionWarp(direction, distance);
	}
	
	public static Domain compound(Domain input1, Domain input2) {
		return new CompoundWarp(input1, input2);
	}
	
	public static Domain add(Domain input1, Domain input2) {
		return new AddWarp(input1, input2);
	}
	
	public static Domain direct() {
		return new DirectWarp();
	}
	
	private static void register(String name, MapCodec<? extends Domain> value) {
		RegistryUtil.register(NTBuiltInRegistries.DOMAIN_TYPE, name, value);
		registeredCount++;
	}
}
