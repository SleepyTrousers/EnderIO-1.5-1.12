package crazypants.enderio.conduit.packet;

import java.util.List;

import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.enderio.conduit.item.filter.ExistingItemFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExistingItemFilterSnapshot extends AbstractConduitPacket<IItemConduit> implements IMessageHandler<PacketExistingItemFilterSnapshot, IMessage> {

  public static enum Opcode {
    CLEAR,
    SET,
    MERGE,
    SET_BLACK,
    UNSET_BLACK
  }

  private EnumFacing dir;
  private Opcode opcode;
  private boolean isInput;

  public PacketExistingItemFilterSnapshot() {
  }
  
  public PacketExistingItemFilterSnapshot(IItemConduit con, EnumFacing dir, boolean isInput, Opcode opcode) {
    super(con.getBundle().getEntity(), con);
    this.dir = dir;
    this.isInput= isInput;
    this.opcode = opcode;
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    dir = EnumFacing.values()[buf.readShort()];
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
    IItemConduit conduit = message.getTileCasted(ctx);
    if(conduit == null) {
      return null;
    }
    ExistingItemFilter filter;
    if(message.isInput) {
      filter = (ExistingItemFilter)conduit.getInputFilter(message.dir);  
    } else {
      filter = (ExistingItemFilter)conduit.getOutputFilter(message.dir);
    }
    
    switch (message.opcode) {
      case CLEAR:
        filter.setSnapshot((List<ItemStack>)null);
        break;

      case SET: {
        ItemConduitNetwork icn = (ItemConduitNetwork)conduit.getNetwork();
        NetworkedInventory inv = icn.getInventory(conduit, message.dir);
        filter.setSnapshot(inv);
        break;
      }

      case MERGE: {
        ItemConduitNetwork icn = (ItemConduitNetwork)conduit.getNetwork();
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

    if(message.isInput) {
      conduit.setInputFilter(message.dir, filter);  
    } else {
      conduit.setOutputFilter(message.dir, filter);
    }
    
    return null;
  }

}
