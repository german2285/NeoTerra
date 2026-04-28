package neoterra.data.worldgen.preset.biomepatch;

import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.world.worldgen.biome.modifier.BiomeModifier;

public record PatchReplace(
	ResourceKey<BiomeModifier> id,
	GenerationStep.Decoration step,
	Optional<HolderSet<Biome>> biomes,
	Map<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> replacements
) {
	public PatchReplace {
		replacements = Map.copyOf(replacements);
	}
}
