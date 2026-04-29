package neoterra.world.worldgen.feature.template.decorator;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;

public class TemplateDecorators {
	private static int registeredCount = 0;

	public static void bootstrap() {
		NTCommon.debug("TemplateDecorators.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		registeredCount = 0;
		register("tree", TreeDecorator.CODEC);
		NTCommon.debug("TemplateDecorators.bootstrap: registered {} template decorator types in {} ms", registeredCount, System.currentTimeMillis() - t0);
	}
	
	public static TreeDecorator tree(net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator decorator) {
		return tree(decorator, decorator);
	}
	
	public static TreeDecorator tree(net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator decorator, net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator modifiedDecorator) {
		return new TreeDecorator(decorator, modifiedDecorator);
	}

	private static void register(String name, MapCodec<? extends TemplateDecorator<?>> placement) {
		RegistryUtil.register(NTBuiltInRegistries.TEMPLATE_DECORATOR_TYPE, name, placement);
		registeredCount++;
	}
}
