package neoterra.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import neoterra.NTCommon;

public final class NTDensityFunctionTags {
	// i dont like this but dont know what to do about it
	// note: this should only include functions not present in the NoiseRouter
	@Deprecated
	public static final TagKey<DensityFunction> ADDITIONAL_NOISE_ROUTER_FUNCTIONS = resolve("additional_noise_router_functions");
	
    private static TagKey<DensityFunction> resolve(String path) {
    	return TagKey.create(Registries.DENSITY_FUNCTION, NTCommon.location(path));
    }
}
