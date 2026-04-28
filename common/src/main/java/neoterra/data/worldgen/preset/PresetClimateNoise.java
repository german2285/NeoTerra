package neoterra.data.worldgen.preset;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import neoterra.data.worldgen.preset.settings.ClimateSettings;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.WorldSettings;
import neoterra.world.worldgen.noise.module.Noise;

public class PresetClimateNoise {
	public static final ResourceKey<Noise> BIOME_EDGE_SHAPE = createKey("biome_edge_shape");
	
	public static void bootstrap(Preset preset, BootstrapContext<Noise> ctx) {
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		
		ClimateSettings climateSettings = preset.climate();
		ClimateSettings.BiomeNoise biomeEdgeShape = climateSettings.biomeEdgeShape;
		
		ctx.register(BIOME_EDGE_SHAPE, biomeEdgeShape.build(0));
	}

	private static ResourceKey<Noise> createKey(String name) {
		return PresetNoiseData.createKey("climate/" + name);
	}
}
