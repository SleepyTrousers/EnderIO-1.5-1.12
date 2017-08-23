package crazypants.enderio.enderface;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketLockClientContainer implements IMessage {

  private int windowId;
  private boolean lock = true;
  
  public PacketLockClientContainer() {
    this.lock = false;
  }
  
  public PacketLockClientContainer(int windowId) {
    this.windowId = windowId;
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    this.windowId = buf.readInt();
    this.lock = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(windowId);
    buf.writeBoolean(lock);
  }

  public static class Handler implements IMessageHandler<PacketLockClientContainer, IMessage> {

    @Override
    public IMessage onMessage(PacketLockClientContainer message, MessageContext ctx) {
      if (message.lock) {
        EnderIOController.INSTANCE.lockAndWaitForChange(message.windowId);
      } else {
        EnderIOController.INSTANCE.unlock();
      }
      return null;
    }
  }

}
