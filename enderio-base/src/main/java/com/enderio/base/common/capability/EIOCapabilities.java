package com.enderio.base.common.capability;

import com.enderio.base.EnderIO;

import com.enderio.base.common.capability.owner.IOwner;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOCapabilities {
    
    @CapabilityInject(IOwner.class)
    public static Capability<IOwner> OWNER;
    
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IOwner.class);
    }

}
