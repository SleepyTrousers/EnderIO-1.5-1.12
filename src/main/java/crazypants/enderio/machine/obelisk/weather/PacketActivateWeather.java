package crazypants.enderio.machine.obelisk.weather;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketActivateWeather extends MessageTileEntity<TileWeatherObelisk> implements IMessageHandler<PacketActivateWeather, IMessage> {

  public PacketActivateWeather() {
  }

  public PacketActivateWeather(TileWeatherObelisk te) {
    super(te);
  }
  
  @Override
  public IMessage onMessage(PacketActivateWeather message, MessageContext ctx) {
    TileWeatherObelisk te = message.getTileEntity(ctx.side.isServer() ? message.getWorld(ctx) : EnderIO.proxy.getClientWorld());
    if (te != null) {
      if (ctx.side.isServer()) {
        te.startTask();
      } else {
        te.stopTask();
      }
    }
    return null;
  }
}
