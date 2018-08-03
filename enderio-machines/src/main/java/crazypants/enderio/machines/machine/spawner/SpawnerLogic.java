package crazypants.enderio.machines.machine.spawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.machines.config.config.SpawnerConfig;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class SpawnerLogic {

  public static interface ISpawnerCallback {

    @Nonnull
    World getSpawnerWorld();

    @Nonnull
    BlockPos getSpawnerPos();

    int getRange();

    default @Nonnull BoundingBox getBounds() {
      return new BoundingBox(getSpawnerPos()).expand(getRange(), 1d, getRange());
    }

    void setNotification(@Nonnull SpawnerNotification note);

    void removeNotification(@Nonnull SpawnerNotification note);

    @Nullable
    CapturedMob getEntity();

    default void setHome(@Nonnull EntityCreature entity) {
    }

  }

  private final @Nonnull ISpawnerCallback spawner;

  public SpawnerLogic(@Nonnull ISpawnerCallback spawner) {
    this.spawner = spawner;
  }

  public boolean isAreaClear() {
    int spawnRange = spawner.getRange();
    return isAreaClear(spawnRange, 2, SpawnerConfig.poweredSpawnerMaxNearbyEntities.get());
  }

  public boolean isAreaClear(int spawnRangeXZ, int spawnRangeY, int amount) {
    if (SpawnerConfig.poweredSpawnerMaxNearbyEntities.get() > 0) {
      World world = spawner.getSpawnerWorld();
      BlockPos pos = spawner.getSpawnerPos();
      Entity entity = createEntity(world.getDifficultyForLocation(pos), true);
      if (!(entity instanceof EntityLiving)) {
        cleanupUnspawnedEntity(entity);
        spawner.setNotification(SpawnerNotification.BAD_SOUL);
        return false;
      }
      return isAreaClear(world, entity, spawnRangeXZ, spawnRangeY, amount);
    }
    return true;
  }

  private boolean isAreaClear(World world, Entity entity, int spawnRangeXZ, int spawnRangeY, int amount) {
    if (SpawnerConfig.poweredSpawnerMaxNearbyEntities.get() > 0) {
      int nearbyEntities = world
          .getEntitiesWithinAABB(entity.getClass(), spawner.getBounds().expand(spawnRangeXZ, spawnRangeY, spawnRangeXZ), EntitySelectors.IS_ALIVE).size();
      if (nearbyEntities >= amount) {
        cleanupUnspawnedEntity(entity);
        spawner.setNotification(SpawnerNotification.AREA_FULL);
        return false;
      }
      spawner.removeNotification(SpawnerNotification.AREA_FULL);
    }
    return true;
  }

  public boolean trySpawnEntity() {
    World world = spawner.getSpawnerWorld();
    BlockPos pos = spawner.getSpawnerPos();
    Entity entity = createEntity(world.getDifficultyForLocation(pos), true);
    if (!(entity instanceof EntityLiving)) {
      cleanupUnspawnedEntity(entity);
      spawner.setNotification(SpawnerNotification.BAD_SOUL);
      return false;
    }
    EntityLiving entityliving = (EntityLiving) entity;

    int spawnRange = spawner.getRange();

    if (!isAreaClear(world, entity, spawnRange, 2, SpawnerConfig.poweredSpawnerMaxNearbyEntities.get())) {
      return false;
    }

    for (int i = 0; i < SpawnerConfig.poweredSpawnerMaxSpawnTries.get(); i++) {
      double x = pos.getX() + .5 + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      double y = pos.getY() + world.rand.nextInt(3) - 1;
      double z = pos.getZ() + .5 + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;

      entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

      if (canSpawnEntity(entityliving)) {
        if (entityliving instanceof EntityCreature) {
          spawner.setHome(((EntityCreature) entityliving));
        }
        world.spawnEntity(entityliving);
        world.playEvent(2004, pos, 0);
        entityliving.spawnExplosionParticle();
        final Entity ridingEntity = entity.getRidingEntity();
        if (ridingEntity != null) {
          ridingEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0F);
        }
        for (Entity passenger : entity.getPassengers()) {
          passenger.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0F);
        }
        return true;
      }
    }

    cleanupUnspawnedEntity(entity);
    spawner.setNotification(SpawnerNotification.NO_LOCATION_FOUND);
    return false;
  }

  public boolean anyLocationInRange() {
    World world = spawner.getSpawnerWorld();
    BlockPos pos = spawner.getSpawnerPos();
    Entity entity = createEntity(world.getDifficultyForLocation(pos), true);
    if (!(entity instanceof EntityLiving)) {
      cleanupUnspawnedEntity(entity);
      spawner.setNotification(SpawnerNotification.BAD_SOUL);
      return false;
    }

    EntityLiving entityliving = (EntityLiving) entity;
    int spawnRange = spawner.getRange();

    int minxi = MathHelper.floor(pos.getX() + (0.0d - Math.nextAfter(1.0d, 0.0d)) * spawnRange);
    int maxxi = MathHelper.floor(pos.getX() + (Math.nextAfter(1.0d, 0.0d) - 0.0d) * spawnRange);

    int minyi = pos.getY() + 0 - 1;
    int maxyi = pos.getY() + 2 - 1;

    int minzi = MathHelper.floor(pos.getZ() + (0.0d - Math.nextAfter(1.0d, 0.0d)) * spawnRange);
    int maxzi = MathHelper.floor(pos.getZ() + (Math.nextAfter(1.0d, 0.0d) - 0.0d) * spawnRange);

    for (int x = minxi; x <= maxxi; x++) {
      for (int y = minyi; y <= maxyi; y++) {
        for (int z = minzi; z <= maxzi; z++) {
          entityliving.setLocationAndAngles(x + .5, y, z + .5, 0.0F, 0.0F);
          if (canSpawnEntity(entityliving)) {
            cleanupUnspawnedEntity(entity);
            spawner.removeNotification(SpawnerNotification.NO_LOCATION_AT_ALL);
            return true;
          }
        }
      }
    }

    cleanupUnspawnedEntity(entity);
    spawner.setNotification(SpawnerNotification.NO_LOCATION_AT_ALL);
    return false;
  }

  private void cleanupUnspawnedEntity(Entity entity) {
    if (entity != null) {
      final Entity ridingEntity = entity.getRidingEntity();
      if (ridingEntity != null) {
        ridingEntity.setDead();
      }
      for (Entity passenger : entity.getPassengers()) {
        passenger.setDead();
      }
    }
  }

  @Nullable
  Entity createEntity(DifficultyInstance difficulty, boolean forceAlive) {
    CapturedMob capturedMob = spawner.getEntity();
    if (capturedMob == null) {
      return null;
    }
    Entity ent = capturedMob.getEntity(spawner.getSpawnerWorld(), spawner.getSpawnerPos(), difficulty, false);
    if (ent == null) {
      // Entity must have been removed from this save or is otherwise missing, so revert to blank spawner
      capturedMob = null;
      return null;
    }
    if (forceAlive && SpawnerConfig.poweredSpawnerMaxPlayerDistance.get() <= 0 && SpawnerConfig.poweredSpawnerDespawnTimeSeconds.get() > 0
        && ent instanceof EntityLiving) {
      ent.getEntityData().setLong(BlockPoweredSpawner.KEY_SPAWNED_BY_POWERED_SPAWNER, spawner.getSpawnerWorld().getTotalWorldTime());
      ((EntityLiving) ent).enablePersistence();
    }
    return ent;
  }

  protected boolean canSpawnEntity(EntityLiving entityliving) {
    if (SpawnerConfig.poweredSpawnerUseVanillaSpawChecks.get()) {
      return ForgeEventFactory.canEntitySpawnSpawner(entityliving, entityliving.world, (float) entityliving.posX, (float) entityliving.posY,
          (float) entityliving.posZ);
    } else {
      return entityliving.isNotColliding() && ForgeEventFactory.canEntitySpawn(entityliving, entityliving.world, (float) entityliving.posX,
          (float) entityliving.posY, (float) entityliving.posZ, true) != Result.DENY;
    }
  }

}
