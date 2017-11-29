package crazypants.enderio.base.teleport;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class RandomTeleportUtil {

  private static final Random rand = new Random();
  private static final ResourceLocation SOUND = new ResourceLocation("entity.endermen.teleport");

  private RandomTeleportUtil() {
  }

  public static void teleportEntity(@Nonnull World world, @Nonnull Entity entity) {
    double origX = entity.posX, origY = entity.posY, origZ = entity.posZ;
    for (int i = 0; i < 5; i++) {
      double targetX = origX + rand.nextGaussian() * 16f;
      double targetY = -1;
      while (targetY < 1.1) {
        targetY = origY + rand.nextGaussian() * 8f;
      }
      double targetZ = origZ + rand.nextGaussian() * 16f;
      if (isClear(world, entity, targetX, targetY, targetZ) && doTeleport(world, entity, targetX, targetY, targetZ)) {
        final SoundEvent sound = SoundEvent.REGISTRY.getObject(SOUND);
        if (sound != null) {
          world.playSound(null, origX, origY, origZ, sound, SoundCategory.BLOCKS, 1, 1);
          world.playSound(null, targetX, targetY, targetZ, sound, SoundCategory.BLOCKS, 1, 1);
        }
        entity.timeUntilPortal = 5;
        return;
      }
    }
  }

  private static boolean isClear(@Nonnull World world, @Nonnull Entity entity, double targetX, double targetY, double targetZ) {
    double origX = entity.posX, origY = entity.posY, origZ = entity.posZ;
    try {
      entity.setPosition(targetX, targetY, targetZ);
      boolean result = world.checkNoEntityCollision(entity.getEntityBoundingBox(), entity)
          && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty();
      return result;
    } finally {
      entity.setPosition(origX, origY, origZ);
    }
  }

  private static boolean doTeleport(@Nonnull World world, @Nonnull Entity entity, double targetX, double targetY, double targetZ) {
    if (entity instanceof EntityLivingBase) {
      return doTeleport(world, (EntityLivingBase) entity, targetX, targetY, targetZ);
    }

    if (entity.isRiding()) {
      entity.dismountRidingEntity();
    }
    if (entity.isBeingRidden()) {
      for (Entity passenger : entity.getPassengers()) {
        passenger.dismountRidingEntity();
      }
    }

    entity.setPositionAndRotation(targetX, targetY, targetZ, entity.rotationYaw, entity.rotationPitch);
    return true;
  }

  private static boolean doTeleport(@Nonnull World world, @Nonnull EntityLivingBase entity, double targetX, double targetY, double targetZ) {
    float damage = 5f;
    if (entity.getMaxHealth() < 10f) {
      damage = 1f;
    }
    EnderTeleportEvent event = new EnderTeleportEvent(entity, targetX, targetY, targetZ, damage);
    if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
      if (rand.nextFloat() < 0.15F && world.getGameRules().getBoolean("doMobSpawning")) {
        EntityEndermite entityendermite = new EntityEndermite(world);
        entityendermite.setSpawnedByPlayer(true);
        entityendermite.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
        world.spawnEntity(entityendermite);
      }

      if (entity.isRiding()) {
        entity.dismountRidingEntity();
      }
      if (entity.isBeingRidden()) {
        for (Entity passenger : entity.getPassengers()) {
          passenger.dismountRidingEntity();
        }
      }

      if (entity instanceof EntityPlayerMP) {
        ((EntityPlayerMP) entity).connection.setPlayerLocation(event.getTargetX(), event.getTargetY(), event.getTargetZ(), entity.rotationYaw,
            entity.rotationPitch);
      } else {
        entity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
      }
      entity.fallDistance = 0.0F;
      entity.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
      return true;
    }
    return false;
  }

}
