package crazypants.enderio.machines.machine.obelisk.weather;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketActivateWeather extends MessageTileEntity<TileWeatherObelisk> {

  public PacketActivateWeather() {
  }

  public PacketActivateWeather(@Nonnull TileWeatherObelisk te) {
    super(te);
  }

  public static class Handler implements IMessageHandler<PacketActivateWeather, IMessage> {

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
}
