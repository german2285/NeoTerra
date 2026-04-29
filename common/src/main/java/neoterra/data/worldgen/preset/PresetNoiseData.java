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
		NTCommon.debug("PresetNoiseData.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		NTCommon.debug("PresetNoiseData.bootstrap: invoking PresetTerrainNoise.bootstrap");
		PresetTerrainNoise.bootstrap(preset, ctx);
		NTCommon.debug("PresetNoiseData.bootstrap: invoking PresetClimateNoise.bootstrap");
		PresetClimateNoise.bootstrap(preset, ctx);
		NTCommon.debug("PresetNoiseData.bootstrap: invoking PresetSurfaceNoise.bootstrap");
		PresetSurfaceNoise.bootstrap(preset, ctx);
		NTCommon.debug("PresetNoiseData.bootstrap: invoking PresetStrataNoise.bootstrap");
		PresetStrataNoise.bootstrap(preset, ctx);
		NTCommon.debug("PresetNoiseData.bootstrap: complete in {} ms", System.currentTimeMillis() - t0);
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
