package crazypants.enderio.zoo.entity;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class TeleportHelper {

  private static final int DEFAULT_RND_TP_DISTANCE = 16;
  private static Random rand = new Random();

  public static boolean teleportRandomly(EntityLivingBase entity, int distance) {
    double d0 = entity.posX + (rand.nextDouble() - 0.5D) * distance;
    double d1 = entity.posY + rand.nextInt(distance + 1) - distance / 2;
    double d2 = entity.posZ + (rand.nextDouble() - 0.5D) * distance;
    return teleportTo(entity, d0, d1, d2, false);
  }

  public static boolean teleportRandomly(EntityLivingBase entity) {
    return teleportRandomly(entity, DEFAULT_RND_TP_DISTANCE);
  }

  public static boolean teleportToEntity(EntityLivingBase entity, Entity toEntity) {
    Vec3d vec3 = new Vec3d(entity.posX - toEntity.posX,
        entity.getEntityBoundingBox().minY + entity.height / 2.0F - toEntity.posY + toEntity.getEyeHeight(), entity.posZ - toEntity.posZ);
    vec3 = vec3.normalize();
    double d0 = 16.0D;
    double d1 = entity.posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3.x * d0;
    double d2 = entity.posY + (rand.nextInt(16) - 8) - vec3.y * d0;
    double d3 = entity.posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3.z * d0;
    return teleportTo(entity, d1, d2, d3, false);
  }

  public static boolean teleportTo(EntityLivingBase entity, double x, double y, double z, boolean fireEndermanEvent) {

    EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
    if (fireEndermanEvent) {
      if (MinecraftForge.EVENT_BUS.post(event)) {
        return false;
      }
    }

    double origX = entity.posX;
    double origY = entity.posY;
    double origZ = entity.posZ;
    entity.posX = event.getTargetX();
    entity.posY = event.getTargetY();
    entity.posZ = event.getTargetZ();

    int xInt = MathHelper.floor(entity.posX);
    int yInt = Math.max(2, MathHelper.floor(entity.posY));
    int zInt = MathHelper.floor(entity.posZ);

    boolean doTeleport = false;
    World worldObj = entity.getEntityWorld();
    if (worldObj.isBlockLoaded(new BlockPos(xInt, yInt, zInt), true)) {
      boolean foundGround = false;
      while (!foundGround && yInt > 2) {
        IBlockState bs = worldObj.getBlockState(new BlockPos(xInt, yInt - 1, zInt));
        if (bs != null && bs.getBlock() != null && bs.getMaterial().blocksMovement()) {
          foundGround = true;
        } else {
          --entity.posY;
          --yInt;
        }
      }

      if (foundGround) {
        entity.setPosition(entity.posX, entity.posY, entity.posZ);
        if (worldObj.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()
            && !worldObj.containsAnyLiquid(entity.getEntityBoundingBox())) {
          doTeleport = true;
        } else if (yInt <= 0) {
          doTeleport = false;
        }
      }
    }

    if (!doTeleport) {
      entity.setPosition(origX, origY, origZ);
      return false;
    }

    entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);

    short short1 = 128;
    for (int l = 0; l < short1; ++l) {
      double d6 = l / (short1 - 1.0D);
      float f = (rand.nextFloat() - 0.5F) * 0.2F;
      float f1 = (rand.nextFloat() - 0.5F) * 0.2F;
      float f2 = (rand.nextFloat() - 0.5F) * 0.2F;
      double d7 = origX + (entity.posX - origX) * d6 + (rand.nextDouble() - 0.5D) * entity.width * 2.0D;
      double d8 = origY + (entity.posY - origY) * d6 + rand.nextDouble() * entity.height;
      double d9 = origZ + (entity.posZ - origZ) * d6 + (rand.nextDouble() - 0.5D) * entity.width * 2.0D;
      worldObj.spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
    }

    worldObj.playSound(origX, origY, origZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
    entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
    return true;

  }

}
