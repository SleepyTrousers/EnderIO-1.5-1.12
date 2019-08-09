package crazypants.enderio.machines.machine.spawner;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSpawnerUpdateNotification extends MessageTileEntity<TilePoweredSpawner> {

  private Set<SpawnerNotification> notification;

  public PacketSpawnerUpdateNotification() {
  }

  public PacketSpawnerUpdateNotification(@Nonnull TilePoweredSpawner tile, @Nonnull Set<SpawnerNotification> notification) {
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

  public static class Handler implements IMessageHandler<PacketSpawnerUpdateNotification, IMessage> {

    @Override
    public IMessage onMessage(PacketSpawnerUpdateNotification message, MessageContext ctx) {
      TilePoweredSpawner te = message.getTileEntity(message.getWorld(ctx));
      if (te != null) {
        te.replaceNotification(message.notification);
      }
      return null;
    }
  }
}
