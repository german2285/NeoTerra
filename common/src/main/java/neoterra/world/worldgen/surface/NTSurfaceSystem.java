package neoterra.world.worldgen.surface;

import java.util.List;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import neoterra.world.worldgen.surface.rule.StrataRule;

public interface NTSurfaceSystem {
	List<List<StrataRule.Layer>> getOrCreateStrata(ResourceLocation name, Function<RandomSource, List<List<StrataRule.Layer>>> factory);
}
