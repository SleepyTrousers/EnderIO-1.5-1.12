package crazypants.enderio.base.handler.darksteel;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
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

  private boolean isActive;
  private IDarkSteelUpgrade type;
  private int entityID;

  public PacketUpgradeState(@Nonnull IDarkSteelUpgrade type, boolean isActive) {
    this(type, isActive, 0);
  }

  public PacketUpgradeState(@Nonnull IDarkSteelUpgrade type, boolean isActive, int entityID) {
    this.type = type;
    this.isActive = isActive;
    this.entityID = entityID;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    if (buf != null) {
      ByteBufUtils.writeRegistryEntry(buf, NullHelper.notnullF(type, "packet uninitialized"));
      buf.writeBoolean(isActive);
      buf.writeInt(entityID);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    if (buf != null) {
      type = UpgradeRegistry.read(buf);
      isActive = buf.readBoolean();
      entityID = buf.readInt();
    }
  }

  public static class ClientHandler implements IMessageHandler<PacketUpgradeState, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
      final Entity player = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
      final IDarkSteelUpgrade type = message.type;
      if (player instanceof EntityPlayer && type != null) {
        DarkSteelController.syncActive((EntityPlayer) player, type, message.isActive);
      }
      return null;
    }
  }

  public static class ServerHandler implements IMessageHandler<PacketUpgradeState, IMessage> {

    @Override
    public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      final IDarkSteelUpgrade type = message.type;
      if (type != null) {
        DarkSteelController.setActive(player, type, message.isActive);
      }
      return null;
    }
  }

}
