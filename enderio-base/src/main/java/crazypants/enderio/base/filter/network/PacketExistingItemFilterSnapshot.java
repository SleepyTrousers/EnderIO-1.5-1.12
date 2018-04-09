package crazypants.enderio.base.filter.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.item.ExistingItemFilter;
import crazypants.enderio.base.filter.item.IItemFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.IItemHandler;

public class PacketExistingItemFilterSnapshot extends PacketFilterUpdate {

  public static enum Opcode {
    CLEAR,
    SET,
    MERGE,
  }

  private Opcode opcode;

  public PacketExistingItemFilterSnapshot() {
  }

  public PacketExistingItemFilterSnapshot(TileEntity te, @Nonnull IItemFilter filter, int filterId, int param1, Opcode opcode) {
    super(te, filter, filterId, param1);
    this.opcode = opcode;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    opcode = Opcode.values()[buf.readByte() & 255];
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(opcode.ordinal());
  }

  public static class Handler implements IMessageHandler<PacketExistingItemFilterSnapshot, IMessage> {

    @Override
    public PacketExistingItemFilterSnapshot onMessage(PacketExistingItemFilterSnapshot message, MessageContext ctx) {
      IFilterHolder<IFilter> filterHolder = message.getFilterHolder(ctx);
      if (filterHolder == null) {
        return null;
      }
      ExistingItemFilter filter = (ExistingItemFilter) filterHolder.getFilter(message.filterId, message.param1);

      switch (message.opcode) {
      case CLEAR:
        filter.setSnapshot((NNList<ItemStack>) null);
        break;

      case SET: {
        IItemHandler inv = filterHolder.getInventoryForSnapshot(message.filterId, message.param1);
        if (inv != null) {
          filter.setSnapshot(inv);
        }
        break;
      }

      case MERGE: {
        IItemHandler inv = filterHolder.getInventoryForSnapshot(message.filterId, message.param1);
        if (inv != null) {
          filter.mergeSnapshot(inv);
        }
        break;
      }

      default:
        throw new AssertionError();
      }

      filterHolder.setFilter(message.filterId, message.param1, filter);

      return null;
    }
  }

}
