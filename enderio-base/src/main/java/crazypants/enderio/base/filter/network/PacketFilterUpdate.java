package crazypants.enderio.base.filter.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilterHolder;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import io.netty.buffer.ByteBuf;
import jline.internal.Log;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketFilterUpdate extends MessageTileEntity<TileEntity> {

  protected int filterId;
  protected int param1;
  protected IItemFilter filter;

  public PacketFilterUpdate() {
  }

  public PacketFilterUpdate(@Nonnull TileEntity te, @Nonnull IItemFilter filter, int filterId, int param1) {
    super(te);
    this.filter = filter;
    this.filterId = filterId;
    this.param1 = param1;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(filterId);
    buf.writeInt(param1);
    FilterRegistry.writeFilter(buf, filter);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    filterId = buf.readInt();
    param1 = buf.readInt();
    filter = FilterRegistry.readFilter(buf);
  }

  public IFilterHolder getFilterHolder(MessageContext ctx) {
    if (ctx.side == Side.SERVER) {
      if (ctx.getServerHandler().player.openContainer instanceof ContainerFilter) {
        final TileEntity tileEntity = ((ContainerFilter) ctx.getServerHandler().player.openContainer).getTileEntity();
        if (tileEntity == null || !tileEntity.getPos().equals(getPos())) {
          Log.warn("Player " + ctx.getServerHandler().player.getName() + " tried to manipulate a filter while another gui was open!");
          return null;
        } else {
          if (tileEntity instanceof IFilterHolder) {
            return (IFilterHolder) tileEntity;
          }
        }
      }
    }
    return null;
  }

  public static class Handler implements IMessageHandler<PacketFilterUpdate, IMessage> {

    @Override
    public IMessage onMessage(PacketFilterUpdate message, MessageContext ctx) {
      IFilterHolder filterHolder = message.getFilterHolder(ctx);
      if (filterHolder != null) {
        filterHolder.setFilter(message.filterId, message.param1, message.filter);
      }
      return null;
    }

  }

}
