package crazypants.enderio.base.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfigSyncNew implements IMessage {

  protected ValueFactory factory;
  protected String factoryName;
  protected ByteBuf bufferCopy;

  public PacketConfigSyncNew() {
    this.factory = null;
  }

  PacketConfigSyncNew(ValueFactory factory) {
    this.factory = factory;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, factory.getModid());
    factory.save(buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    factoryName = ByteBufUtils.readUTF8String(buf);
    bufferCopy = buf.copy();
  }

  public static class PacketConfigSyncNewHandler implements IMessageHandler<PacketConfigSyncNew, IMessage> {
    @Override
    public IMessage onMessage(PacketConfigSyncNew message, MessageContext ctx) {
      if (true || !Minecraft.getMinecraft().isIntegratedServerRunning()) {
        ValueFactory.read(message.factoryName, message.bufferCopy);
      }
      if (message.bufferCopy != null) {
        message.bufferCopy.release();
      }
      return null;
    }
  }

}
