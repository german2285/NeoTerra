package neoterra.platform.fabric;

import net.minecraft.core.RegistrySetBuilder;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.settings.Preset;

public final class BiomeModifierPlatformImpl {

	public static void addPatches(RegistrySetBuilder builder, Preset preset) {
		// no-op: on Fabric biome modifiers are registered statically via FabricBiomePatches
		// using Fabric's BiomeModifications API; nothing extra needs to be added to the
		// preset patch set.
		NTCommon.LOGGER.debug("Fabric addPatches: no-op (modifiers registered statically via FabricBiomePatches)");
	}
}
