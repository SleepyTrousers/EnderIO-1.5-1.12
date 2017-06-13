package crazypants.enderio.teleport.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
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
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTravelEvent implements IMessage {

  long pos;
  int powerUse;
  boolean conserveMotion;
  int source;
  int hand;

  public PacketTravelEvent() {
  }

  public PacketTravelEvent(BlockPos pos, int powerUse, boolean conserveMotion, TravelSource source, EnumHand hand) {
    this.pos = pos.toLong();
    this.powerUse = powerUse;
    this.conserveMotion = conserveMotion;
    this.source = source.ordinal();
    this.hand = (hand == null ? EnumHand.MAIN_HAND : hand).ordinal();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    buf.writeInt(powerUse);
    buf.writeBoolean(conserveMotion);
    buf.writeInt(source);
    buf.writeInt(hand);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = buf.readLong();
    powerUse = buf.readInt();
    conserveMotion = buf.readBoolean();
    source = buf.readInt();
    hand = buf.readInt();
  }

  public static class Handler implements IMessageHandler<PacketTravelEvent, IMessage> {

    @Override
    public IMessage onMessage(PacketTravelEvent message, MessageContext ctx) {
      Entity toTp = ctx.getServerHandler().player;

      TravelSource source = NullHelper.notnullJ(TravelSource.values()[message.source], "Enum.values()");
      EnumHand hand = NullHelper.notnullJ(EnumHand.values()[message.hand], "Enum.values()");

      doServerTeleport(toTp, BlockPos.fromLong(message.pos), message.powerUse, message.conserveMotion, source, hand);

      return null;
    }

    private boolean doServerTeleport(@Nonnull Entity toTp, @Nonnull BlockPos pos, int powerUse, boolean conserveMotion, @Nonnull TravelSource source,
        @Nonnull EnumHand hand) {
      EntityPlayer player = toTp instanceof EntityPlayer ? (EntityPlayer) toTp : null;

      TeleportEntityEvent evt = new TeleportEntityEvent(toTp, source, pos, toTp.dimension);
      if (MinecraftForge.EVENT_BUS.post(evt)) {
        return false;
      }
      pos = evt.getTarget();

      SoundHelper.playSound(toTp.world, toTp, source.sound, 1.0F, 1.0F);

      if (player != null) {
        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5);
      } else {
        toTp.setPosition(pos.getX(), pos.getY(), pos.getZ());
      }

      SoundHelper.playSound(toTp.world, toTp, source.sound, 1.0F, 1.0F);

      toTp.fallDistance = 0;

      if (player != null) {
        if (conserveMotion) {
          Vector3d velocityVex = Util.getLookVecEio(player);
          SPacketEntityVelocity p = new SPacketEntityVelocity(toTp.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z);
          ((EntityPlayerMP) player).connection.sendPacket(p);
        }

        if (powerUse > 0) {
          ItemStack heldItem = player.getHeldItem(hand);
          if (heldItem.getItem() instanceof IItemOfTravel) {
            ItemStack item = heldItem.copy();
            ((IItemOfTravel) item.getItem()).extractInternal(item, powerUse);
            player.setHeldItem(hand, item);
          }
        }
      }

      return true;
    }

  }

}
