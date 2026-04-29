package neoterra.neoforge;

import com.mojang.serialization.MapCodec;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import neoterra.NTCommon;
import neoterra.client.data.NTLanguageProvider;
import neoterra.client.data.NTTranslationKeys;
import neoterra.platform.neoforge.RegistryUtilImpl;
import neoterra.world.worldgen.biome.modifier.neoforge.PrependFeaturesBiomeModifier;

@Mod("neoterra")
public class NTNeoForge {
    private static final DeferredRegister<MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
        DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, NTCommon.MOD_ID);

    static {
        BIOME_MODIFIER_SERIALIZERS.register("prepend_features", () -> PrependFeaturesBiomeModifier.CODEC);
    }

    public NTNeoForge(IEventBus modEventBus, ModContainer container) {
    	NTCommon.debug("NeoForge entry point: NTNeoForge constructor");
    	NTCommon.bootstrap();

    	NTCommon.debug("Registering BIOME_MODIFIER_SERIALIZERS DeferredRegister on mod event bus");
    	BIOME_MODIFIER_SERIALIZERS.register(modEventBus);

    	if (FMLEnvironment.dist == Dist.CLIENT) {
    		NTCommon.debug("Client dist detected, attaching preset editor listeners");
    		modEventBus.addListener(NTNeoForgeClient::registerPresetEditors);
    	}
    	modEventBus.addListener(NTNeoForge::gatherData);
    	RegistryUtilImpl.register(modEventBus);
    	NTCommon.debug("NeoForge constructor complete");
    }

    private static void gatherData(GatherDataEvent event) {
    	NTCommon.debug("NeoForge gatherData: starting");
    	boolean includeClient = event.includeClient();
    	DataGenerator generator = event.getGenerator();
    	PackOutput output = generator.getPackOutput();

    	generator.addProvider(includeClient, new NTLanguageProvider.EnglishUS(output));
    	generator.addProvider(includeClient, PackMetadataGenerator.forFeaturePack(output, Component.translatable(NTTranslationKeys.METADATA_DESCRIPTION)));
    	NTCommon.debug("NeoForge gatherData: registered LanguageProvider and PackMetadataGenerator (includeClient={})", includeClient);
    }
}