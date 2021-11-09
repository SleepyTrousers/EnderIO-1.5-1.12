package com.enderio.core.common.network;

import com.enderio.core.common.network.packet.SyncClientToServerMenuPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class EnderNetwork {

    private static final String PROTOCOL_VERSION = "1.0";
    public static EnderNetwork NETWORK = new EnderNetwork();
    private final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
        channelName(),
        () -> PROTOCOL_VERSION,
        getProtocolVersion()::equals,
        getProtocolVersion()::equals);

    private static int index = 0;
    protected EnderNetwork() {
        registerMessages();
    }

    public static EnderNetwork getNetwork() {
        return NETWORK;
    }

    public ResourceLocation channelName() {
        return new ResourceLocation("endercore", "network");
    }

    public SimpleChannel getNetworkChannel() {
        return channel;
    }

    public String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    private static int getAndUpdateIndex() {
        return index++;
    }

    protected void registerMessages() {
        registerMessage(new ClientToServerMenuPacket.Handler<>(SyncClientToServerMenuPacket::new), SyncClientToServerMenuPacket.class);
    }

    public <MSG extends Packet> void registerMessage(Packet.PacketHandler<MSG> handler, Class<MSG> packetClass) {
        getNetworkChannel().registerMessage(getAndUpdateIndex(), packetClass, handler::to, handler::of, handler, handler.getDirection());
    }
}
