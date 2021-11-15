package com.enderio.core.client;

import com.enderio.core.common.menu.SyncedMenu;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            if (Minecraft.getInstance().player.containerMenu instanceof SyncedMenu syncedMenu) {
                syncedMenu.clientTick();
            }
        }
    }
}
