package crazypants.enderio.zoo.entity;

import java.util.List;

import crazypants.enderio.zoo.vec.Point3i;
import crazypants.enderio.zoo.vec.VecUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class SpawnUtil {

  public static boolean findClearGround(World world, Point3i startingLocation, Point3i clearLocation) {
    return findClearGround(world, startingLocation, clearLocation, 2, 10, false);
  }
  
  public static boolean findClearGround(World world, Point3i startingLocation, Point3i clearLocation, int horizRange, int vertRange,
      boolean checkForLivingEntities) {
    //first find some air in the y
    boolean foundTargetSpace = false;
    for (int xOff = -horizRange; xOff <= horizRange && !foundTargetSpace; xOff++) {
      clearLocation.x = startingLocation.x + xOff;
      for (int zOff = -horizRange; zOff <= horizRange && !foundTargetSpace; zOff++) {
        clearLocation.z = startingLocation.z + zOff;
        foundTargetSpace = SpawnUtil.seachYForClearGround(clearLocation, world, vertRange, checkForLivingEntities);
        if(!foundTargetSpace) {
          clearLocation.y = startingLocation.y;
        }
      }
    }
    return foundTargetSpace;
  }
  
  public static boolean seachYForClearGround(Point3i target, World world) {
    return seachYForClearGround(target, world, 10, false);
  }
  
  public static boolean seachYForClearGround(Point3i target, World world, int searchRange, boolean checkForLivingEntities) {
    boolean foundY = false;
    for (int i = 0; i < searchRange && !foundY; i++) {
      if(world.isAirBlock(VecUtil.bpos(target.x, target.y, target.z))) {
        foundY = true;
      } else {
        target.y++;
      }
    }
    boolean onGround = false;
    if(foundY) {
      for (int i = 0; i < searchRange && !onGround; i++) {
        onGround = !world.isAirBlock(VecUtil.bpos(target.x, target.y - 1, target.z)) && !isLiquid(world, target.x, target.y - 1, target.z);
        if(!onGround) {
          target.y--;
        } else if(checkForLivingEntities && containsLiving(world, target)) {
          return false;
        }
      }
    }
    return foundY && onGround;
  }

  public static boolean containsLiving(World world, Point3i blockCoord) {
    AxisAlignedBB bb = new AxisAlignedBB(blockCoord.x, blockCoord.y, blockCoord.z, blockCoord.x + 1, blockCoord.y + 1, blockCoord.z + 1);
    List<?> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
    return ents != null && !ents.isEmpty();
  }

  public static boolean isLiquid(World world, int x, int y, int z) {
	IBlockState bs = world.getBlockState(VecUtil.bpos(x, y, z));
	if(bs == null || bs.getBlock() == null) {
		return false;
	}
    if(bs.getMaterial().isLiquid()) {
      return true;
    }
    return false;
  }

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityLiving entity, EntityCreature asCreature, boolean checkEntityCollisions, boolean canSpawnInLiquid) {
    if(asCreature != null && asCreature.getBlockPathWeight(entity.getPosition()) < 0) {
      return false;
    }
    if(checkEntityCollisions && !worldObj.checkNoEntityCollision(entity.getEntityBoundingBox())) {
      return false;
    }
    if(!worldObj.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
      return false;
    }    
    if(!canSpawnInLiquid && worldObj.containsAnyLiquid(entity.getEntityBoundingBox())) {
      return false;
    }    
    return true;
  }

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityCreature entityCreature, boolean checkEntityCollisions, boolean canSpawnInLiquid) {
    return isSpaceAvailableForSpawn(worldObj, entityCreature, entityCreature, checkEntityCollisions, false);
  }
  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityLiving entity, boolean checkEntityCollisions, boolean canSpawnInLiquid) {
    return isSpaceAvailableForSpawn(worldObj, entity, entity instanceof EntityCreature ? ((EntityCreature)entity) : null, checkEntityCollisions, canSpawnInLiquid);
  }
  
  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityLiving spawn, boolean checkEntityCollisions) {
    return isSpaceAvailableForSpawn(worldObj, spawn, checkEntityCollisions, false);
  }

  
}
