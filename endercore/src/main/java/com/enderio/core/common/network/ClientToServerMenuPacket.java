package com.enderio.core.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Function;

public abstract class ClientToServerMenuPacket<Menu extends AbstractContainerMenu> implements Packet {

    private final int containerID;
    private final Class<Menu> menuClass;

    protected ClientToServerMenuPacket(Class<Menu> menuClass, int containerID) {
        this.containerID = containerID;
        this.menuClass = menuClass;
    }
    protected ClientToServerMenuPacket(Class<Menu> menuClass, FriendlyByteBuf buf) {
        this.containerID = buf.readInt();
        this.menuClass = menuClass;
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeInt(containerID);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        if (context.getSender() != null) {
            AbstractContainerMenu menu = context.getSender().containerMenu;
            if (menu != null) {
                return menu.containerId == containerID
                    && menu.getClass() == menuClass;
            }
        }
        return false;
    }

    protected Menu getMenu(NetworkEvent.Context context) {
        return menuClass.cast(context.getSender().containerMenu);
    }

    public static class Handler<MSG extends ClientToServerMenuPacket<?>> extends Packet.PacketHandler<MSG> {

        private final Function<FriendlyByteBuf, MSG> constructor;
        public Handler(Function<FriendlyByteBuf, MSG> constructor) {
            this.constructor = constructor;
        }

        @Override
        public MSG of(FriendlyByteBuf buf) {
            return constructor.apply(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_SERVER);
        }

        @Override
        public void to(MSG packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }
    }

    protected void handleWrongPlayer(NetworkEvent.Context context) {
        //TODO: handle Packet Message
    }
}
