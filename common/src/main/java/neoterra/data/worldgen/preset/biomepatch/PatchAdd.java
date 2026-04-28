package neoterra.data.worldgen.preset.biomepatch;

import java.util.Optional;

import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import neoterra.world.worldgen.biome.modifier.BiomeModifier;
import neoterra.world.worldgen.biome.modifier.Filter;
import neoterra.world.worldgen.biome.modifier.Order;

public record PatchAdd(
	ResourceKey<BiomeModifier> id,
	Order order,
	GenerationStep.Decoration step,
	Optional<Filter> filter,
	HolderSet<PlacedFeature> features
) {}
