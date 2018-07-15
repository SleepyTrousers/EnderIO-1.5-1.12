package crazypants.enderio.base.teleport;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.sound.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class RandomTeleportUtil {

  private static final Random rand = new Random();

  private RandomTeleportUtil() {
  }

  public static void teleportSpawnItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
    EntityItem entity = new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack);
    entity.setDefaultPickupDelay();
    double origX = entity.posX, origY = MathHelper.clamp(entity.posY, 0, 255), origZ = entity.posZ;
    for (int i = 0; i < 5; i++) {
      double targetX = origX + rand.nextGaussian() * 16f;
      double targetY = -1;
      while (targetY < 1.1) {
        targetY = origY + rand.nextGaussian() * 8f;
      }
      double targetZ = origZ + rand.nextGaussian() * 16f;
      if (isClear(world, entity, targetX, targetY, targetZ) && doTeleport(world, entity, targetX, targetY, targetZ)) {
        world.spawnEntity(entity);
        entity.timeUntilPortal = 5;
        return;
      }
    }
    world.spawnEntity(entity);
  }

  public static void teleportEntity(@Nonnull World world, @Nonnull Entity entity, boolean isItem, boolean dropToGround, float range) {
    if (entity instanceof FakePlayer) {
      // don't even bother...
      return;
    }
    double origX = entity.posX, origY = MathHelper.clamp(entity.posY, 0, 255), origZ = entity.posZ;
    for (int i = 0; i < 15; i++) {
      double targetX = origX + rand.nextGaussian() * range;
      double targetY = -1;
      while (targetY < 1.1) {
        targetY = origY + rand.nextGaussian() * (range / 2);
      }
      double targetZ = origZ + rand.nextGaussian() * range;
      if (dropToGround) {
        targetY = MathHelper.floor(targetY) + .05;
        while (targetY >= 2f && !(hasGround(world, targetX, targetY, targetZ) && isClear(world, entity, targetX, targetY, targetZ))) {
          targetY -= 1f;
        }
      }
      if (targetY >= 2f && isClear(world, entity, targetX, targetY, targetZ) && doTeleport(world, entity, targetX, targetY, targetZ)) {
        final SoundRegistry sound = isItem ? SoundRegistry.TRAVEL_SOURCE_ITEM : SoundRegistry.TRAVEL_SOURCE_BLOCK;
        world.playSound(null, origX, origY, origZ, sound.getSoundEvent(), sound.getSoundCategory(), 1, 1);
        world.playSound(null, targetX, targetY, targetZ, sound.getSoundEvent(), sound.getSoundCategory(), 1, 1);
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

  private static boolean hasGround(@Nonnull World world, double targetX, double targetY, double targetZ) {
    int xInt = MathHelper.floor(targetX);
    int yInt = MathHelper.floor(targetY);
    int zInt = MathHelper.floor(targetZ);
    return yInt > 1 && world.getBlockState(new BlockPos(xInt, yInt - 1, zInt)).getMaterial().blocksMovement();
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
    if (!MinecraftForge.EVENT_BUS.post(event)) {
      if (rand.nextFloat() < 0.15F && world.getGameRules().getBoolean("doMobSpawning") && !(entity instanceof EntityEndermite)) {
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
