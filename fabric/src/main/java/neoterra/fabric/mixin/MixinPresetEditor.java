package neoterra.fabric.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import neoterra.NTCommon;
import neoterra.client.gui.screen.presetconfig.PresetConfigScreen;
import neoterra.data.worldgen.NTWorldgenKeys;

// PresetEditor.EDITORS — это Map.of(...) в clinit. Mojang не даёт public API
// зарегистрировать editor для своего worldgen preset, поэтому Redirect перехватывает
// вызов Map.of и возвращает modifiable HashMap, в который PUT'аем editor для нашего
// neoterra:overworld.
//
// На NeoForge для этого есть штатный RegisterPresetEditorsEvent (см. NTNeoForgeClient).
// На Fabric API публичного аналога нет — тот же хак применяют TerraForged /
// ReTerraForged / NeoTerraForged. Если/когда Fabric API добавит хук — мигрируем,
// см. TODO/todo/fabric-api-preset-editor-watch.md.
@Mixin(PresetEditor.class)
interface MixinPresetEditor {

	@Redirect(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"
		),
		remap = false
	)
	private static Map<Object, Object> of(Object k1, Object v1, Object k2, Object v2) {
		NTCommon.debug("Fabric MixinPresetEditor.of: replacing immutable PresetEditor map with mutable HashMap to inject neoterra:overworld editor");
		Map<Object, Object> map = new HashMap<>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(Optional.of(NTWorldgenKeys.OVERWORLD_WORLD_PRESET), (PresetEditor) (screen, ctx) -> new PresetConfigScreen(screen));
		NTCommon.debug("Fabric MixinPresetEditor.of: registered neoterra:overworld editor (total {} entries)", map.size());
		return map;
    }
}
