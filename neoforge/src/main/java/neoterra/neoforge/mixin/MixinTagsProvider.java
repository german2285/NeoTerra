package neoterra.neoforge.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.IResourceType;
import neoterra.NTCommon;

@Mixin(TagsProvider.class)
class MixinTagsProvider {
	@Unique
	private static boolean neoterra$logged_trackGenerated;
	@Unique
	private static boolean neoterra$logged_trackGenerated_null;

	@Redirect(
		remap = false,
		method = "getOrCreateRawBuilder",
		at = @At(
			remap = false,
			value = "INVOKE",
			target = "Lnet/neoforged/neoforge/common/data/ExistingFileHelper;trackGenerated(Lnet/minecraft/resources/ResourceLocation;Lnet/neoforged/neoforge/common/data/ExistingFileHelper$IResourceType;)V"
		)
	)
	void trackGenerated(@Nullable ExistingFileHelper fileHelper, ResourceLocation loc, IResourceType type) {
		// what the hell forge
		// they even annotated it with @Nullable
		if(fileHelper != null) {
			if(!neoterra$logged_trackGenerated) {
				neoterra$logged_trackGenerated = true;
				NTCommon.debug("NeoForge MixinTagsProvider.trackGenerated: first call delegating to ExistingFileHelper for {} ({})", loc, type);
			}
			fileHelper.trackGenerated(loc, type);
		} else if(!neoterra$logged_trackGenerated_null) {
			neoterra$logged_trackGenerated_null = true;
			NTCommon.debug("NeoForge MixinTagsProvider.trackGenerated: first call with null ExistingFileHelper, skipping (loc={}, type={})", loc, type);
		}
	}
}
