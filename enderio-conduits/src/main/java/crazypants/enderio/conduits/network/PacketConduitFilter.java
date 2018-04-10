package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConduitFilter extends AbstractConduitPacket<IConduit> {

  private EnumFacing dir;
  private IFilter inputFilter;
  private IFilter outputFilter;

  public PacketConduitFilter() {

  }

  public PacketConduitFilter(@Nonnull IConduit con, @Nonnull EnumFacing dir) {
    super(con);
    this.dir = dir;

    if (con.hasCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
      IFilterHolder<IFilter> filterHolder = con.getCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      inputFilter = filterHolder.getFilter(filterHolder.getInputFilterIndex(), dir.ordinal());
      outputFilter = filterHolder.getFilter(filterHolder.getOutputFilterIndex(), dir.ordinal());
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if (dir == null) {
      buf.writeShort(-1);
    } else {
      buf.writeShort(dir.ordinal());
    }
    FilterRegistry.writeFilter(buf, inputFilter);
    FilterRegistry.writeFilter(buf, outputFilter);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    if (ord < 0) {
      dir = null;
    } else {
      dir = EnumFacing.values()[ord];
    }
    inputFilter = FilterRegistry.readFilter(buf);
    outputFilter = FilterRegistry.readFilter(buf);
  }

  public static class Handler implements IMessageHandler<PacketConduitFilter, IMessage> {

    @Override
    public IMessage onMessage(PacketConduitFilter message, MessageContext ctx) {
      IConduit conduit = message.getConduit(ctx);
      if (conduit != null) {
        applyFilter(message.dir, conduit, message.inputFilter, true);
        applyFilter(message.dir, conduit, message.outputFilter, false);

        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }

    private void applyFilter(EnumFacing dir, IConduit conduit, IFilter filter, boolean isInput) {
      if (conduit.hasCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
        IFilterHolder<IFilter> filterHolder = conduit.getCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
        if (isInput) {
          filterHolder.setFilter(filterHolder.getInputFilterIndex(), dir.ordinal(), filter);
        } else {
          filterHolder.setFilter(filterHolder.getOutputFilterIndex(), dir.ordinal(), filter);
        }
      }
    }
  }

}
