package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.conduits.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEnderLiquidConduit extends PacketConduitFilter<EnderLiquidConduit> {

  private @Nonnull DyeColor colIn = DyeColor.BLACK;
  private @Nonnull DyeColor colOut = DyeColor.BLACK;
  private int priority;
  private boolean roundRobin;
  private boolean selfFeed;

  public PacketEnderLiquidConduit() {
  }

  public PacketEnderLiquidConduit(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing dir) {
    super(con, dir);
    colIn = con.getInputColor(dir);
    colOut = con.getOutputColor(dir);
    priority = con.getOutputPriority(dir);
    roundRobin = con.isRoundRobinEnabled(dir);
    selfFeed = con.isSelfFeedEnabled(dir);
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    buf.writeShort(colIn.ordinal());
    buf.writeShort(colOut.ordinal());
    buf.writeInt(priority);
    buf.writeBoolean(roundRobin);
    buf.writeBoolean(selfFeed);
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    colIn = EnumReader.get(DyeColor.class, buf.readShort());
    colOut = EnumReader.get(DyeColor.class, buf.readShort());
    priority = buf.readInt();
    roundRobin = buf.readBoolean();
    selfFeed = buf.readBoolean();
  }

  public static class Handler implements IMessageHandler<PacketEnderLiquidConduit, IMessage> {

    @Override
    public IMessage onMessage(PacketEnderLiquidConduit message, MessageContext ctx) {
      EnderLiquidConduit conduit = message.getConduit(ctx);
      if (conduit != null) {
        conduit.setInputColor(message.dir, message.colIn);
        conduit.setOutputColor(message.dir, message.colOut);
        conduit.setOutputPriority(message.dir, message.priority);
        conduit.setRoundRobinEnabled(message.dir, message.roundRobin);
        conduit.setSelfFeedEnabled(message.dir, message.selfFeed);
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
