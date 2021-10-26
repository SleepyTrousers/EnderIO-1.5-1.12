package com.enderio.base.client;

import com.enderio.base.EnderIO;
import com.enderio.base.client.renderers.GraveRenderer;
import com.enderio.base.common.blockentity.EIOBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void customModelLoaders(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(EnderIO.loc("item/wood_gear_helper"));
        ModelLoader.addSpecialModel(EnderIO.loc("item/stone_gear_helper"));
        ModelLoader.addSpecialModel(EnderIO.loc("item/iron_gear_helper"));
        ModelLoader.addSpecialModel(EnderIO.loc("item/energized_gear_helper"));
        ModelLoader.addSpecialModel(EnderIO.loc("item/vibrant_gear_helper"));
        ModelLoader.addSpecialModel(EnderIO.loc("item/dark_bimetal_gear_helper"));
    }

    @SubscribeEvent
    public static void registerBERS(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EIOBlockEntities.GRAVE.get(), GraveRenderer::new);
    }
}
