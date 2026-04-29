package neoterra.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import neoterra.NTCommon;

public final class NTWorldgenKeys {
	public static final ResourceKey<DimensionType> OVERWORLD_DIMENSION_TYPE =
		ResourceKey.create(Registries.DIMENSION_TYPE, NTCommon.location("overworld"));

	public static final ResourceKey<WorldPreset> OVERWORLD_WORLD_PRESET =
		ResourceKey.create(Registries.WORLD_PRESET, NTCommon.location("overworld"));

	private NTWorldgenKeys() {}
}
