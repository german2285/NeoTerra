package neoterra.data.worldgen.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.levelgen.DensityFunction;
import neoterra.data.worldgen.preset.PresetNoiseRouterData;
import neoterra.tags.NTDensityFunctionTags;

public class NTDensityFunctionTagsProvider extends TagsProvider<DensityFunction> {

	public NTDensityFunctionTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(packOutput, Registries.DENSITY_FUNCTION, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(NTDensityFunctionTags.ADDITIONAL_NOISE_ROUTER_FUNCTIONS).add(PresetNoiseRouterData.GRADIENT, PresetNoiseRouterData.HEIGHT_EROSION, PresetNoiseRouterData.SEDIMENT);
	}
}
