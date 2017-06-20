package crazypants.enderio.machine.vat;

import com.enderio.core.common.network.MessageTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDumpTank extends MessageTileEntity<TileVat> implements IMessageHandler<PacketDumpTank, IMessage> {

  private int tank;

  public PacketDumpTank() {
    super();
  }

  public PacketDumpTank(TileVat te, int tank) {
    super(te);
    this.tank = tank;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(tank);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    tank = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketDumpTank message, MessageContext ctx) {
    TileVat te = message.getTileEntity(ctx.getServerHandler().playerEntity.world);
    if (te != null) {
      if (message.tank == 2) {
        te.outputTank.setFluid(null);
      } else {
        if (te.inputTank.isEmpty()) {
          // NOP
        } else if (te.isActive()) {
          ctx.getServerHandler().playerEntity.addChatMessage(new TextComponentTranslation("enderio.gui.machine.vat.dump.active"));
        } else if (te.outputTank.isEmpty()) {
          te.outputTank.setFluid(te.inputTank.getFluid());
          te.inputTank.setFluid(null);
        } else if (te.outputTank.getFluid().isFluidEqual(te.inputTank.getFluid()) && te.outputTank.getAvailableSpace() > 0) {
          FluidStack drain = te.inputTank.drainInternal(te.outputTank.getAvailableSpace(), true);
          te.outputTank.fill(drain, true);
        } else {
          ctx.getServerHandler().playerEntity.addChatMessage(new TextComponentTranslation("enderio.gui.machine.vat.dump.fail"));
        }
      }
      te.markDirty();
      return new PacketTanks(te);
    }
    return null;
  }
}
