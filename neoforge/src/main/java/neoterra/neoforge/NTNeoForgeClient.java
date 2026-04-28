package neoterra.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent;
import neoterra.NTCommon;
import neoterra.client.gui.screen.presetconfig.PresetConfigScreen;

class NTNeoForgeClient {

	public static final ResourceKey<WorldPreset> DEFAULT = ResourceKey.create(
		Registries.WORLD_PRESET,
		ResourceLocation.fromNamespaceAndPath(NTCommon.MOD_ID, "default")
	);

	public static void registerPresetEditors(RegisterPresetEditorsEvent event) {
		event.register(DEFAULT, (screen, ctx) -> new PresetConfigScreen(screen));
	}
}
