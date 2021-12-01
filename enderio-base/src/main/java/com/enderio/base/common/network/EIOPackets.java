package com.enderio.base.common.network;

import com.enderio.base.EnderIO;
import com.enderio.base.common.network.packet.UpdateCoordinateSelectionNameMenuPacket;
import com.enderio.core.common.network.ClientToServerMenuPacket;
import com.enderio.core.common.network.EnderNetwork;
import net.minecraft.resources.ResourceLocation;

public class EIOPackets extends EnderNetwork {

    private EIOPackets() {}

    public static EIOPackets NETWORK = new EIOPackets();

    private static final String PROTOCOL_VERSION = "1.0";

    @Override
    public ResourceLocation channelName() {
        return EnderIO.loc("network");
    }

    @Override
    public String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    public static EIOPackets getNetwork() {
        return NETWORK;
    }

    @Override
    protected void registerMessages() {
        registerMessage(new ClientToServerMenuPacket.Handler<>(UpdateCoordinateSelectionNameMenuPacket::new), UpdateCoordinateSelectionNameMenuPacket.class);
    }
}
