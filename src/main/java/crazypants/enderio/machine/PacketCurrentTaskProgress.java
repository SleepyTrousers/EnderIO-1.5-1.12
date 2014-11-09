package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;

public class PacketCurrentTaskProgress extends MessageTileEntity<AbstractPoweredTaskEntity> implements IMessageHandler<PacketCurrentTaskProgress, IMessage>  {

  private float progress;

  public PacketCurrentTaskProgress() {
  }

  public PacketCurrentTaskProgress(AbstractPoweredTaskEntity tile) {
    super(tile);
    progress = -1;
    if(tile.getCurrentTask() != null) {      
      progress = tile.getCurrentTask().getProgress();
    } 
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeFloat(progress);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    progress = buf.readFloat();
  }

  @Override
  public IMessage onMessage(PacketCurrentTaskProgress message, MessageContext ctx) {
    AbstractPoweredTaskEntity tile = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if(tile != null) {
      if(message.progress < 0) {
        tile.currentTask = null;
      } else {
        tile.currentTask = new PoweredTaskProgress(message.progress);
      }
    }
    return null;
  }
}
