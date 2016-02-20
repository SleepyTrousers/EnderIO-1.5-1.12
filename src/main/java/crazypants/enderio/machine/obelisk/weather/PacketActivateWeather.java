package crazypants.enderio.machine.obelisk.weather;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketActivateWeather extends MessageTileEntity<TileWeatherObelisk> implements IMessageHandler<PacketActivateWeather, IMessage> {

  public PacketActivateWeather() {
  }

  int taskid;

  public PacketActivateWeather(TileWeatherObelisk te, TileWeatherObelisk.WeatherTask task) {
    super(te);
    this.taskid = task == null ? -1 : task.ordinal();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(taskid);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    taskid = buf.readInt();
  }
  
  @Override
  public IMessage onMessage(PacketActivateWeather message, MessageContext ctx) {
    TileWeatherObelisk te = message.getTileEntity(ctx.side.isServer() ? message.getWorld(ctx) : EnderIO.proxy.getClientWorld());
    if (te != null) {
      te.startTask(message.taskid);
    }
    return null;
  }
}
