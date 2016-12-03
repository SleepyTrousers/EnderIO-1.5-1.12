package crazypants.enderio.machine.spawner;

import io.netty.buffer.ByteBuf;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.spawner.TilePoweredSpawner.SpawnResult;

public class PacketUpdateNotification extends MessageTileEntity<TilePoweredSpawner> implements IMessage,
    IMessageHandler<PacketUpdateNotification, IMessage> {

  private SpawnResult notification;

  public PacketUpdateNotification() {
  }

  public PacketUpdateNotification(TilePoweredSpawner tile, SpawnResult notification) {
    super(tile);
    this.notification = notification;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ordinal = buf.readShort();
    notification = SpawnResult.values()[ordinal];
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort((short) notification.ordinal());
  }

  @Override
  public IMessage onMessage(PacketUpdateNotification message, MessageContext ctx) {
    TilePoweredSpawner te = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if(te != null) {
      te.setReason(message.notification);
    }
    return null;
  }
}
