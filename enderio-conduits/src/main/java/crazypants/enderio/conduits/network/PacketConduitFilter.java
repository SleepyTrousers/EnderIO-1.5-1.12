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

public class PacketConduitFilter<T extends IConduit> extends AbstractConduitPacket.Sided<T> {

  protected IFilter inputFilter;
  protected IFilter outputFilter;

  public PacketConduitFilter() {

  }

  public PacketConduitFilter(@Nonnull T con, @Nonnull EnumFacing dir) {
    super(con, dir);

    if (con.hasInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
      IFilterHolder<IFilter> filterHolder = con.getInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
      if (filterHolder != null) {
        inputFilter = filterHolder.getFilter(filterHolder.getInputFilterIndex(), dir.ordinal());
        outputFilter = filterHolder.getFilter(filterHolder.getOutputFilterIndex(), dir.ordinal());
      }
    }
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    FilterRegistry.writeFilter(buf, inputFilter);
    FilterRegistry.writeFilter(buf, outputFilter);
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    inputFilter = FilterRegistry.readFilter(buf);
    outputFilter = FilterRegistry.readFilter(buf);
  }

  @SuppressWarnings("rawtypes")
  public static class Handler implements IMessageHandler<PacketConduitFilter, IMessage> {

    @Override
    public IMessage onMessage(PacketConduitFilter message, MessageContext ctx) {
      IConduit conduit = message.getConduit(ctx);
      if (conduit != null) {
        final IFilter inputFilter = message.inputFilter;
        if (inputFilter != null) {
          applyFilter(message.dir, conduit, inputFilter, true);
        }
        final IFilter outputFilter = message.outputFilter;
        if (outputFilter != null) {
          applyFilter(message.dir, conduit, outputFilter, false);
        }

        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }

    private void applyFilter(@Nonnull EnumFacing dir, @Nonnull IConduit conduit, @Nonnull IFilter filter, boolean isInput) {
      if (conduit.hasInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir)) {
        IFilterHolder<IFilter> filterHolder = conduit.getInternalCapability(CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY, dir);
        if (filterHolder != null) {
          if (isInput) {
            filterHolder.setFilter(filterHolder.getInputFilterIndex(), dir.ordinal(), filter);
          } else {
            filterHolder.setFilter(filterHolder.getOutputFilterIndex(), dir.ordinal(), filter);
          }
        }
      }
    }
  }

}
