package crazypants.enderio.invpanel.network.sensor;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.invpanel.sensor.TileInventoryPanelSensor;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemCount extends MessageTileEntity<TileInventoryPanelSensor> {

  private int startCount;
  private int stopCount;

  public PacketItemCount() {
  }

  public PacketItemCount(@Nonnull TileInventoryPanelSensor tile) {
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
    public IMessage onMessage(@Nonnull PacketItemCount message, @Nonnull MessageContext ctx) {
      TileInventoryPanelSensor te = message.getTileEntity(ctx.getServerHandler().player.world);
      if (te != null) {

        te.setStartCount(message.startCount);
        te.setStopCount(message.stopCount);
      }
      return null;
    }
  }
}