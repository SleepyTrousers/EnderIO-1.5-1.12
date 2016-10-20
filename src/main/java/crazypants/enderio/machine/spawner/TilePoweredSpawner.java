package crazypants.enderio.machine.spawner;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.PoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeParticle;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.CapturedMob;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.itemSoulVessel;
import static crazypants.enderio.capacitor.CapacitorKey.SPAWNER_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.SPAWNER_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.SPAWNER_POWER_USE;
import static crazypants.enderio.capacitor.CapacitorKey.SPAWNER_SPEEDUP;

@Storable
public class TilePoweredSpawner extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity, IRanged {

  @Store({ StoreFor.CLIENT, StoreFor.SAVE })
  private CapturedMob capturedMob = null;
  @Store
  private boolean isSpawnMode = true;
  private int remainingSpawnTries;

  public TilePoweredSpawner() {
    super(new SlotDefinition(1, 1, 1), SPAWNER_POWER_INTAKE, SPAWNER_POWER_BUFFER, SPAWNER_POWER_USE);
  }

  public boolean isSpawnMode() {
    return isSpawnMode;
  }

  public void setSpawnMode(boolean isSpawnMode) {
    if (isSpawnMode != this.isSpawnMode) {
      currentTask = null;
    }
    this.isSpawnMode = isSpawnMode;
  }

  @Override
  protected void taskComplete() {
    super.taskComplete();
    if (hasEntity()) {
      if (isSpawnMode) {
        remainingSpawnTries = Config.poweredSpawnerSpawnCount + Config.poweredSpawnerMaxSpawnTries;
        for (int i = 0; i < Config.poweredSpawnerSpawnCount && remainingSpawnTries > 0; ++i) {
          if (!trySpawnEntity()) {
            break;
          }
        }
      } else {
        if (getStackInSlot(0) == null || getStackInSlot(1) != null || !hasEntity()) {
          return;
        }
        ItemStack res = capturedMob.toStack(itemSoulVessel.getItem(), 1, 1);
        decrStackSize(0, 1);
        setInventorySlotContents(1, res);
      }
    } else {
      this.worldObj.destroyBlock(getPos(), true);
    }
  }

  @Override
  public int getPowerUsePerTick() {
    return (int) (super.getPowerUsePerTick() * PoweredSpawnerConfig.getInstance().getCostMultiplierFor(getEntityName()));
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return (int) (super.getMaxEnergyRecieved(dir) * PoweredSpawnerConfig.getInstance().getCostMultiplierFor(getEntityName()));
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockPoweredSpawner.getUnlocalisedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (itemstack == null || isSpawnMode) {
      return false;
    }
    if (slotDefinition.isInputSlot(i)) {
      return itemstack.getItem() == itemSoulVessel.getItem() && !CapturedMob.containsSoul(itemstack);
    }
    return false;
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    if (!hasEntity()) {
      return null;
    }
    if (isSpawnMode) {
      if (Config.poweredSpawnerMaxPlayerDistance > 0) {
        BlockPos p = getPos();
        if (worldObj.getClosestPlayer(p.getX() + 0.5, p.getX() + 0.5, p.getX() + 0.5, Config.poweredSpawnerMaxPlayerDistance, false) == null) {
          return null;
        }
      }
    } else {
      if (getStackInSlot(0) == null || getStackInSlot(1) != null) {
        return null;
      }
    }
    return new DummyRecipe();
  }

  @Override
  protected boolean startNextTask(IMachineRecipe nextRecipe, float chance) {
    return super.startNextTask(nextRecipe, chance);
  }

  @Override
  protected boolean hasInputStacks() {
    return true;
  }

  @Override
  protected boolean canInsertResult(float chance, IMachineRecipe nextRecipe) {
    return true;
  }

  @Override
  public void writeToItemStack(ItemStack stack) {
    super.writeToItemStack(stack);
    // save mob the same way as the soul binder adds it to the item
    if (hasEntity() && stack != null) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      capturedMob.toNbt(stack.getTagCompound());
    }
  }

  @Override
  protected void updateEntityClient() {
    if (isActive()) {
      double x = getPos().getX() + worldObj.rand.nextFloat();
      double y = getPos().getY() + worldObj.rand.nextFloat();
      double z = getPos().getZ() + worldObj.rand.nextFloat();
      worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
      worldObj.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
    }
    super.updateEntityClient();
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    PoweredTask res = new PoweredTask(nextRecipe, chance, getRecipeInputs());

    int ticksDelay;
    if (isSpawnMode) {
      ticksDelay = Config.poweredSpawnerMinDelayTicks
          + (int) Math.round((Config.poweredSpawnerMaxDelayTicks - Config.poweredSpawnerMinDelayTicks) * Math.random());
    } else {
      ticksDelay = Config.poweredSpawnerMaxDelayTicks - ((Config.poweredSpawnerMaxDelayTicks - Config.poweredSpawnerMinDelayTicks) / 2);
    }
    ticksDelay /= SPAWNER_SPEEDUP.get(getCapacitorData());
    int powerPerTick = getPowerUsePerTick();
    res.setRequiredEnergy(powerPerTick * ticksDelay);
    return res;
  }

  protected boolean canSpawnEntity(EntityLiving entityliving) {
    boolean spaceClear = worldObj.checkNoEntityCollision(entityliving.getEntityBoundingBox())
        && worldObj.getCollisionBoxes(entityliving, entityliving.getEntityBoundingBox()).isEmpty()
        && (!worldObj.containsAnyLiquid(entityliving.getEntityBoundingBox()) || entityliving.isCreatureType(EnumCreatureType.WATER_CREATURE, false));
    if (spaceClear && Config.poweredSpawnerUseVanillaSpawChecks) {
      // Full checks for lighting, dimension etc
      spaceClear = entityliving.getCanSpawnHere();
    }
    return spaceClear;
  }

  Entity createEntity(DifficultyInstance difficulty, boolean forceAlive) {
    Entity ent = capturedMob.getEntity(worldObj, pos, difficulty, false);
    if (forceAlive && Config.poweredSpawnerMaxPlayerDistance <= 0 && Config.poweredSpawnerDespawnTimeSeconds > 0 && ent instanceof EntityLiving) {
      ent.getEntityData().setLong(BlockPoweredSpawner.KEY_SPAWNED_BY_POWERED_SPAWNER, worldObj.getTotalWorldTime());
      ((EntityLiving) ent).enablePersistence();
    }
    return ent;
  }

  protected boolean trySpawnEntity() {
    Entity entity = createEntity(worldObj.getDifficultyForLocation(getPos()), true);
    if (!(entity instanceof EntityLiving)) {
      return false;
    }
    EntityLiving entityliving = (EntityLiving) entity;

    int spawnRange = getRange();

    if (Config.poweredSpawnerMaxNearbyEntities > 0) {
      int nearbyEntities = worldObj.getEntitiesWithinAABB(entity.getClass(), getBounds().expand(spawnRange, 2, spawnRange)).size();
      if (nearbyEntities >= Config.poweredSpawnerMaxNearbyEntities) {
        return false;
      }
    }

    while (remainingSpawnTries-- > 0) {
      double x = getPos().getX() + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * spawnRange;
      double y = getPos().getY() + worldObj.rand.nextInt(3) - 1;
      double z = getPos().getZ() + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * spawnRange;

      entity.setLocationAndAngles(x, y, z, worldObj.rand.nextFloat() * 360.0F, 0.0F);

      if (canSpawnEntity(entityliving)) {
        worldObj.spawnEntityInWorld(entityliving);
        worldObj.playEvent(2004, getPos(), 0);
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

    final Entity ridingEntity = entity.getRidingEntity();
    if (ridingEntity != null) {
      ridingEntity.setDead();
    }
    for (Entity passenger : entity.getPassengers()) {
      passenger.setDead();
    }
    return false;
  }

  public String getEntityName() {
    return capturedMob != null ? capturedMob.getEntityName() : null;
  }

  public CapturedMob getEntity() {
    return capturedMob;
  }

  public boolean hasEntity() {
    return capturedMob != null;
  }

  @Override
  public void readFromItemStack(ItemStack stack) {
    super.readFromItemStack(stack);
    capturedMob = CapturedMob.create(stack);
  }

  // RANGE

  private BoundingBox bounds;
  private boolean showingRange;

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  private final static Vector4f color = new Vector4f(.94f, .11f, .11f, .4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TilePoweredSpawner>(this, color));
    }
  }

  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    bounds = null;
  }

  @Override
  public BoundingBox getBounds() {
    if (isSpawnMode) {
      if (bounds == null) {
        bounds = new BoundingBox(getPos()).expand(getRange(), 1d, getRange());
        if (capturedMob != null) {
          Entity ent = capturedMob.getEntity(worldObj, false);
          if (ent != null) {
            int height = Math.max((int) Math.ceil(ent.height) - 1, 0);
            bounds = bounds.setMaxY(bounds.maxY + height);
          }
        }
      }
      return bounds;
    } else {
      return new BoundingBox(getPos());
    }
  }

  public int getRange() {
    return Config.poweredSpawnerSpawnRange;
  }

  // RANGE END

}
