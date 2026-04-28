package neoterra.data.worldgen.preset;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.WorldSettings;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.noise.module.Noises;

public class PresetTerrainTypeNoise {
	public static final ResourceKey<Noise> GROUND = PresetTerrainNoise.createKey("ground");
	
	public static void bootstrap(Preset preset, BootstrapContext<Noise> ctx) {
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		float seaLevel = properties.seaLevel;
		int worldHeight = properties.worldHeight;

		ctx.register(GROUND, Noises.constant(seaLevel / (float)worldHeight));
	}
}
