package neoterra.platform.fabric;

import net.minecraft.core.RegistrySetBuilder;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.settings.Preset;

public final class BiomeModifierPlatformImpl {

	public static void addPatches(RegistrySetBuilder builder, Preset preset) {
		// no-op: on Fabric biome modifiers are applied at runtime via the MixinBiome injector
		// driven by FabricBiomeModifierApplier; nothing extra needs to be added to the preset
		// patch set at datapack-build time.
		NTCommon.debug("Fabric addPatches: no-op (modifiers applied via Mixin in FabricBiomeModifierApplier)");
	}
}
