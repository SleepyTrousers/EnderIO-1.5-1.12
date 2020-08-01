package crazypants.enderio.base.teleport;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.sound.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ITeleporter;

public class TeleportUtil {

  public static boolean doTeleport(@Nonnull Entity entityLiving, @Nonnull BlockPos pos, int targetDim, boolean conserveMotion, @Nonnull TravelSource source) {
    if (entityLiving instanceof FakePlayer) {
      // don't even bother...
      return false;
    }
    if (entityLiving.world.isRemote) {
      return checkClientTeleport(entityLiving, pos, targetDim, source);
    }
    return serverTeleport(entityLiving, pos, targetDim, conserveMotion, source);
  }

  public static boolean checkClientTeleport(@Nonnull Entity entityLiving, @Nonnull BlockPos pos, int targetDim, @Nonnull TravelSource source) {
    if (entityLiving instanceof FakePlayer) {
      // don't even bother...
      return false;
    }
    return !MinecraftForge.EVENT_BUS.post(new TeleportEntityEvent(entityLiving, source, pos, targetDim));
  }

  public static boolean serverTeleport(@Nonnull Entity entity, @Nonnull BlockPos pos, int targetDim, boolean conserveMotion, @Nonnull TravelSource source) {
    if (entity instanceof FakePlayer || entity.isDead) {
      // don't even bother...
      return false;
    }

    TeleportEntityEvent evt = new TeleportEntityEvent(entity, source, pos, targetDim);
    if (MinecraftForge.EVENT_BUS.post(evt)) {
      return false;
    }

    if (entity instanceof EntityPlayerMP) {
      if (entity.dimension == targetDim) {
        serverPlayerLocalTeleport((EntityPlayerMP) entity, pos, conserveMotion, source);
      } else {
        serverPlayerDimensionTeleport((EntityPlayerMP) entity, pos, targetDim, conserveMotion, source);
      }
    } else {
      if (entity.dimension == targetDim) {
        serverEntityLocalTeleport(entity, pos, source);
      } else {
        serverEntityDimensionTeleport(entity, pos, targetDim, source);
      }
    }
    return true;
  }

  private static void serverEntityLocalTeleport(@Nonnull Entity entity, @Nonnull BlockPos pos, @Nonnull TravelSource source) {
    SoundHelper.playSound(entity.world, entity, source.sound, 1.0F, 1.0F);
    entity.world.getChunkFromBlockCoords(pos);
    entity.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5);
    entity.fallDistance = 0;
    SoundHelper.playSound(entity.world, entity, source.sound, 1.0F, 1.0F);
  }

  private static void serverPlayerLocalTeleport(@Nonnull EntityPlayerMP player, @Nonnull BlockPos pos, boolean conserveMotion, @Nonnull TravelSource source) {

    ChunkTicket.loadChunk(player, player.world, BlockCoord.get(player));
    ChunkTicket.loadChunk(player, player.world, pos);

    SoundHelper.playSound(player.world, player, source.sound, 1.0F, 1.0F);
    player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, player.rotationYaw, player.rotationPitch);
    player.fallDistance = 0;
    SoundHelper.playSound(player.world, player, source.sound, 1.0F, 1.0F);

    if (conserveMotion) {
      Vector3d velocityVex = Util.getLookVecEio(player);
      SPacketEntityVelocity p = new SPacketEntityVelocity(player.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z);
      player.connection.sendPacket(p);
    }
  }

  private static void serverEntityDimensionTeleport(@Nonnull Entity entity, @Nonnull BlockPos pos, int targetDim, @Nonnull TravelSource source) {

    SoundHelper.playSound(entity.world, entity, source.sound, 1.0F, 1.0F);
    entity.changeDimension(targetDim, new ITeleporter() {
      @Override
      public void placeEntity(World world, Entity entity2, float yaw) {
        entity2.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, entity2.rotationYaw, entity2.rotationPitch);
        entity2.motionX = 0;
        entity2.motionY = 0;
        entity2.motionZ = 0;
        entity2.fallDistance = 0;
      }
    });
    SoundHelper.playSound(entity.world, entity, source.sound, 1.0F, 1.0F);
  }

  private static void serverPlayerDimensionTeleport(@Nonnull final EntityPlayerMP player, @Nonnull final BlockPos pos, final int targetDim,
      final boolean conserveMotion, @Nonnull final TravelSource source) {

    ChunkTicket.loadChunk(player, player.world, BlockCoord.get(player));
    SoundHelper.playSound(player.world, player, source.sound, 1.0F, 1.0F);

    player.mcServer.getPlayerList().transferPlayerToDimension(player, targetDim, new ITeleporter() {

      @Override
      public void placeEntity(World world, Entity entity, float yaw) {
        // like Forge's teleport command:
        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, entity.rotationYaw, entity.rotationPitch);
        // like vanilla's nether teleporter:
        ((EntityPlayerMP)entity).connection.setPlayerLocation(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, entity.rotationYaw, entity.rotationPitch);
        // Note: Each one of the above should be enough, but there have been issues with setting the player position after a dimension change, so we're doing
        // both to be on the safe side...
        entity.motionX = 0;
        entity.motionY = 0;
        entity.motionZ = 0;
        entity.fallDistance = 0;
      }
    });

    SoundHelper.playSound(player.world, player, source.sound, 1.0F, 1.0F);
    ChunkTicket.loadChunk(player, player.world, BlockCoord.get(player));

    if (conserveMotion) {
      Vector3d velocityVex = Util.getLookVecEio(player);
      player.connection.sendPacket(new SPacketEntityVelocity(player.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z));
    }

  }

}
