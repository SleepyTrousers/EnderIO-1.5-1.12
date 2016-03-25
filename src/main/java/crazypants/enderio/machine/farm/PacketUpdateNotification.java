package crazypants.enderio.machine.farm;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;

public class PacketUpdateNotification extends MessageTileEntity<TileFarmStation> implements IMessageHandler<PacketUpdateNotification, IMessage> {

  private String notification;

  public PacketUpdateNotification() {
  }

  public PacketUpdateNotification(TileFarmStation tile, String notification) {
    super(tile);
    this.notification = notification;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    notification = ByteBufUtils.readUTF8String(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    ByteBufUtils.writeUTF8String(buf, notification);
  }

  @Override
  public IMessage onMessage(PacketUpdateNotification message, MessageContext ctx) {
    TileFarmStation te = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if(te != null) {
      te.notification = message.notification;
    }
    return null;
  }
}
