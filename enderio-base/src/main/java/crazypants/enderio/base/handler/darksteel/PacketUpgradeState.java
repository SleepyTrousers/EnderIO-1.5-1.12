package crazypants.enderio.base.handler.darksteel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpgradeState implements IMessage {

  public PacketUpgradeState() {
  }

  private int entityID;
  private final @Nonnull Map<String, Boolean> payload = new HashMap<>();

  public PacketUpgradeState(@Nonnull String type, boolean isActive) {
    this(type, isActive, 0);
  }

  public PacketUpgradeState(@Nonnull String type, boolean isActive, int entityID) {
    this.entityID = entityID;
    this.add(type, isActive);
  }

  public PacketUpgradeState(int entityID) {
    this.entityID = entityID;
  }

  public void add(@Nonnull String type, boolean isActive) {
    this.payload.put(type, isActive);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    if (buf != null) {
      buf.writeInt(entityID);
      buf.writeInt(payload.size());
      for (Entry<String, Boolean> pair : payload.entrySet()) {
        ByteBufUtils.writeUTF8String(buf, pair.getKey());
        buf.writeBoolean(pair.getValue());
      }
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    if (buf != null) {
      entityID = buf.readInt();
      int count = buf.readInt();
      for (int i = 0; i < count; i++) {
        payload.put(ByteBufUtils.readUTF8String(buf), buf.readBoolean());
      }
    }
  }

  public static class ClientHandler implements IMessageHandler<PacketUpgradeState, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
      final Entity player = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
      if (player instanceof EntityPlayer) {
        for (Entry<String, Boolean> pair : message.payload.entrySet()) {
          StateController.syncActive((EntityPlayer) player, NullHelper.first(pair.getKey(), ""), pair.getValue());
        }
      }
      return null;
    }
  }

  public static class ServerHandler implements IMessageHandler<PacketUpgradeState, IMessage> {

    @Override
    public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      for (Entry<String, Boolean> pair : message.payload.entrySet()) {
        StateController.setActive(player, NullHelper.first(pair.getKey(), ""), pair.getValue());
      }
      return null;
    }
  }

}
