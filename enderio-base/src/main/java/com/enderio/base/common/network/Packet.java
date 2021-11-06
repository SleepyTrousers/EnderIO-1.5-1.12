package com.enderio.base.common.network;

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
                String sender;
                if (ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    sender = "the server";
                } else {
                    sender = ctx.getSender().getName().getContents() + " with IP-Address " + ctx.getSender().getIpAddress();
                }
                LogManager.getLogger().warn("invalid packet received from {}", sender);
            }
            context.get().setPacketHandled(true);
        }

        public abstract Optional<NetworkDirection> getDirection();
    }
}
