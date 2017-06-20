package crazypants.enderio.machine.spawner;

import com.enderio.core.common.network.MessageTileEntity;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.EnumSet;
import java.util.Set;

public class PacketUpdateNotification extends MessageTileEntity<TilePoweredSpawner> implements IMessageHandler<PacketUpdateNotification, IMessage> {

  private Set<SpawnerNotification> notification;

  public PacketUpdateNotification() {
  }

  public PacketUpdateNotification(TilePoweredSpawner tile, Set<SpawnerNotification> notification) {
    super(tile);
    this.notification = notification;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    notification = EnumSet.noneOf(SpawnerNotification.class);
    int count = buf.readByte();
    for (int i = 0; i < count; i++) {
      notification.add(SpawnerNotification.values()[buf.readByte()]);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(notification.size());
    for (SpawnerNotification farmNotification : notification) {
      buf.writeByte(farmNotification.ordinal());
    }
  }

  @Override
  public IMessage onMessage(PacketUpdateNotification message, MessageContext ctx) {
    TilePoweredSpawner te = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if (te != null) {
      te.replaceNotification(message.notification);
    }
    return null;
  }
}
