package neoterra.neoforge;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import neoterra.NTCommon;
import neoterra.client.data.NTLanguageProvider;
import neoterra.client.data.NTTranslationKeys;
import neoterra.platform.RegistryUtil;
import neoterra.platform.neoforge.RegistryUtilImpl;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.biome.modifier.BiomeModifier;

@Mod("neoterra")
public class NTNeoForge {

    public NTNeoForge(IEventBus modEventBus, ModContainer container) {
    	NTCommon.bootstrap();

    	RegistryUtil.createDataRegistry(NTRegistries.BIOME_MODIFIER, BiomeModifier.CODEC, false);

    	if (FMLEnvironment.dist == Dist.CLIENT) {
    		modEventBus.addListener(NTNeoForgeClient::registerPresetEditors);
    	}
    	modEventBus.addListener(NTNeoForge::gatherData);
    	RegistryUtilImpl.register(modEventBus);
    }

    private static void gatherData(GatherDataEvent event) {
    	boolean includeClient = event.includeClient();
    	DataGenerator generator = event.getGenerator();
    	PackOutput output = generator.getPackOutput();

    	generator.addProvider(includeClient, new NTLanguageProvider.EnglishUS(output));
    	generator.addProvider(includeClient, PackMetadataGenerator.forFeaturePack(output, Component.translatable(NTTranslationKeys.METADATA_DESCRIPTION)));
    }
}