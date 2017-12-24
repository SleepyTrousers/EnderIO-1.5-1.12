package crazypants.enderio.machines.machine.teleport.telepad.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFluidLevel extends MessageTileEntity<TileEntity> {

  private int level;

  public PacketFluidLevel() {
    super();
  }

  public PacketFluidLevel(TileTelePad te) {
    super(te.getTileEntity());
    level = te.getFluidAmount();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(level);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    level = buf.readInt();
  }

  public static class Handler implements IMessageHandler<PacketFluidLevel, IMessage> {

    @Override
    public IMessage onMessage(PacketFluidLevel message, MessageContext ctx) {
      TileEntity te = message.getTileEntity(ctx.side.isClient() ? EnderIO.proxy.getClientWorld() : message.getWorld(ctx));
      if (te instanceof TileTelePad) {
        TileTelePad tp = (TileTelePad) te;
        tp.setFluidAmount(message.level);
      }
      return null;
    }
  }
}