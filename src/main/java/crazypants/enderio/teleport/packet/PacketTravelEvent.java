package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class PacketTravelEvent implements IMessage, IMessageHandler<PacketTravelEvent, IMessage> {

  int x;
  int y;
  int z;
  int powerUse;
  boolean conserveMotion;

  public PacketTravelEvent() {
  }

  public PacketTravelEvent(int x, int y, int z, int powerUse, boolean conserveMotion) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.powerUse = powerUse;
    this.conserveMotion = conserveMotion;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeInt(powerUse);
    buf.writeBoolean(conserveMotion);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    powerUse = buf.readInt();
    conserveMotion = buf.readBoolean();
  }
  
  @Override
  public IMessage onMessage(PacketTravelEvent message, MessageContext ctx) {
      
    EntityPlayer ep = ctx.getServerHandler().playerEntity;

    ep.worldObj.playSoundEffect(ep.posX, ep.posY, ep.posZ, "mob.endermen.portal", 1.0F, 1.0F);

    ep.playSound("mob.endermen.portal", 1.0F, 1.0F);

    ep.setPositionAndUpdate(message.x + 0.5, message.y + 1.1, message.z + 0.5);

    ep.worldObj.playSoundEffect(message.x, message.y, message.z, "mob.endermen.portal", 1.0F, 1.0F);
    ep.fallDistance = 0;

    if(message.conserveMotion) {
      Vector3d velocityVex = Util.getLookVecEio(ep);
      S12PacketEntityVelocity p = new S12PacketEntityVelocity(ep.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z);

      ctx.getServerHandler().sendPacket(p);
    }

    if(message.powerUse > 0 && ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().getItem() instanceof IItemOfTravel) {
      ItemStack item = ep.getCurrentEquippedItem().copy();
      ((IItemOfTravel)item.getItem()).extractInternal(item, message.powerUse);
      ep.setCurrentItemOrArmor(0, item);
    }

    return null;
  }

}
