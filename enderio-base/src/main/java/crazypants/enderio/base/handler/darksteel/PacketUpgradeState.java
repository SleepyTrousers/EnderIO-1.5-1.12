package crazypants.enderio.base.handler.darksteel;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpgradeState implements IMessage {

  public enum Type {
    GLIDE,
    SPEED,
    STEP_ASSIST,
    JUMP,
    ELYTRA,
    GOGGLES,
    NIGHTVISION
  }

  public PacketUpgradeState() {
  }

  private boolean isActive;
  private Type type;
  private int entityID;

  public PacketUpgradeState(Type type, boolean isActive) {
    this(type, isActive, 0);
  }

  public PacketUpgradeState(Type type, boolean isActive, int entityID) {
    this.type = type;
    this.isActive = isActive;
    this.entityID = entityID;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(type.ordinal());
    buf.writeBoolean(isActive);
    buf.writeInt(entityID);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    type = Type.values()[buf.readShort()];
    isActive = buf.readBoolean();
    entityID = buf.readInt();
  }

  public static class ClientHandler implements IMessageHandler<PacketUpgradeState, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
      Entity player = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
      if (player instanceof EntityPlayer) {
        DarkSteelController.syncActive((EntityPlayer) player, message.type, message.isActive);
      }
      return null;
    }
  }

  public static class ServerHandler implements IMessageHandler<PacketUpgradeState, IMessage> {

    @Override
    public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      DarkSteelController.setActive(player, message.type, message.isActive);
      return null;
    }
  }

}
