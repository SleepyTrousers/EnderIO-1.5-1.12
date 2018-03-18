package crazypants.enderio.base.config.factory;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfigSyncNew implements IMessage {

  protected IValueFactory factory;
  protected String modid, section;
  protected ByteBuf bufferCopy;

  public PacketConfigSyncNew() {
    this.factory = null;
  }

  PacketConfigSyncNew(IValueFactory factory) {
    this.factory = factory;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, factory.getModid());
    ByteBufUtils.writeUTF8String(buf, factory.getSection());
    factory.save(buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    modid = ByteBufUtils.readUTF8String(buf);
    section = ByteBufUtils.readUTF8String(buf);
    bufferCopy = buf.copy();
  }

  public static class PacketConfigSyncNewHandler implements IMessageHandler<PacketConfigSyncNew, IMessage> {
    @Override
    public IMessage onMessage(PacketConfigSyncNew message, MessageContext ctx) {
      if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
        FactoryManager.read(message.modid, message.section, message.bufferCopy);
      }
      if (message.bufferCopy != null) {
        message.bufferCopy.release();
      }
      return null;
    }
  }

}
