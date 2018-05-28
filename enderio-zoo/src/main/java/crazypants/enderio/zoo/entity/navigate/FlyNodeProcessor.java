package crazypants.enderio.zoo.entity.navigate;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FlyNodeProcessor extends WalkNodeProcessor {

  @Override
  public @Nonnull PathPoint getStart() {
    EntityLiving entityIn = entity;
    return openPoint(MathHelper.floor(entityIn.getEntityBoundingBox().minX), MathHelper.floor(entityIn.getEntityBoundingBox().minY + 0.5D),
        MathHelper.floor(entityIn.getEntityBoundingBox().minZ));
  }

  @Override
  public @Nonnull PathPoint getPathPointToCoords(double x, double y, double z) {
    EntityLiving entityIn = entity;
    return openPoint(MathHelper.floor(x - entityIn.width / 2.0F), MathHelper.floor(y), MathHelper.floor(z - entityIn.width / 2.0F));
  }

  @Override
  public int findPathOptions(@Nonnull PathPoint[] pathOptions, @Nonnull PathPoint currentPoint, @Nonnull PathPoint targetPoint, float maxDistance) {
    EntityLiving entityIn = entity;
    int i = 0;
    for (EnumFacing enumfacing : EnumFacing.values()) {
      PathPoint pathpoint = getSafePoint(entityIn, currentPoint.x + enumfacing.getFrontOffsetX(), currentPoint.y + enumfacing.getFrontOffsetY(),
          currentPoint.z + enumfacing.getFrontOffsetZ());
      if (pathpoint != null && !pathpoint.visited && (pathpoint.distanceTo(targetPoint) < maxDistance)) {
        pathOptions[i++] = pathpoint;
      }
    }
    return i;
  }

  private PathPoint getSafePoint(Entity entityIn, int x, int y, int z) {
    boolean i = entityFits(entityIn, x, y, z);
    return i ? openPoint(x, y, z) : null;
  }

  private boolean entityFits(Entity entityIn, int x, int y, int z) {

    BlockPos.MutableBlockPos mutableblockpos = new BlockPos.MutableBlockPos();
    for (int i = x; i < x + entitySizeX; ++i) {
      for (int j = y; j < y + entitySizeY; ++j) {
        for (int k = z; k < z + entitySizeZ; ++k) {
          IBlockState bs = blockaccess.getBlockState(mutableblockpos.setPos(i, j, k));
          if (bs.getMaterial() != Material.AIR) {
            AxisAlignedBB bb = bs.getCollisionBoundingBox(entityIn.world, mutableblockpos);
            if (bb != null) {
              return false;
            }
          }
        }
      }
    }

    return true;
  }

}
