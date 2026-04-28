package neoterra.data.worldgen.preset;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.noise.module.Noises;

public class PresetNoiseData {

	public static void bootstrap(Preset preset, BootstrapContext<Noise> ctx) {
		PresetTerrainNoise.bootstrap(preset, ctx);
		PresetClimateNoise.bootstrap(preset, ctx);
		PresetSurfaceNoise.bootstrap(preset, ctx);
		PresetStrataNoise.bootstrap(preset, ctx);
	}
	
	public static Noise getNoise(HolderGetter<Noise> noiseLookup, ResourceKey<Noise> key) {
		return new Noises.HolderHolder(noiseLookup.getOrThrow(key));
	}
	
	public static Noise registerAndWrap(BootstrapContext<Noise> ctx, ResourceKey<Noise> key, Noise noise) {
		return new Noises.HolderHolder(ctx.register(key, noise));
	}
	
	public static ResourceKey<Noise> createKey(String name) {
        return ResourceKey.create(NTRegistries.NOISE, NTCommon.location(name));
	}
}
