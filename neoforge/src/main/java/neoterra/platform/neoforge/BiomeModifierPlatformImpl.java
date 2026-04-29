package neoterra.platform.neoforge;

import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.biomepatch.neoforge.PatchesToNeoForgeBiomeModifiers;
import neoterra.data.worldgen.preset.settings.Preset;

public final class BiomeModifierPlatformImpl {

	public static void addPatches(RegistrySetBuilder builder, Preset preset) {
		NTCommon.LOGGER.debug("NeoForge addPatches: scheduling BIOME_MODIFIERS bootstrap for preset");
		builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ctx -> PatchesToNeoForgeBiomeModifiers.bootstrap(ctx, preset));
	}
}
