package crazypants.enderio.base.network;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ExecPacket implements IMessage {

  public interface IServerExec extends IForgeRegistryEntry<IServerExec>, Function<ByteBuf, Consumer<EntityPlayerMP>> {

  }

  private static IForgeRegistry<IServerExec> REGISTRY = null;

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<IServerExec>().setName(new ResourceLocation(EnderIO.DOMAIN, "remoteexec")).setType(IServerExec.class)
        .setIDRange(0, Short.MAX_VALUE).create();
  }

  private IServerExec remoteExec;
  private Consumer<ByteBuf> writer;
  private Consumer<EntityPlayerMP> runner;

  public static void send(IServerExec remoteExec, Consumer<ByteBuf> writer) {
    if (writer != null && remoteExec != null && REGISTRY != null) {
      PacketHandler.INSTANCE.sendToServer(new ExecPacket(remoteExec, writer));
    }
  }

  public ExecPacket(@Nonnull IServerExec remoteExec, @Nonnull Consumer<ByteBuf> writer) {
    this.remoteExec = remoteExec;
    this.writer = writer;
  }

  public ExecPacket() {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    if (buf != null && REGISTRY != null) {
      remoteExec = ByteBufUtils.readRegistryEntry(buf, REGISTRY);
      runner = remoteExec.apply(buf);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    if (buf != null && remoteExec != null) {
      ByteBufUtils.writeRegistryEntry(buf, remoteExec);
      writer.accept(buf);
    }
  }

  public static class Handler implements IMessageHandler<ExecPacket, IMessage> {

    @Override
    public IMessage onMessage(ExecPacket message, MessageContext ctx) {
      if (message.remoteExec != null && message.runner != null) {
        message.runner.accept(ctx.getServerHandler().player);
      }
      return null;
    }

  }

}
