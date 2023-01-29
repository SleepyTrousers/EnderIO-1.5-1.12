package crazypants.enderio.item.darksteel;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;

public class PacketUpgradeState implements IMessage, IMessageHandler<PacketUpgradeState, IMessage> {

    public enum Type {
        GLIDE,
        SPEED,
        STEP_ASSIST,
        JUMP
    }

    public PacketUpgradeState() {}

    private boolean isActive;
    private Type type;
    private int entityID;

    public PacketUpgradeState(Type type, boolean isActive) {
        this(type, isActive, 0);
    }

    public PacketUpgradeState(Type type, boolean isActive, int entityID) {
        this.type = type;
        this.isActive = isActive;
        this.entityID = entityID;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(type.ordinal());
        buf.writeBoolean(isActive);
        buf.writeInt(entityID);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readShort()];
        isActive = buf.readBoolean();
        entityID = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
        EntityPlayer player = (EntityPlayer) (ctx.side.isClient()
                ? EnderIO.proxy.getClientWorld().getEntityByID(message.entityID)
                : ctx.getServerHandler().playerEntity);
        if (player != null) {
            DarkSteelController.instance.setActive(player, message.type, message.isActive);
            if (ctx.side.isServer()) {
                message.entityID = player.getEntityId();
                PacketHandler.INSTANCE.sendToDimension(message, player.worldObj.provider.dimensionId);
            }
        }
        return null;
    }
}
