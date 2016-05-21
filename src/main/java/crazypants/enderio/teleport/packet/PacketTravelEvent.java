package crazypants.enderio.teleport.packet;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.sound.SoundHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTravelEvent implements IMessage, IMessageHandler<PacketTravelEvent, IMessage> {

  int x;
  int y;
  int z;
  int powerUse;
  boolean conserveMotion;
  int entityId;
  int source;

  public PacketTravelEvent() {
  }

  public PacketTravelEvent(Entity entity, int x, int y, int z, int powerUse, boolean conserveMotion, TravelSource source) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.powerUse = powerUse;
    this.conserveMotion = conserveMotion;
    this.entityId = entity instanceof EntityPlayer ? -1 : entity.getEntityId();
    this.source = source.ordinal();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeInt(powerUse);
    buf.writeBoolean(conserveMotion);
    buf.writeInt(entityId);
    buf.writeInt(source);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    powerUse = buf.readInt();
    conserveMotion = buf.readBoolean();
    entityId = buf.readInt();
    source = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketTravelEvent message, MessageContext ctx) {
    Entity toTp = message.entityId == -1 ? ctx.getServerHandler().playerEntity : ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityId);

    int x = message.x, y = message.y, z = message.z;

    TravelSource source = TravelSource.values()[message.source];

    doServerTeleport(toTp, x, y, z, message.powerUse, message.conserveMotion, source);

    return null;
  }

  public static boolean doServerTeleport(Entity toTp, int x, int y, int z, int powerUse, boolean conserveMotion, TravelSource source) {
    EntityPlayer player = toTp instanceof EntityPlayer ? (EntityPlayer) toTp : null;
    
    TeleportEntityEvent evt = new TeleportEntityEvent(toTp, source, x, y, z);
    if(MinecraftForge.EVENT_BUS.post(evt)) {
      return false;
    }
    x = evt.targetX;
    y = evt.targetY;
    z = evt.targetZ;

    SoundHelper.playSound(toTp.worldObj, toTp, source.sound, 1.0F, 1.0F);

    if(player != null) {
      player.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);
    } else {
      toTp.setPosition(x, y, z);
    }

    SoundHelper.playSound(toTp.worldObj, toTp, source.sound, 1.0F, 1.0F);

    toTp.fallDistance = 0;

    if(player != null) {
      if(conserveMotion) {
        Vector3d velocityVex = Util.getLookVecEio(player);
        SPacketEntityVelocity p = new SPacketEntityVelocity(toTp.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z);
        ((EntityPlayerMP) player).connection.sendPacket(p);
      }

      if(powerUse > 0 && player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IItemOfTravel) {
        ItemStack item = player.getHeldItemMainhand().copy();
        ((IItemOfTravel) item.getItem()).extractInternal(item, powerUse);
        toTp.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, item);
      }
    }
    
    return true;
  }
}
