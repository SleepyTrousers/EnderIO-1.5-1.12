package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import crazypants.enderio.base.filter.fluid.FluidFilter;
import crazypants.enderio.base.filter.fluid.IFluidFilter;
import crazypants.enderio.conduits.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduits.conduit.liquid.ILiquidConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFluidFilter extends AbstractConduitPacket.Sided<ILiquidConduit> {

  private boolean isInput;
  private @Nonnull IFluidFilter filter = new FluidFilter();

  public PacketFluidFilter() {
  }

  public PacketFluidFilter(@Nonnull EnderLiquidConduit eConduit, @Nonnull EnumFacing dir, @Nonnull IFluidFilter filter, boolean isInput) {
    super(eConduit, dir);
    this.filter = filter;
    this.isInput = isInput;
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    buf.writeBoolean(isInput);
    NBTTagCompound tag = new NBTTagCompound();
    filter.writeToNBT(tag);
    ByteBufUtils.writeTag(buf, tag);
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    isInput = buf.readBoolean();
    NBTTagCompound tag = ByteBufUtils.readTag(buf);
    if (tag != null) {
      filter.readFromNBT(tag);
    }
  }

  public static class Handler implements IMessageHandler<PacketFluidFilter, IMessage> {

    @Override
    public IMessage onMessage(PacketFluidFilter message, MessageContext ctx) {
      ILiquidConduit conduit = message.getConduit(ctx);
      if (!(conduit instanceof EnderLiquidConduit)) {
        return null;
      }
      EnderLiquidConduit eCon = (EnderLiquidConduit) conduit;
      eCon.setFilter(message.dir, message.filter, message.isInput);

      IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
      message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      return null;
    }
  }

}
