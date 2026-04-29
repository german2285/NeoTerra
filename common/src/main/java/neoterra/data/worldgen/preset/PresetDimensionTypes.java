package neoterra.data.worldgen.preset;

import java.util.OptionalLong;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import neoterra.NTCommon;
import neoterra.data.worldgen.NTWorldgenKeys;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.WorldSettings;

public final class PresetDimensionTypes {

	public static void bootstrap(Preset preset, BootstrapContext<DimensionType> ctx) {
		NTCommon.debug("PresetDimensionTypes.bootstrap: starting");
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		int worldHeight = properties.worldHeight;
		int worldDepth = properties.worldDepth;
		int totalHeight = worldDepth + worldHeight;

        ctx.register(NTWorldgenKeys.OVERWORLD_DIMENSION_TYPE, new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0, true, false, -worldDepth, totalHeight, totalHeight, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0f, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
		NTCommon.debug("PresetDimensionTypes.bootstrap: registered neoterra:overworld (worldHeight={}, worldDepth={}, totalHeight={})", worldHeight, worldDepth, totalHeight);
	}
}
