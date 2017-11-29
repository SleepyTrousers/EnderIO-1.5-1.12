package crazypants.enderio.machines.machine.obelisk.weather;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
