package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.IPacketEio;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class PacketTravelEvent implements IPacketEio {

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
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(x);
    buffer.writeInt(y);
    buffer.writeInt(z);
    buffer.writeInt(powerUse);
    buffer.writeBoolean(conserveMotion);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    x = buffer.readInt();
    y = buffer.readInt();
    z = buffer.readInt();
    powerUse = buffer.readInt();
    conserveMotion = buffer.readBoolean();
  }

  @Override
  public void handleClientSide(EntityPlayer player) {

  }

  @Override
  public void handleServerSide(EntityPlayer ep) {

    ep.worldObj.playSoundEffect(ep.posX, ep.posY, ep.posZ, "mob.endermen.portal", 1.0F, 1.0F);

    ep.playSound("mob.endermen.portal", 1.0F, 1.0F);

    ep.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);

    ep.worldObj.playSoundEffect(x, y, z, "mob.endermen.portal", 1.0F, 1.0F);
    ep.fallDistance = 0;

    if(conserveMotion) {
      Vector3d velocityVex = Util.getLookVecEio(ep);
      S12PacketEntityVelocity p = new S12PacketEntityVelocity(ep.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z);

      EnderIO.packetPipeline.sendTo(p, (EntityPlayerMP) ep);

    }

    if(powerUse > 0 && ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().getItem() == EnderIO.itemTravelStaff) {
      ItemStack item = ep.getCurrentEquippedItem().copy();
      EnderIO.itemTravelStaff.extractInternal(item, powerUse);
      ep.setCurrentItemOrArmor(0, item);
    }

  }

}
