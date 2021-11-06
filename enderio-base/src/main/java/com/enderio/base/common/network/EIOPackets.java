package com.enderio.base.common.network;

import com.enderio.base.EnderIO;
import com.enderio.base.common.network.packet.UpdateCoordinateSelectionNameMenuPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class EIOPackets {

    private EIOPackets() {}

    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        EnderIO.loc("network"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals);

    private static int index = 0;

    private static int getAndUpdateIndex() {
        return index++;
    }

    public static void register() {
        registerMessage(new UpdateCoordinateSelectionNameMenuPacket.Handler(), UpdateCoordinateSelectionNameMenuPacket.class);
    }

    public static <MSG extends Packet> void registerMessage(Packet.PacketHandler<MSG> handler, Class<MSG> packetClass) {
        INSTANCE.registerMessage(getAndUpdateIndex(), packetClass, handler::to, handler::of, handler, handler.getDirection());
    }
}
