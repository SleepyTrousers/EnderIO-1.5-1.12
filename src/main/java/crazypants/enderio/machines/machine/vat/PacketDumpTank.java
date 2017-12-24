package crazypants.enderio.machines.machine.vat;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.machines.lang.Lang;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDumpTank extends MessageTileEntity<TileVat> {

  private int tank;

  public PacketDumpTank() {
    super();
  }

  public PacketDumpTank(@Nonnull TileVat te, int tank) {
    super(te);
    this.tank = tank;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(tank);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    tank = buf.readByte();
  }

  public static class Handler implements IMessageHandler<PacketDumpTank, IMessage> {

    @Override
    public IMessage onMessage(PacketDumpTank message, MessageContext ctx) {
      TileVat te = message.getTileEntity(ctx.getServerHandler().player.world);
      if (te != null) {
        if (message.tank == 2) {
          te.outputTank.setFluid(null);
        } else {
          if (te.inputTank.isEmpty()) {
            // NOP
          } else if (te.isActive()) {
            ctx.getServerHandler().player.sendMessage(Lang.GUI_VAT_DUMP_ACTIVE.toChatServer());
          } else if (te.outputTank.isEmpty()) {
            te.outputTank.setFluid(te.inputTank.getFluid());
            te.inputTank.setFluid(null);
          } else {
            final FluidStack fluidInOutput = te.outputTank.getFluid();
            if (fluidInOutput != null && fluidInOutput.isFluidEqual(te.inputTank.getFluid()) && !te.outputTank.isFull()) {
              FluidStack drain = te.inputTank.drainInternal(te.outputTank.getAvailableSpace(), true);
              te.outputTank.fill(drain, true);
            } else {
              ctx.getServerHandler().player.sendMessage(Lang.GUI_VAT_DUMP_FAIL.toChatServer());
            }
          }
        }
        te.markDirty();
        return new PacketTanks(te);
      }
      return null;
    }

  }
}