package crazypants.enderio.machines.machine.farm;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateNotification extends MessageTileEntity<TileFarmStation> {

  private Set<FarmNotification> notification;

  public PacketUpdateNotification() {
  }

  public PacketUpdateNotification(@Nonnull TileFarmStation tile, @Nonnull Set<FarmNotification> notification) {
    super(tile);
    this.notification = notification;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    notification = EnumSet.noneOf(FarmNotification.class);
    int count = buf.readByte();
    for (int i = 0; i < count; i++) {
      notification.add(FarmNotification.values()[buf.readByte()]);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(notification.size());
    for (FarmNotification farmNotification : notification) {
      buf.writeByte(farmNotification.ordinal());
    }
  }

  public static class Handler implements IMessageHandler<PacketUpdateNotification, IMessage> {

    @Override
    public IMessage onMessage(PacketUpdateNotification message, MessageContext ctx) {
      TileFarmStation te = message.getTileEntity(EnderIO.proxy.getClientWorld());
      if (te != null) {
        te.clearNotification();
        te.getNotification().addAll(message.notification);
      }
      return null;
    }
  }

}
