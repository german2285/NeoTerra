package neoterra.data.worldgen.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import neoterra.data.worldgen.preset.settings.MiscellaneousSettings;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.tags.NTBlockTags;

public class NTBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
	private Preset preset;
	
	public NTBlockTagsProvider(Preset preset, PackOutput packOutput, CompletableFuture<Provider> completableFuture) {
		super(packOutput, Registries.BLOCK, completableFuture, (block) -> block.builtInRegistryHolder().key());

		this.preset = preset;
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
//		MiscellaneousSettings miscellaneousSettings = this.preset.miscellaneous();

		this.tag(NTBlockTags.SOIL).add(Blocks.DIRT, Blocks.COARSE_DIRT);
		this.tag(NTBlockTags.CLAY).add(Blocks.CLAY);
		this.tag(NTBlockTags.SEDIMENT).add(Blocks.SAND, Blocks.GRAVEL);
		this.tag(NTBlockTags.ERODIBLE).add(Blocks.SNOW_BLOCK).add(Blocks.POWDER_SNOW).add(Blocks.GRAVEL).addOptionalTag(BlockTags.DIRT.location());
		
//		if(!miscellaneousSettings.oreCompatibleStoneOnly) {
			this.tag(NTBlockTags.ROCK).add(Blocks.GRANITE, Blocks.ANDESITE, Blocks.STONE, Blocks.DIORITE);
//		} else{
			//TODO
//		}
	}
}