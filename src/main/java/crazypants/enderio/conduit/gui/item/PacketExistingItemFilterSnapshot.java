package crazypants.enderio.conduit.gui.item;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.enderio.conduit.item.filter.ExistingItemFilter;
import crazypants.enderio.conduit.packet.AbstractConduitPacket;
import crazypants.enderio.conduit.packet.ConTypeEnum;
import io.netty.buffer.ByteBuf;

public class PacketExistingItemFilterSnapshot extends AbstractConduitPacket<IItemConduit>
        implements IMessageHandler<PacketExistingItemFilterSnapshot, IMessage> {

    public static enum Opcode {
        CLEAR,
        SET,
        MERGE,
        SET_BLACK,
        UNSET_BLACK
    }

    private ForgeDirection dir;
    private Opcode opcode;
    private boolean isInput;

    public PacketExistingItemFilterSnapshot() {}

    public PacketExistingItemFilterSnapshot(IItemConduit con, ForgeDirection dir, boolean isInput, Opcode opcode) {
        super(con.getBundle().getEntity(), ConTypeEnum.ITEM);
        this.dir = dir;
        this.isInput = isInput;
        this.opcode = opcode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        dir = ForgeDirection.values()[buf.readShort()];
        isInput = buf.readBoolean();
        opcode = Opcode.values()[buf.readByte() & 255];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(dir.ordinal());
        buf.writeBoolean(isInput);
        buf.writeByte(opcode.ordinal());
    }

    @Override
    public PacketExistingItemFilterSnapshot onMessage(PacketExistingItemFilterSnapshot message, MessageContext ctx) {
        if (isInvalidPacketForGui(message, ctx)) return null;
        IItemConduit conduit = message.getTileCasted(ctx);
        if (conduit == null) {
            return null;
        }
        ExistingItemFilter filter;
        if (message.isInput) {
            filter = (ExistingItemFilter) conduit.getInputFilter(message.dir);
        } else {
            filter = (ExistingItemFilter) conduit.getOutputFilter(message.dir);
        }

        switch (message.opcode) {
            case CLEAR:
                filter.setSnapshot((List<ItemStack>) null);
                System.out.println("PacketExistingItemFilterSnapshot.onMessage: Cleared snapshot");
                break;

            case SET: {
                ItemConduitNetwork icn = (ItemConduitNetwork) conduit.getNetwork();
                NetworkedInventory inv = icn.getInventory(conduit, message.dir);
                inv.updateInventory();
                filter.setSnapshot(inv);
                break;
            }

            case MERGE: {
                ItemConduitNetwork icn = (ItemConduitNetwork) conduit.getNetwork();
                NetworkedInventory inv = icn.getInventory(conduit, message.dir);
                filter.mergeSnapshot(inv);
                break;
            }

            case SET_BLACK:
                filter.setBlacklist(true);
                break;
            case UNSET_BLACK:
                filter.setBlacklist(false);
                break;

            default:
                throw new AssertionError();
        }

        if (message.isInput) {
            conduit.setInputFilter(message.dir, filter);
        } else {
            conduit.setOutputFilter(message.dir, filter);
        }

        return null;
    }
}
