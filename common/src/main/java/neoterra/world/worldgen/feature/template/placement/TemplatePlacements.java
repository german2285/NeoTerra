package neoterra.world.worldgen.feature.template.placement;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;

public class TemplatePlacements {
	private static int registeredCount = 0;

	public static void bootstrap() {
		NTCommon.debug("TemplatePlacements.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		registeredCount = 0;
		register("any", AnyPlacement.CODEC);
		register("tree", TreePlacement.CODEC);
		NTCommon.debug("TemplatePlacements.bootstrap: registered {} template placement types in {} ms", registeredCount, System.currentTimeMillis() - t0);
	}
	
	public static AnyPlacement any() {
		return new AnyPlacement();
	}	
	
	public static TreePlacement tree() {
		return new TreePlacement();
	}
	
	private static void register(String name, MapCodec<? extends TemplatePlacement<?>> placement) {
		RegistryUtil.register(NTBuiltInRegistries.TEMPLATE_PLACEMENT_TYPE, name, placement);
		registeredCount++;
	}
}
