package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.conduits.conduit.liquid.EnderLiquidConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEnderLiquidConduit extends PacketConduitFilter<EnderLiquidConduit> {

  private DyeColor colIn;
  private DyeColor colOut;

  public PacketEnderLiquidConduit() {
  }

  public PacketEnderLiquidConduit(@Nonnull EnderLiquidConduit con, @Nonnull EnumFacing dir) {
    super(con, dir);
    colIn = con.getInputColor(dir);
    colOut = con.getOutputColor(dir);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(colIn.ordinal());
    buf.writeShort(colOut.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    colIn = DyeColor.values()[buf.readShort()];
    colOut = DyeColor.values()[buf.readShort()];
  }

  public static class Handler implements IMessageHandler<PacketEnderLiquidConduit, IMessage> {

    @Override
    public IMessage onMessage(PacketEnderLiquidConduit message, MessageContext ctx) {
      EnderLiquidConduit conduit = message.getConduit(ctx);
      if (conduit != null) {
        conduit.setInputColor(message.dir, message.colIn);
        conduit.setOutputColor(message.dir, message.colOut);
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
