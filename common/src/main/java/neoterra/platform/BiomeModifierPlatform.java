package neoterra.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.RegistrySetBuilder;
import neoterra.data.worldgen.preset.settings.Preset;

public final class BiomeModifierPlatform {

	@ExpectPlatform
	public static void addPatches(RegistrySetBuilder builder, Preset preset) {
		throw new IllegalStateException();
	}
}
