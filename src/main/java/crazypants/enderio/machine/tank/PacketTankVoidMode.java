package crazypants.enderio.machine.tank;

import com.enderio.core.common.network.MessageTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTankVoidMode extends MessageTileEntity<TileTank> implements IMessageHandler<PacketTankVoidMode, IMessage> {

  public PacketTankVoidMode() {
  }

  private VoidMode mode;

  public PacketTankVoidMode(TileTank tank) {
    super(tank);
    this.mode = tank.getVoidMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    mode = VoidMode.values()[buf.readByte()];
  }

  @Override
  public IMessage onMessage(PacketTankVoidMode message, MessageContext ctx) {
    TileTank te = message.getTileEntity(ctx.getServerHandler().playerEntity.world);
    if (te != null) {
      te.setVoidMode(message.mode);
    }
    return null;
  }
}
