package neoterra.neoforge;

import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent;
import neoterra.client.gui.screen.presetconfig.PresetConfigScreen;
import neoterra.data.worldgen.NTWorldgenKeys;

class NTNeoForgeClient {

	public static void registerPresetEditors(RegisterPresetEditorsEvent event) {
		event.register(NTWorldgenKeys.OVERWORLD_WORLD_PRESET, (screen, ctx) -> new PresetConfigScreen(screen));
	}
}
