package crazypants.enderio.teleport.packet;

import com.enderio.core.common.TileEntityEnder;
import com.enderio.core.common.network.MessageTileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.api.teleport.ITravelAccessable;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class PacketPassword extends MessageTileEntity<TileEntityEnder> {

    private ItemStack stack;
    private int slot;
    private boolean setLabel;

    public PacketPassword() {}

    private PacketPassword(TileEntityEnder tile) {
        super(tile);
    }

    public static IMessage setPassword(TileEntityEnder te, int slot, ItemStack stack) {
        PacketPassword msg = new PacketPassword(te);
        msg.slot = slot;
        msg.stack = stack;
        msg.setLabel = false;
        return msg;
    }

    public static PacketPassword setLabel(TileEntityEnder te, ItemStack stack) {
        PacketPassword msg = new PacketPassword(te);
        msg.slot = 0;
        msg.stack = stack;
        msg.setLabel = true;
        return msg;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        slot = buf.readShort();
        setLabel = buf.readBoolean();
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(slot);
        buf.writeBoolean(setLabel);
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static class Handler implements IMessageHandler<PacketPassword, IMessage> {

        @Override
        public IMessage onMessage(PacketPassword msg, MessageContext ctx) {
            TileEntityEnder te = msg.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
            if (te instanceof ITravelAccessable) {
                if (((ITravelAccessable) te).canUiBeAccessed(ctx.getServerHandler().playerEntity)) {
                    if (msg.stack != null) {
                        msg.stack.stackSize = 0;
                    }
                    if (msg.setLabel) {
                        ((ITravelAccessable) te).setItemLabel(msg.stack);
                    } else {
                        ((ITravelAccessable) te).getPassword()[msg.slot] = msg.stack;
                        ((ITravelAccessable) te).clearAuthorisedUsers();
                    }
                    te.getWorldObj().markBlockForUpdate(msg.x, msg.y, msg.z);
                }
            }
            return null;
        }
    }
}
