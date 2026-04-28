package neoterra.world.worldgen.cell.climate;

import net.minecraft.core.HolderGetter;
import neoterra.data.worldgen.preset.PresetClimateNoise;
import neoterra.data.worldgen.preset.PresetNoiseData;
import neoterra.data.worldgen.preset.settings.ClimateSettings;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.WorldSettings;
import neoterra.world.worldgen.GeneratorContext;
import neoterra.world.worldgen.cell.Cell;
import neoterra.world.worldgen.cell.continent.Continent;
import neoterra.world.worldgen.cell.heightmap.Levels;
import neoterra.world.worldgen.cell.terrain.TerrainType;
import neoterra.world.worldgen.noise.NoiseUtil;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.noise.module.Noises;

public record Climate(int randomSeed, Noise offsetX, Noise offsetZ, int offsetDistance, Levels levels, ClimateModule biomeNoise) {

	public void apply(Cell cell, float x, float z, boolean applyClimate) {
		this.biomeNoise.apply(cell, x, z, x, z, true);
		float edgeBlend = 0.4F;
		if (cell.height <= this.levels.water) {
			if (cell.terrain == TerrainType.COAST) {
				cell.terrain = TerrainType.SHALLOW_OCEAN;
			}
		} else if (cell.biomeRegionEdge < edgeBlend || cell.terrain == TerrainType.MOUNTAIN_CHAIN) {
			float modifier = 1.0F - NoiseUtil.map(cell.biomeRegionEdge, 0.0F, edgeBlend, edgeBlend);
			float distance = this.offsetDistance * modifier;
			float dx = this.offsetX.compute(x, z, 0) * distance;
			float dz = this.offsetZ.compute(x, z, 0) * distance;
			float ox = x;
			float oz = z;
			x += dx;
			z += dz;
			this.biomeNoise.apply(cell, x, z, ox, oz, false);
		}
	}
	
	public static Climate make(Continent continent, GeneratorContext context) {
		HolderGetter<Noise> noiseLookup = context.noiseLookup;
		
		Preset preset = context.preset;
		
		WorldSettings worldSettings = preset.world();
		ClimateSettings climateSettings = preset.climate();
		
		ClimateModule biomeNoise = new ClimateModule(context.seed, continent, worldSettings.controlPoints, climateSettings, context.levels);
		Levels levels = context.levels;
		int randSeed = context.seed.next();
		
		Noise biomeEdgeShape = PresetNoiseData.getNoise(noiseLookup, PresetClimateNoise.BIOME_EDGE_SHAPE);
		Noise offsetX = Noises.shiftSeed(biomeEdgeShape, context.seed.next());
		Noise offsetZ = Noises.shiftSeed(biomeEdgeShape, context.seed.next());
		int offsetDistance = climateSettings.biomeEdgeShape.strength;
		return new Climate(randSeed, offsetX, offsetZ, offsetDistance, levels, biomeNoise);
	}
}
