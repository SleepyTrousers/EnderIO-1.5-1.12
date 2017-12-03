package crazypants.enderio.base.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfigSyncNew implements IMessage {

  protected ValueFactory factory;
  protected ByteBuf bufferCopy;

  public PacketConfigSyncNew() {
    this.factory = null;
  }

  PacketConfigSyncNew(ValueFactory factory) {
    this.factory = factory;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    factory.save(buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    bufferCopy = buf.copy();
  }

  public static class PacketConfigSyncNewHandler implements IMessageHandler<PacketConfigSyncNew, IMessage> {

    protected ValueFactory factory;

    public PacketConfigSyncNewHandler(ValueFactory factory) {
      this.factory = factory;
    }

    @Override
    public IMessage onMessage(PacketConfigSyncNew message, MessageContext ctx) {
      if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
        factory.read(message.bufferCopy);
      }
      return null;
    }
    
  }
}
