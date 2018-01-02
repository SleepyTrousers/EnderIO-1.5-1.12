package crazypants.enderio.machines.machine.obelisk.weather;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketActivateWeather extends MessageTileEntity<TileWeatherObelisk> {

  boolean start;

  public PacketActivateWeather() {
  }

  public PacketActivateWeather(@Nonnull TileWeatherObelisk te, boolean start) {
    super(te);
    this.start = start;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(start);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    start = buf.readBoolean();
  }

  public static class Handler implements IMessageHandler<PacketActivateWeather, IMessage> {

    @Override
    public IMessage onMessage(PacketActivateWeather message, MessageContext ctx) {
      TileWeatherObelisk te = message.getTileEntity(ctx.side.isServer() ? message.getWorld(ctx) : EnderIO.proxy.getClientWorld());
      if (te != null) {
        if (message.start) {
          te.startTask();
        } else {
          te.stopTask();
        }
        if (ctx.side.isServer()) {
          return new PacketActivateWeather(te, te.getActiveTask() != null);
        }
      }
      return null;
    }
  }
}
