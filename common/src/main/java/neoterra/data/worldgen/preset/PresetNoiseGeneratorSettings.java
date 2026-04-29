package neoterra.data.worldgen.preset;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import neoterra.NTCommon;
import neoterra.data.worldgen.NTWorldgenKeys;
import neoterra.data.worldgen.preset.settings.CaveSettings;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.WorldSettings;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.noise.module.Noise;

public class PresetNoiseGeneratorSettings {

	public static void bootstrap(Preset preset, BootstrapContext<NoiseGeneratorSettings> ctx) {
		NTCommon.debug("PresetNoiseGeneratorSettings.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		HolderGetter<Noise> noises = ctx.lookup(NTRegistries.NOISE);

		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		int worldHeight = properties.worldHeight;
		int worldDepth = properties.worldDepth;

		CaveSettings caveSettings = preset.caves();

		ctx.register(NTWorldgenKeys.OVERWORLD_NOISE_SETTINGS, new NoiseGeneratorSettings(
			NoiseSettings.create(-worldDepth, worldDepth + worldHeight, 1, 2),
			Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(),
			PresetNoiseRouterData.overworld(preset, densityFunctions, noiseParams, noises),
			PresetSurfaceRuleData.overworld(preset, densityFunctions, noises),
			properties.spawnType.getParameterPoints(),
			properties.seaLevel,
			false,
			true,
			caveSettings.largeOreVeins,
			false
		));
		NTCommon.debug("PresetNoiseGeneratorSettings.bootstrap: registered neoterra:overworld in {} ms (seaLevel={}, worldDepth={}, worldHeight={}, largeOreVeins={})", System.currentTimeMillis() - t0, properties.seaLevel, worldDepth, worldHeight, caveSettings.largeOreVeins);
    }
}
