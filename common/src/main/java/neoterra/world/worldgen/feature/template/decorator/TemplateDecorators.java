package neoterra.world.worldgen.feature.template.decorator;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;

public class TemplateDecorators {

	public static void bootstrap() {
		register("tree", TreeDecorator.CODEC);
	}
	
	public static TreeDecorator tree(net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator decorator) {
		return tree(decorator, decorator);
	}
	
	public static TreeDecorator tree(net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator decorator, net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator modifiedDecorator) {
		return new TreeDecorator(decorator, modifiedDecorator);
	}

	private static void register(String name, MapCodec<? extends TemplateDecorator<?>> placement) {
		RegistryUtil.register(NTBuiltInRegistries.TEMPLATE_DECORATOR_TYPE, name, placement);
	}
}
