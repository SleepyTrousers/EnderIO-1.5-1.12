package com.enderio.core.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface Packet {

    boolean isValid(NetworkEvent.Context context);

    void handle(NetworkEvent.Context context);


    abstract class PacketHandler<MSG extends Packet> implements BiConsumer<MSG, Supplier<NetworkEvent.Context>> {
        public abstract MSG of(FriendlyByteBuf buf);

        public abstract void to(MSG packet, FriendlyByteBuf buf);

        @Override
        public void accept(MSG msg, Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            if (msg.isValid(context.get())) {
                ctx.enqueueWork(() -> msg.handle(ctx));
            } else {
                logPacketError(ctx, "didn't pass check and is invalid", msg);
            }
            context.get().setPacketHandled(true);
        }

        public abstract Optional<NetworkDirection> getDirection();
    }

    static void logPacketError(NetworkEvent.Context context, String error, Packet packet) {
        String sender;
        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            sender = "the server";
        } else {
            sender = context.getSender().getName().getContents() + " with IP-Address " + context.getSender().getIpAddress();
        }
        LogManager.getLogger().warn("Packet {} from {}: {}", packet.getClass(),sender, error);
    }
}
