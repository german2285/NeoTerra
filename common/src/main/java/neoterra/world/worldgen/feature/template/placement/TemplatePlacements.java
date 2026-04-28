package neoterra.world.worldgen.feature.template.placement;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;

public class TemplatePlacements {

	public static void bootstrap() {
		register("any", AnyPlacement.CODEC);
		register("tree", TreePlacement.CODEC);
	}
	
	public static AnyPlacement any() {
		return new AnyPlacement();
	}	
	
	public static TreePlacement tree() {
		return new TreePlacement();
	}
	
	private static void register(String name, MapCodec<? extends TemplatePlacement<?>> placement) {
		RegistryUtil.register(NTBuiltInRegistries.TEMPLATE_PLACEMENT_TYPE, name, placement);
	}
}
