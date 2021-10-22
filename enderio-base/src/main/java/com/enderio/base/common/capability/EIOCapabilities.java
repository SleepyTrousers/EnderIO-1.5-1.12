package com.enderio.base.common.capability;

import com.enderio.base.EnderIO;

import com.enderio.base.common.capability.owner.IOwner;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOCapabilities {
    public static final Capability<IOwner> OWNER = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IOwner.class);
    }

}
