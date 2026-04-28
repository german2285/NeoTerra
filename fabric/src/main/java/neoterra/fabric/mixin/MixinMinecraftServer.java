package neoterra.fabric.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import neoterra.server.NTMinecraftServer;
import neoterra.world.worldgen.feature.template.template.FeatureTemplateManager;

@Implements(@Interface(iface = NTMinecraftServer.class, prefix = "neoterra$NTMinecraftServer$"))
@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	private FeatureTemplateManager templateManager;

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	public void MinecraftServer(CallbackInfo callback) {
		this.templateManager = new FeatureTemplateManager(this.getResourceManager());
	}
	
	public FeatureTemplateManager neoterra$NTMinecraftServer$getFeatureTemplateManager() {
		return this.templateManager;
	}
	
	@Inject(
		method = { "method_29440" },
		require = 0,
		at = @At("TAIL"),
		remap = false
	)
	private void method_29440(CallbackInfo callback) {
		this.templateManager.onReload(this.getResourceManager());
	}

	@Shadow
	private ResourceManager getResourceManager() {
		throw new UnsupportedOperationException();
	}
}
