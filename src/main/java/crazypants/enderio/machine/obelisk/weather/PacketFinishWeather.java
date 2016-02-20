package crazypants.enderio.machine.obelisk.weather;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.obelisk.weather.TileWeatherObelisk.WeatherTask;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFinishWeather extends PacketActivateWeather {

  public PacketFinishWeather() {}
  
  public PacketFinishWeather(TileWeatherObelisk te, WeatherTask task) {
    super(te, task);
  }
  
  public static class Handler implements IMessageHandler<PacketFinishWeather, IMessage> {

    @Override
    public IMessage onMessage(PacketFinishWeather message, MessageContext ctx) {
      TileWeatherObelisk te = message.getTileEntity(EnderIO.proxy.getClientWorld());
      if(te != null) {
        te.activateClientParticles(WeatherTask.values()[message.taskid]);
        te.powerUsed = 0;
      }
      return null;
    }
  }
}
