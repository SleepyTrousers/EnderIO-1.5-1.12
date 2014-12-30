package crazypants.enderio.machine.weather;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.MessageTileEntity;

public class PacketActivateWeather extends MessageTileEntity<TileWeatherObelisk> implements IMessageHandler<PacketActivateWeather, IMessage> {

  public PacketActivateWeather() {
  }

  int taskid;

  public PacketActivateWeather(TileWeatherObelisk te, TileWeatherObelisk.Task task) {
    super(te);
    this.taskid = task.ordinal();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(taskid);
  }

  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    taskid = buf.readInt();
  }
  
  @Override
  public IMessage onMessage(PacketActivateWeather message, MessageContext ctx) {
    TileWeatherObelisk te = message.getTileEntity(message.getWorld(ctx));
    if (te != null) {
      te.startTask(message.taskid);
    }
    return null;
  }
}
