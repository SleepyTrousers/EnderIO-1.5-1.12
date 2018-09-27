package crazypants.enderio.base.filter.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.ITileFilterContainer;
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

  public PacketExistingItemFilterSnapshot(@Nonnull TileEntity te, @Nonnull IItemFilter filter, int filterId, int param1, @Nonnull Opcode opcode) {
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
      ITileFilterContainer filterContainer = message.getFilterContainer(ctx);
      if (filterContainer == null) {
        return null;
      }
      final IFilter filter = filterContainer.getFilter(message.filterId, message.param1);
      if (!(filter instanceof ExistingItemFilter)) {
        return null;
      }
      final ExistingItemFilter existingItemFilter = (ExistingItemFilter) filter;

      switch (message.opcode) {
      case CLEAR:
        existingItemFilter.setSnapshot((NNList<ItemStack>) null);
        break;

      case SET: {
        IItemHandler inv = filterContainer.getInventoryForSnapshot(message.filterId, message.param1);
        if (inv != null) {
          existingItemFilter.setSnapshot(inv);
        }
        break;
      }

      case MERGE: {
        IItemHandler inv = filterContainer.getInventoryForSnapshot(message.filterId, message.param1);
        if (inv != null) {
          existingItemFilter.mergeSnapshot(inv);
        }
        break;
      }

      default:
        throw new AssertionError();
      }

      filterContainer.setFilter(message.filterId, message.param1, existingItemFilter);

      return null;
    }
  }

}
