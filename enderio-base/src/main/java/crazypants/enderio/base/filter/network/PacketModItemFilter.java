package crazypants.enderio.base.filter.network;

import javax.annotation.Nonnull;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.ModItemFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketModItemFilter extends PacketFilterUpdate {

  private int index;
  private String name;

  public PacketModItemFilter() {
  }

  public PacketModItemFilter(TileEntity te, @Nonnull IItemFilter filter, int filterId, int param1, int index, String name) {
    super(te, filter, filterId, param1);
    this.index = index;
    this.name = name;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    index = buf.readInt();
    boolean isNull = buf.readBoolean();
    if (isNull) {
      name = null;
    } else {
      name = ByteBufUtils.readUTF8String(buf);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(index);
    buf.writeBoolean(name == null);
    if (name != null) {
      ByteBufUtils.writeUTF8String(buf, name);
    }
  }

  public static class Handler implements IMessageHandler<PacketModItemFilter, IMessage> {

    @Override
    public IMessage onMessage(PacketModItemFilter message, MessageContext ctx) {
      IFilterHolder<IFilter> filterHolder = message.getFilterHolder(ctx);
      if (filterHolder == null) {
        return null;
      }
      ModItemFilter filter = (ModItemFilter) filterHolder.getFilter(message.filterId, message.param1);

      filter.setMod(message.index, message.name);

      filterHolder.setFilter(message.filterId, message.param1, filter);

      return null;
    }
  }
}
