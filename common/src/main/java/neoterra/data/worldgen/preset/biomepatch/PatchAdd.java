package neoterra.data.worldgen.preset.biomepatch;

import java.util.Optional;

import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record PatchAdd(
	ResourceLocation id,
	Order order,
	GenerationStep.Decoration step,
	Optional<Filter> filter,
	HolderSet<PlacedFeature> features
) {}
