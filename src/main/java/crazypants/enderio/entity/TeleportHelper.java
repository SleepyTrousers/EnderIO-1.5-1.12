package crazypants.enderio.entity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class TeleportHelper {

  private static final int DEFAULT_RND_TP_DISTANCE = 16;
  private static Random rand = new Random();

  public static boolean teleportRandomly(EntityLivingBase entity, int distance) {
    double d0 = entity.posX + (rand.nextDouble() - 0.5D) * distance;
    double d1 = entity.posY + rand.nextInt(distance) - distance / 2;
    double d2 = entity.posZ + (rand.nextDouble() - 0.5D) * distance;
    return teleportTo(entity, d0, d1, d2, false);
  }

  public static boolean teleportRandomly(EntityLivingBase entity) {
    return teleportRandomly(entity, DEFAULT_RND_TP_DISTANCE);
  }

  public static boolean teleportToEntity(EntityLivingBase entity, Entity toEntity) {
    Vec3 vec3 = Vec3.createVectorHelper(entity.posX - toEntity.posX, entity.boundingBox.minY + (double) (entity.height / 2.0F) - toEntity.posY
        + (double) toEntity.getEyeHeight(), entity.posZ - toEntity.posZ);
    vec3 = vec3.normalize();
    double d0 = 16.0D;
    double d1 = entity.posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
    double d2 = entity.posY + (double) (rand.nextInt(16) - 8) - vec3.yCoord * d0;
    double d3 = entity.posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;
    return teleportTo(entity, d1, d2, d3, false);
  }

  public static boolean teleportTo(EntityLivingBase entity, double x, double y, double z, boolean fireEndermanEvent) {

    EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
    if(fireEndermanEvent) {
      if(MinecraftForge.EVENT_BUS.post(event)) {
        return false;
      }
    }

    double d3 = entity.posX;
    double d4 = entity.posY;
    double d5 = entity.posZ;
    entity.posX = event.targetX;
    entity.posY = event.targetY;
    entity.posZ = event.targetZ;

    int xInt = MathHelper.floor_double(entity.posX);
    int yInt = MathHelper.floor_double(entity.posY);
    int zInt = MathHelper.floor_double(entity.posZ);

    boolean flag = false;
    if(entity.worldObj.blockExists(xInt, yInt, zInt)) {

      boolean foundGround = false;
      while (!foundGround && yInt > 0) {
        Block block = entity.worldObj.getBlock(xInt, yInt - 1, zInt);
        if(block.getMaterial().blocksMovement()) {
          foundGround = true;
        } else {
          --entity.posY;
          --yInt;
        }
      }

      if(foundGround) {
        entity.setPosition(entity.posX, entity.posY, entity.posZ);
        if(entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty() && !entity.worldObj.isAnyLiquid(entity.boundingBox)) {
          flag = true;
        }
      }
    }

    if(!flag) {
      entity.setPosition(d3, d4, d5);
      return false;
    }
    
    entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);    

    short short1 = 128;
    for (int l = 0; l < short1; ++l) {
      double d6 = (double) l / ((double) short1 - 1.0D);
      float f = (rand.nextFloat() - 0.5F) * 0.2F;
      float f1 = (rand.nextFloat() - 0.5F) * 0.2F;
      float f2 = (rand.nextFloat() - 0.5F) * 0.2F;
      double d7 = d3 + (entity.posX - d3) * d6 + (rand.nextDouble() - 0.5D) * (double) entity.width * 2.0D;
      double d8 = d4 + (entity.posY - d4) * d6 + rand.nextDouble() * (double) entity.height;
      double d9 = d5 + (entity.posZ - d5) * d6 + (rand.nextDouble() - 0.5D) * (double) entity.width * 2.0D;
      entity.worldObj.spawnParticle("portal", d7, d8, d9, (double) f, (double) f1, (double) f2);
    }

    entity.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
    entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
    return true;

  }

}
