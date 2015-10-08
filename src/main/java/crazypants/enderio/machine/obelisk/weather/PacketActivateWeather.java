package crazypants.enderio.machine.obelisk.weather;

import io.netty.buffer.ByteBuf;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

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
