package crazypants.enderio.base.filter.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.ITileFilterContainer;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import io.netty.buffer.ByteBuf;
import jline.internal.Log;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketFilterUpdate extends MessageTileEntity<TileEntity> {

  protected int filterId;
  protected int param1;
  protected IFilter filter;

  public PacketFilterUpdate() {
  }

  public PacketFilterUpdate(@Nonnull TileEntity te, @Nonnull IFilter filter, int filterId, int param1) {
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

  public ITileFilterContainer getFilterContainer(MessageContext ctx) {
    if (ctx.side == Side.SERVER) {
      if (ctx.getServerHandler().player.openContainer instanceof ContainerFilter) {
        final TileEntity tileEntity = ((ContainerFilter) ctx.getServerHandler().player.openContainer).getTileEntity();
        if (tileEntity == null || !tileEntity.getPos().equals(getPos())) {
          Log.warn("Player " + ctx.getServerHandler().player.getName() + " tried to manipulate a filter while another gui was open!");
          return null;
        } else {
          if (tileEntity instanceof ITileFilterContainer) {
            return (ITileFilterContainer) tileEntity;
          }
        }
      }
    }
    return null;
  }

  public IFilterHolder<IFilter> getFilterHolderCapability(MessageContext ctx) {
    if (ctx.side == Side.SERVER) {
      if (ctx.getServerHandler().player.openContainer instanceof ContainerFilter) {
        final TileEntity tileEntity = ((ContainerFilter) ctx.getServerHandler().player.openContainer).getTileEntity();
        if (tileEntity == null || !tileEntity.getPos().equals(getPos())) {
          Log.warn("Player " + ctx.getServerHandler().player.getName() + " tried to manipulate a filter while another gui was open!");
          return null;
        } else {
          if (tileEntity.hasCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, EnumFacing.getFront(param1))) {
            return tileEntity.getCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, EnumFacing.getFront(param1));
          }
        }
      }
    }
    return null;
  }

  public static class Handler implements IMessageHandler<PacketFilterUpdate, IMessage> {

    @Override
    public IMessage onMessage(PacketFilterUpdate message, MessageContext ctx) {
      ITileFilterContainer filterContainer = message.getFilterContainer(ctx);
      if (filterContainer != null) {
        filterContainer.setFilter(message.filterId, message.param1, message.filter);
      }
      return null;
    }

  }

}
