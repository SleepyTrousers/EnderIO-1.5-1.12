package crazypants.enderio.machine.monitor.v2;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.enderio.core.common.network.MessageTileEntity;

public class PacketPMon3 extends MessageTileEntity<TilePMon> {

  private boolean engineControlEnabled;
  private float startLevel, stopLevel;

  public PacketPMon3() {
  }

  public PacketPMon3(TilePMon tile, boolean engineControlEnabled, float startLevel, float stopLevel) {
    super(tile);
    this.engineControlEnabled = engineControlEnabled;
    this.startLevel = startLevel;
    this.stopLevel = stopLevel;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    engineControlEnabled = buf.readBoolean();
    startLevel = buf.readFloat();
    stopLevel = buf.readFloat();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(engineControlEnabled);
    buf.writeFloat(startLevel);
    buf.writeFloat(stopLevel);
  }

  public static class ServerHandler implements IMessageHandler<PacketPMon3, IMessage> {

    @Override
    public IMessage onMessage(PacketPMon3 msg, MessageContext ctx) {
      TilePMon te = msg.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
      if (te != null) {
        te.setEngineControlEnabled(msg.engineControlEnabled);
        te.setStartLevel(msg.startLevel);
        te.setStopLevel(msg.stopLevel);
      }
      return null;
    }
  }

}
