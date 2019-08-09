package crazypants.enderio.machines.machine.teleport.telepad.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTelePadFluidLevel extends MessageTileEntity<TileTelePad> {

  private int level;

  public PacketTelePadFluidLevel() {
    super();
  }

  public PacketTelePadFluidLevel(@Nonnull TileTelePad te) {
    super(te);
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

  public static class Handler implements IMessageHandler<PacketTelePadFluidLevel, IMessage> {

    @Override
    public IMessage onMessage(PacketTelePadFluidLevel message, MessageContext ctx) {
      TileTelePad te = message.getTileEntity(message.getWorld(ctx));
      if (te != null) {
        te.setFluidAmount(message.level);
      }
      return null;
    }
  }
}