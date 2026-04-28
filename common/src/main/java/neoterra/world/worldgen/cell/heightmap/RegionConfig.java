package neoterra.world.worldgen.cell.heightmap;

import neoterra.world.worldgen.noise.module.Noise;

public record RegionConfig(int seed, int scale, Noise warpX, Noise warpZ, float warpStrength) {
}
