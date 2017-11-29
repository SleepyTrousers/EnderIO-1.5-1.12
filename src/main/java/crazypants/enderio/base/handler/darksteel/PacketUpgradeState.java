package crazypants.enderio.base.handler.darksteel;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpgradeState implements IMessage, IMessageHandler<PacketUpgradeState, IMessage> {

  public enum Type {
    GLIDE,
    SPEED,
    STEP_ASSIST, 
    JUMP,
    ELYTRA
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

  @Override
  public IMessage onMessage(PacketUpgradeState message, MessageContext ctx) {
    EntityPlayer player = (EntityPlayer) (ctx.side.isClient() ? EnderIO.proxy.getClientWorld().getEntityByID(message.entityID) : ctx.getServerHandler().player);
    if (player != null) {
      DarkSteelController.instance.setActive(player, message.type, message.isActive);
      if (ctx.side.isServer()) {
        message.entityID = player.getEntityId();
        PacketHandler.INSTANCE.sendToDimension(message, player.world.provider.getDimension());
      }
    }
    return null;
  }
}
