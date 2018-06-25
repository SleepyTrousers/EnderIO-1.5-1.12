package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.blockiterators.PlanarBlockIterator;
import com.enderio.core.common.util.blockiterators.PlanarBlockIterator.Orientation;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class SpawnUtil {

  public static BlockPos findClearGround(@Nonnull World world, @Nonnull BlockPos startingLocation) {
    return findClearGround(world, startingLocation, 2, 10, false);
  }

  public static BlockPos findClearGround(@Nonnull World world, @Nonnull BlockPos startingLocation, int horizRange, int vertRange,
      boolean checkForLivingEntities) {
    for (PlanarBlockIterator itr = new PlanarBlockIterator(startingLocation, Orientation.EAST_WEST, horizRange); itr.hasNext();) {
      BlockPos location = SpawnUtil.seachYForClearGround(itr.next(), world, vertRange, checkForLivingEntities);
      if (location != null) {
        return location;
      }
    }
    return null;
  }

  public static BlockPos seachYForClearGround(@Nonnull BlockPos target, @Nonnull World world) {
    return seachYForClearGround(target, world, 10, false);
  }

  public static BlockPos seachYForClearGround(@Nonnull BlockPos startingLocation, @Nonnull World world, int searchRange, boolean checkForLivingEntities) {
    MutableBlockPos pos = new MutableBlockPos(startingLocation);
    while (!world.isAirBlock(pos)) {
      pos.move(EnumFacing.UP);
      if (pos.getY() > 255 || pos.distanceSq(startingLocation) > searchRange * searchRange) {
        return null;
      }
    }
    while (world.isAirBlock(pos.down()) || isLiquid(world, pos.down())) {
      pos.move(EnumFacing.DOWN);
      if (pos.getY() < 0 || pos.distanceSq(startingLocation) > searchRange * searchRange) {
        return null;
      }
    }
    if (checkForLivingEntities && containsLiving(world, pos)) {
      return null;
    }
    return pos.toImmutable();

  }

  public static boolean containsLiving(@Nonnull World world, @Nonnull BlockPos pos) {
    return !world.checkNoEntityCollision(new AxisAlignedBB(pos));
  }

  public static boolean isLiquid(@Nonnull World world, @Nonnull BlockPos pos) {
    return world.getBlockState(pos).getMaterial().isLiquid();
  }

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityLiving entity, EntityCreature asCreature, boolean checkEntityCollisions,
      boolean canSpawnInLiquid) {
    if (asCreature != null && asCreature.getBlockPathWeight(entity.getPosition()) < 0) {
      return false;
    }
    if (checkEntityCollisions && !worldObj.checkNoEntityCollision(entity.getEntityBoundingBox())) {
      return false;
    }
    if (!worldObj.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
      return false;
    }
    if (!canSpawnInLiquid && worldObj.containsAnyLiquid(entity.getEntityBoundingBox())) {
      return false;
    }
    return true;
  }

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityCreature entityCreature, boolean checkEntityCollisions, boolean canSpawnInLiquid) {
    return isSpaceAvailableForSpawn(worldObj, entityCreature, entityCreature, checkEntityCollisions, false);
  }

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityLiving entity, boolean checkEntityCollisions, boolean canSpawnInLiquid) {
    return isSpaceAvailableForSpawn(worldObj, entity, entity instanceof EntityCreature ? ((EntityCreature) entity) : null, checkEntityCollisions,
        canSpawnInLiquid);
  }

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityLiving spawn, boolean checkEntityCollisions) {
    return isSpaceAvailableForSpawn(worldObj, spawn, checkEntityCollisions, false);
  }

}
