package crazypants.enderio.machine.weather;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.weather.TileWeatherObelisk.Task;

public class PacketFinishWeather extends PacketActivateWeather {

  public PacketFinishWeather() {}
  
  public PacketFinishWeather(TileWeatherObelisk te, Task task) {
    super(te, task);
  }
  
  public static class Handler implements IMessageHandler<PacketFinishWeather, IMessage> {

    @Override
    public IMessage onMessage(PacketFinishWeather message, MessageContext ctx) {
      TileWeatherObelisk te = message.getTileEntity(EnderIO.proxy.getClientWorld());
      if (te != null) {
        te.activateClientParticles();
      }
      return null;
    }
  }
}
