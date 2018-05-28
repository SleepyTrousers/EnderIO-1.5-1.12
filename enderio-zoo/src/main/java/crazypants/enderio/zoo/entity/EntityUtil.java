package crazypants.enderio.zoo.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Point3i;

import crazypants.enderio.base.EnderIO;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityUtil {

  public static boolean isHardDifficulty(@Nonnull World worldObj) {
    return worldObj.getDifficulty() == EnumDifficulty.HARD;
  }

  public static float getDifficultyMultiplierForLocation(@Nonnull World world, double x, double y, double z) {
    // Value between 0 and 1 (normal) - 1.5 based on how long a chunk has been
    // occupied
    float occupiedDiffcultyMultiplier = world.getDifficultyForLocation(new BlockPos(x, y, z)).getClampedAdditionalDifficulty();
    occupiedDiffcultyMultiplier /= 1.5f; // normalize
    return occupiedDiffcultyMultiplier;
  }

  public static @Nonnull String getDisplayNameForEntity(@Nonnull String mobName) {
    return EnderIO.lang.localizeExact("entity." + mobName + ".name");
  }

  public static @Nonnull Vec3d getEntityPosition(@Nonnull Entity entity) {
    return new Vec3d(entity.posX, entity.posY, entity.posZ);
  }

  public static @Nonnull AxisAlignedBB getBoundsAround(@Nonnull Entity entity, double range) {
    return getBoundsAround(entity.posX, entity.posY, entity.posZ, range);
  }

  public static @Nonnull AxisAlignedBB getBoundsAround(@Nonnull Vec3d pos, double range) {
    return getBoundsAround(pos.x, pos.y, pos.z, range);
  }

  public static @Nonnull AxisAlignedBB getBoundsAround(@Nonnull BlockPos pos, int range) {
    return getBoundsAround(pos.getX(), pos.getY(), pos.getZ(), range);
  }

  public static @Nonnull AxisAlignedBB getBoundsAround(double x, double y, double z, double range) {
    return new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
  }

  public static @Nonnull Point3i getEntityPositionI(@Nonnull Entity entity) {
    return new Point3i((int) entity.posX, (int) entity.posY, (int) entity.posZ);
  }

  public static void cancelCurrentTasks(@Nonnull EntityLiving ent) {
    Iterator<EntityAITaskEntry> iterator = ent.tasks.taskEntries.iterator();
    List<EntityAITasks.EntityAITaskEntry> currentTasks = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    while (iterator.hasNext()) {
      EntityAITaskEntry entityaitaskentry = iterator.next();
      if (entityaitaskentry != null) {
        currentTasks.add(entityaitaskentry);
      }
    }
    // Only available way to stop current execution is to remove all current
    // tasks, then re-add them
    for (EntityAITaskEntry task : currentTasks) {
      ent.tasks.removeTask(task.action);
      ent.tasks.addTask(task.priority, task.action);
    }
    ent.getNavigator().clearPath();
  }

  public static @Nullable IAttributeInstance removeModifier(@Nonnull EntityLivingBase ent, @Nonnull IAttribute p, @Nonnull UUID u) {
    IAttributeInstance att = ent.getEntityAttribute(p);
    AttributeModifier curmod = att.getModifier(u);
    if (curmod != null) {
      att.removeModifier(curmod);
    }
    return att;
  }

  public static double getDistanceSqToNearestPlayer(@Nonnull Entity entity, double maxRange) {
    AxisAlignedBB bounds = getBoundsAround(entity, maxRange);
    EntityPlayer nearest = (EntityPlayer) entity.getEntityWorld().findNearestEntityWithinAABB(EntityPlayer.class, bounds, entity);
    if (nearest == null) {
      return 1;
    }
    return nearest.getDistanceSq(entity);
  }

  public static boolean isPlayerWithinRange(@Nonnull Entity entity, double range) {
    List<EntityPlayer> res = entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, getBoundsAround(entity, range));
    return !res.isEmpty();
  }

  public static boolean isOnGround(@Nonnull EntityCreature entity) {
    List<AxisAlignedBB> collides = entity.getEntityWorld().getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(0, -0.1, 0));
    if (collides.isEmpty()) {
      return false;
    }
    BlockPos groundPos = entity.getPosition().down();
    IBlockState bs = entity.getEntityWorld().getBlockState(groundPos);
    if (bs.getMaterial().isLiquid()) {
      return false;
    }
    return true;
  }

  public static @Nullable BlockPos findRandomLandingSurface(@Nonnull EntityCreature entity, int searchRange, int minY, int maxY, int searchAttempts) {
    for (int i = 0; i < searchAttempts; i++) {
      BlockPos res = findRandomLandingSurface(entity, searchRange, minY, maxY);
      if (res != null) {
        return res;
      }
    }
    return null;
  }

  public static @Nullable BlockPos findRandomClearArea(@Nonnull EntityCreature entity, int searchRange, int minY, int maxY, int searchAttempts) {
    BlockPos ep = entity.getPosition();
    Vec3d pos = entity.getPositionVector();
    World worldObj = entity.getEntityWorld();
    for (int i = 0; i < searchAttempts; i++) {
      int x = ep.getX() + -searchRange + (worldObj.rand.nextInt(searchRange + 1) * 2);
      int y = minY + worldObj.rand.nextInt(maxY - minY + 1);
      int z = ep.getZ() + -searchRange + (worldObj.rand.nextInt(searchRange + 1) * 2);
      entity.setPosition(x + 0.5, y, z + 0.5);
      boolean isSpace = SpawnUtil.isSpaceAvailableForSpawn(worldObj, entity, false);
      entity.setPosition(pos.x, pos.y, pos.z);
      if (isSpace) {
        return new BlockPos(x, y, z);
      }
    }
    return null;
  }

  public static @Nullable BlockPos findRandomLandingSurface(@Nonnull EntityLiving entity, int searchRange, int minY, int maxY) {
    BlockPos ep = entity.getPosition();
    World worldObj = entity.getEntityWorld();
    int x = ep.getX() + -searchRange + (worldObj.rand.nextInt(searchRange + 1) * 2);
    int z = ep.getZ() + -searchRange + (worldObj.rand.nextInt(searchRange + 1) * 2);
    return findClearLandingSurface(entity, x, z, minY, maxY);
  }

  public static @Nullable BlockPos findClearLandingSurface(@Nonnull EntityLiving ent, int x, int z, int minY, int maxY) {

    double origX = ent.posX;
    double origY = ent.posY;
    double origZ = ent.posZ;

    int y = maxY;

    boolean canLand = canLandAtLocation(ent, x, y, z);
    while (!canLand) {
      --y;
      if (y < minY) {
        break;
      }
      canLand = canLandAtLocation(ent, x, y, z);
    }
    ent.setPosition(origX, origY, origZ);

    if (canLand) {
      return new BlockPos(x, y, z);
    }
    return null;
  }

  private static boolean canLandAtLocation(@Nonnull EntityLiving ent, int x, int y, int z) {

    World world = ent.getEntityWorld();
    ent.setPosition(x + 0.5, y, z + 0.5);
    if (!SpawnUtil.isSpaceAvailableForSpawn(world, ent, false, false)) {
      return false;
    }

    BlockPos below = new BlockPos(x, y, z).down();
    IBlockState bs = world.getBlockState(below);
    if (!bs.getMaterial().isSolid()) {
      return false;
    }
    AxisAlignedBB collides = bs.getCollisionBoundingBox(world, below);
    return collides != null;
  }

}
