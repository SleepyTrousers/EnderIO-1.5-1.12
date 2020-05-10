package crazypants.enderio.machine.spawner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.PoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;

public class TilePoweredSpawner extends AbstractPoweredTaskEntity {

  public static final int MIN_SPAWN_DELAY_BASE = Config.poweredSpawnerMinDelayTicks;
  public static final int MAX_SPAWN_DELAY_BASE = Config.poweredSpawnerMaxDelayTicks;

  public static final int POWER_PER_TICK_ONE = Config.poweredSpawnerLevelOnePowerPerTickRF;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor(0, (int) (POWER_PER_TICK_ONE * 1.25), Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int POWER_PER_TICK_TWO = Config.poweredSpawnerLevelTwoPowerPerTickRF;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor(0,(int) (POWER_PER_TICK_TWO * 1.25),
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int POWER_PER_TICK_THREE = Config.poweredSpawnerLevelThreePowerPerTickRF;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor(0,(int) (POWER_PER_TICK_THREE * 1.25),
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int MIN_PLAYER_DISTANCE = Config.poweredSpawnerMaxPlayerDistance;
  public static final boolean USE_VANILLA_SPAWN_CHECKS = Config.poweredSpawnerUseVanillaSpawChecks;

  private static final String NULL_ENTITY_NAME = "None";

  private String entityTypeName;
  private boolean isSpawnMode = true;
  private int powerUsePerTick;
  private int remainingSpawnTries;

  public TilePoweredSpawner() {
    super(new SlotDefinition(1, 1, 1));
    entityTypeName = NULL_ENTITY_NAME;
  }

  public boolean isSpawnMode() {
    return isSpawnMode;
  }

  public void setSpawnMode(boolean isSpawnMode) {
    if(isSpawnMode != this.isSpawnMode) {
      currentTask = null;
    }
    this.isSpawnMode = isSpawnMode;
  }

  @Override
  protected void taskComplete() {
    super.taskComplete();
    if(isSpawnMode) {
      remainingSpawnTries = Config.poweredSpawnerSpawnCount + Config.poweredSpawnerMaxSpawnTries;
      for (int i = 0; i < Config.poweredSpawnerSpawnCount && remainingSpawnTries > 0; ++i) {
        if(!trySpawnEntity()) {
          break;
        }
      }
    } else {
      if(getStackInSlot(0) == null || getStackInSlot(1) != null || !hasEntityName()) {
        return;
      }
      ItemStack res = EnderIO.itemSoulVessel.createVesselWithEntityStub(getEntityName());
      decrStackSize(0, 1);
      setInventorySlotContents(1, res);
    }
  }

  @Override
  public void onCapacitorTypeChange() {
    ICapacitor refCap;
    int basePowerUse;
    switch (getCapacitorType()) {
    default:
    case BASIC_CAPACITOR:
      refCap = CAP_ONE;
      basePowerUse = POWER_PER_TICK_ONE;
      break;
    case ACTIVATED_CAPACITOR:
      refCap = CAP_TWO;
      basePowerUse = POWER_PER_TICK_TWO;
      break;
    case ENDER_CAPACITOR:
      refCap = CAP_THREE;
      basePowerUse = POWER_PER_TICK_THREE;
      break;
    }
    double multiplier = PoweredSpawnerConfig.getInstance().getCostMultiplierFor(getEntityName());
    setCapacitor(new BasicCapacitor((int) (refCap.getMaxEnergyExtracted() * multiplier), refCap.getMaxEnergyStored()));
    powerUsePerTick = (int) Math.ceil(basePowerUse * multiplier);
    forceClientUpdate = true;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPoweredSpawner.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if(itemstack == null || isSpawnMode) {
      return false;
    }
    if(slotDefinition.isInputSlot(i)) {
      return itemstack.getItem() == EnderIO.itemSoulVessel && !EnderIO.itemSoulVessel.containsSoul(itemstack);
    }
    return false;
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    if(!hasEntityName()) {
      return null;
    }
    if(isSpawnMode) {
      if(MIN_PLAYER_DISTANCE > 0) {
        if(worldObj.getClosestPlayer(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, MIN_PLAYER_DISTANCE) == null) {
          return null;
        }
      }
    } else {
      if(getStackInSlot(0) == null || getStackInSlot(1) != null) {
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
  public int getPowerUsePerTick() {
    return powerUsePerTick;
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
  public void readCommon(NBTTagCompound nbtRoot) {
    //Must read the mob type first so we know the multiplier to be used when calculating input/output power
    String mobType = BlockPoweredSpawner.readMobTypeFromNBT(nbtRoot);
    if(mobType == null) {
      mobType = NULL_ENTITY_NAME;
    }
    entityTypeName = mobType;
    if(!nbtRoot.hasKey("isSpawnMode")) {
      isSpawnMode = true;
    } else {
      isSpawnMode = nbtRoot.getBoolean("isSpawnMode");
    }
    super.readCommon(nbtRoot);
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    if(hasEntityName()) {
      BlockPoweredSpawner.writeMobTypeToNBT(nbtRoot, getEntityName());
    } else {
      BlockPoweredSpawner.writeMobTypeToNBT(nbtRoot, null);
    }
    nbtRoot.setBoolean("isSpawnMode", isSpawnMode);
    super.writeCommon(nbtRoot);
  }

  @Override
  protected void updateEntityClient() {
    if(isActive()) {
        double x = xCoord + worldObj.rand.nextFloat();
        double y = yCoord + worldObj.rand.nextFloat();
        double z = zCoord + worldObj.rand.nextFloat();
        worldObj.spawnParticle("smoke", x, y, z, 0.0D, 0.0D, 0.0D);
        worldObj.spawnParticle("flame", x, y, z, 0.0D, 0.0D, 0.0D);
    }
    super.updateEntityClient();
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    PoweredTask res = new PoweredTask(nextRecipe, chance, getRecipeInputs());

    int ticksDelay;
    if(isSpawnMode) {
      ticksDelay = TilePoweredSpawner.MIN_SPAWN_DELAY_BASE
          + (int) Math.round((TilePoweredSpawner.MAX_SPAWN_DELAY_BASE - TilePoweredSpawner.MIN_SPAWN_DELAY_BASE) * Math.random());
    } else {
      ticksDelay = TilePoweredSpawner.MAX_SPAWN_DELAY_BASE - ((TilePoweredSpawner.MAX_SPAWN_DELAY_BASE - TilePoweredSpawner.MIN_SPAWN_DELAY_BASE) / 2);
    }
    if(getCapacitorType().ordinal() == 1) {
      ticksDelay /= 2;
    } else if(getCapacitorType().ordinal() == 2) {
      ticksDelay /= 4;
    }
    int powerPerTick = getPowerUsePerTick();
    res.setRequiredEnergy(powerPerTick * ticksDelay);
    return res;
  }

  protected boolean canSpawnEntity(EntityLiving entityliving) {
    boolean spaceClear = worldObj.checkNoEntityCollision(entityliving.boundingBox)
        && worldObj.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).isEmpty()
        && (!worldObj.isAnyLiquid(entityliving.boundingBox) || entityliving.isCreatureType(EnumCreatureType.waterCreature, false));
    if(spaceClear && USE_VANILLA_SPAWN_CHECKS) {
      //Full checks for lighting, dimension etc
      spaceClear = entityliving.getCanSpawnHere();
    }
    return spaceClear;
  }

  Entity createEntity(boolean forceAlive) {
    Entity ent = EntityList.createEntityByName(getEntityName(), worldObj);
    if(forceAlive && MIN_PLAYER_DISTANCE <= 0 && Config.poweredSpawnerDespawnTimeSeconds > 0 && ent instanceof EntityLiving) {
       ent.getEntityData().setLong(BlockPoweredSpawner.KEY_SPAWNED_BY_POWERED_SPAWNER, worldObj.getTotalWorldTime());
      ((EntityLiving) ent).func_110163_bv();
    }
    return ent;
  }

  protected boolean trySpawnEntity() {
    Entity entity = createEntity(true);
    if(!(entity instanceof EntityLiving)) {
      return false;
    }
    EntityLiving entityliving = (EntityLiving) entity;
    int spawnRange = Config.poweredSpawnerSpawnRange;

    if(Config.poweredSpawnerMaxNearbyEntities > 0) {
      int nearbyEntities = worldObj.getEntitiesWithinAABB(
          entity.getClass(),
          AxisAlignedBB.getBoundingBox(
                  xCoord - spawnRange*2, yCoord - 4, zCoord - spawnRange*2,
                  xCoord + spawnRange*2, yCoord + 4, zCoord + spawnRange*2)).size();

      if(nearbyEntities >= Config.poweredSpawnerMaxNearbyEntities) {
        return false;
      }
    }

    while(remainingSpawnTries-- > 0) {
      double x = xCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * spawnRange;
      double y = yCoord + worldObj.rand.nextInt(3) - 1;
      double z = zCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * spawnRange;
      entity.setLocationAndAngles(x, y, z, worldObj.rand.nextFloat() * 360.0F, 0.0F);

      if(canSpawnEntity(entityliving)) {
        entityliving.onSpawnWithEgg(null);
        worldObj.spawnEntityInWorld(entityliving);
        worldObj.playAuxSFX(2004, xCoord, yCoord, zCoord, 0);
        entityliving.spawnExplosionParticle();
        return true;
      }
    }

    return false;
  }

  public String getEntityName() {
    return entityTypeName;
  }

  public boolean hasEntityName() {
    return !NULL_ENTITY_NAME.equals(entityTypeName);
  }
}
