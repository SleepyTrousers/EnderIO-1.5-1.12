package crazypants.enderio.machines.machine.niard;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSFXFluidFizzle implements IMessage {

  private NBTTagCompound tag;
  private @Nonnull BlockPos pos = BlockPos.ORIGIN;

  public PacketSFXFluidFizzle() {
  }

  public PacketSFXFluidFizzle(@Nonnull FluidStack fluid, @Nonnull BlockPos pos) {
    this.tag = fluid.writeToNBT(new NBTTagCompound());
    this.pos = pos;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeTag(buf, tag);
    buf.writeLong(pos.toLong());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    tag = ByteBufUtils.readTag(buf);
    pos = BlockPos.fromLong(buf.readLong());
  }

  public static class Handler implements IMessageHandler<PacketSFXFluidFizzle, IMessage> {

    @Override
    public IMessage onMessage(PacketSFXFluidFizzle message, MessageContext ctx) {
      World world = EnderIO.proxy.getClientWorld();
      FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(message.tag);
      if (fluidStack != null && world != null && world.isBlockLoaded(message.pos)) {
        fluidStack.getFluid().vaporize(EnderIO.proxy.getClientPlayer(), world, message.pos, fluidStack);
      }
      return null;
    }
  }

}