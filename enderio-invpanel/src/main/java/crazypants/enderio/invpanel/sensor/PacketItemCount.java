package crazypants.enderio.invpanel.sensor;

import com.enderio.core.common.network.MessageTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemCount extends MessageTileEntity<TileInventoryPanelSensor> {

  private int startCount;
  private int stopCount;

  public PacketItemCount() {
  }

  public PacketItemCount(TileInventoryPanelSensor tile) {
    super(tile);
    startCount = tile.getStartCount();
    stopCount = tile.getStopCount();
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    startCount = buf.readInt();
    stopCount = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(startCount);
    buf.writeInt(stopCount);
  }

  public static class Handler implements IMessageHandler<PacketItemCount, IMessage> {

    @Override
    public IMessage onMessage(PacketItemCount message, MessageContext ctx) {
      TileInventoryPanelSensor te = message.getTileEntity(ctx.getServerHandler().player.world);
      if (te != null) {

        te.setStartCount(message.startCount);
        te.setStopCount(message.stopCount);
      }
      return null;
    }
  }
}