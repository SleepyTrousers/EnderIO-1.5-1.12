package com.enderio.base.common.capability;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capability.capacitors.ICapacitorData;
import com.enderio.base.common.capability.entity.IEntityStorage;
import com.enderio.base.common.capability.location.ICoordinateSelectionHolder;
import com.enderio.base.common.capability.owner.IOwner;
import com.enderio.base.common.capability.toggled.IToggled;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOCapabilities {
    public static final Capability<IEntityStorage> ENTITY_STORAGE = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<IToggled> TOGGLED = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<IOwner> OWNER = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<ICapacitorData> CAPACITOR = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<ICoordinateSelectionHolder> COORDINATE_SELECTION_HOLDER = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IEntityStorage.class);
        event.register(IToggled.class);
        event.register(IOwner.class);
        event.register(ICapacitorData.class);
        event.register(ICoordinateSelectionHolder.class);
    }
}
