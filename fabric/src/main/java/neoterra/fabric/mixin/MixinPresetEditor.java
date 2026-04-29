package neoterra.fabric.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import neoterra.NTCommon;
import neoterra.client.gui.screen.presetconfig.PresetConfigScreen;

// PresetEditor.EDITORS — это Map.of(...) в clinit. Mojang не даёт public API
// зарегистрировать editor для своего worldgen preset, поэтому Redirect перехватывает
// вызов Map.of и возвращает modifiable HashMap, в который PUT'аем editor для нашего
// neoterra:default.
@Deprecated
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
		NTCommon.debug("Fabric MixinPresetEditor.of: replacing immutable PresetEditor map with mutable HashMap to inject neoterra:default editor");
		Map<Object, Object> map = new HashMap<>();
		map.put(k1, v1);
		map.put(k2, v2);
		ResourceKey<WorldPreset> ntDefault = ResourceKey.create(Registries.WORLD_PRESET, NTCommon.location("default"));
		map.put(Optional.of(ntDefault), (PresetEditor) (screen, ctx) -> new PresetConfigScreen(screen));
		NTCommon.debug("Fabric MixinPresetEditor.of: registered neoterra:default editor (total {} entries)", map.size());
		return map;
    }
}
