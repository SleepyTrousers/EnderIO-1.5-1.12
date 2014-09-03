package crazypants.enderio.machine.spawner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
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
import crazypants.enderio.power.PowerHandlerUtil;

public class TilePoweredSpawner extends AbstractPoweredTaskEntity {

  public static final int MIN_SPAWN_DELAY_BASE = Config.poweredSpawnerMinDelayTicks;
  public static final int MAX_SPAWN_DELAY_BASE = Config.poweredSpawnerMaxDelayTicks;

  public static final int POWER_PER_TICK_ONE = Config.poweredSpawnerLevelOnePowerPerTickRF;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor((int) (POWER_PER_TICK_ONE * 1.25), Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int POWER_PER_TICK_TWO = Config.poweredSpawnerLevelTwoPowerPerTickRF;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor((int) (POWER_PER_TICK_TWO * 1.25),
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int POWER_PER_TICK_THREE = Config.poweredSpawnerLevelThreePowerPerTickRF;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor((int) (POWER_PER_TICK_THREE * 1.25),
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int MIN_PLAYER_DISTANCE = Config.poweredSpawnerMaxPlayerDistance;
  public static final boolean USE_VANILLA_SPAWN_CHECKS = Config.poweredSpawnerUseVanillaSpawChecks;

  private final MobSpawnerBaseLogic logic = new SpawnerLogic();

  private static final String NULL_ENTITY_NAME = "None";
  
  private ICapacitor capacitor;

  public TilePoweredSpawner() {
    super(new SlotDefinition(0, 0));
    logic.setEntityName(NULL_ENTITY_NAME);
    capacitor = CAP_ONE;
  }

  @Override
  protected void taskComplete() {
    super.taskComplete();
    logic.spawnDelay = 0;
  }

  @Override
  public void setCapacitor(Capacitors capacitorType) {
    this.capacitorType = capacitorType;
    switch (capacitorType) {
    case BASIC_CAPACITOR:
      capacitor = CAP_ONE;
      break;
    case ACTIVATED_CAPACITOR:
      capacitor = CAP_ONE;
      break;
    case ENDER_CAPACITOR:
      capacitor = CAP_ONE;
      break;
    default:
      capacitor = CAP_ONE;
      break;
    }
    forceClientUpdate = true;
  }

  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPoweredSpawner.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    if(logic.getEntityNameToSpawn() == null || logic.getEntityNameToSpawn().equals(NULL_ENTITY_NAME)) {
      return null;
    }
    return new DummyRecipe();
  }

  @Override
  protected boolean startNextTask(IMachineRecipe nextRecipe, float chance) {
    return super.startNextTask(nextRecipe, chance);
  }

  @Override
  public int getPowerUsePerTick() {
    if(capacitorType.ordinal() == 0) {
      return POWER_PER_TICK_ONE;
    } else if(capacitorType.ordinal() == 1) {
      return POWER_PER_TICK_TWO;
    }
    return POWER_PER_TICK_THREE;
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
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    logic.readFromNBT(nbtRoot);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    logic.writeToNBT(nbtRoot);

  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    String mobType = BlockPoweredSpawner.readMobTypeFromNBT(nbtRoot);
    if(mobType == null) {
      mobType = NULL_ENTITY_NAME;
    }
    logic.setEntityName(mobType);
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    String mobType = logic.getEntityNameToSpawn();
    if(mobType == null || mobType.equals(NULL_ENTITY_NAME)) {
      BlockPoweredSpawner.writeMobTypeToNBT(nbtRoot, null);
    } else {
      BlockPoweredSpawner.writeMobTypeToNBT(nbtRoot, mobType);
    }
  }

  @Override
  public void updateEntity() {
    logic.updateSpawner();
    super.updateEntity();
  }

  /**
   * Called when a client event is received with the event number and argument,
   * see World.sendClientEvent
   */
  @Override
  public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
    return logic.setDelayToMin(p_145842_1_) ? true : super.receiveClientEvent(p_145842_1_, p_145842_2_);
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    PoweredTask res = new PoweredTask(nextRecipe, chance, getInputs());

    int ticksDelay = TilePoweredSpawner.MIN_SPAWN_DELAY_BASE
        + (int) Math.round((TilePoweredSpawner.MAX_SPAWN_DELAY_BASE - TilePoweredSpawner.MIN_SPAWN_DELAY_BASE) * Math.random());
    if(capacitorType.ordinal() == 1) {
      ticksDelay /= 2;
    } else if(capacitorType.ordinal() == 2) {
      ticksDelay /= 4;
    }
    int powerPerTick = (int) getPowerUsePerTick();
    res.setRequiredEnergy(powerPerTick * ticksDelay);
    return res;
  }

  protected boolean canSpawnEntity(EntityLiving entityliving) {
    boolean spaceClear = worldObj.checkNoEntityCollision(entityliving.boundingBox)
        && worldObj.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).isEmpty() && !worldObj.isAnyLiquid(entityliving.boundingBox);
    if(spaceClear && USE_VANILLA_SPAWN_CHECKS) {
      //Full checks for lighting, dimension etc 
      spaceClear = entityliving.getCanSpawnHere();
    }
    return spaceClear;
  }
  
  public String getEntityName() {
    return logic.getEntityNameToSpawn();
  }

  class SpawnerLogic extends MobSpawnerBaseLogic {

    private int spawnCount = 4;
    private int maxNearbyEntities = 6;
    private int spawnRange = 4;

    @Override
    public void func_98267_a(int par1) {
      worldObj.addBlockEvent(xCoord, yCoord, zCoord, EnderIO.blockPoweredSpawner, par1, 0);
    }

    @Override
    public World getSpawnerWorld() {
      return worldObj;
    }

    @Override
    public int getSpawnerX() {
      return xCoord;
    }

    @Override
    public int getSpawnerY() {
      return yCoord;
    }

    @Override
    public int getSpawnerZ() {
      return zCoord;
    }

    @Override
    public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart par1WeightedRandomMinecart) {
      super.setRandomEntity(par1WeightedRandomMinecart);
      if(getSpawnerWorld() != null) {
        getSpawnerWorld().markBlockForUpdate(xCoord, yCoord, zCoord);
      }
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to
     * activate it.
     */
    @Override
    public boolean isActivated() {
      if(MIN_PLAYER_DISTANCE > 0) {
        //TODO: Add this to main 'hasPower' like check so turn of the machine if the player is out of range?
        boolean playerInRange = worldObj.getClosestPlayer(getSpawnerX() + 0.5D, getSpawnerY() + 0.5D, getSpawnerZ() + 0.5D,
            MIN_PLAYER_DISTANCE) != null;
        if(!playerInRange) {
          return false;
        }
      }
      return isActive();
    }

    @Override
    public void updateSpawner() {

      if(isActivated()) {
        double d2;

        if(getSpawnerWorld().isRemote) {
          double d0 = getSpawnerX() + getSpawnerWorld().rand.nextFloat();
          double d1 = getSpawnerY() + getSpawnerWorld().rand.nextFloat();
          d2 = getSpawnerZ() + getSpawnerWorld().rand.nextFloat();
          getSpawnerWorld().spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
          getSpawnerWorld().spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);

          if(spawnDelay > 0)
          {
            --spawnDelay;
          }

          field_98284_d = field_98287_c;
          field_98287_c = (field_98287_c + 1000.0F / (spawnDelay + 200.0F)) % 360.0D;
        } else {

          if(spawnDelay == -1) {
            resetTimer();
          }
          if(spawnDelay > 0) {
            --spawnDelay;
            return;
          }

          boolean doTimerReset = false;

          for (int i = 0; i < spawnCount; ++i) {

            Entity entity = EntityList.createEntityByName(getEntityNameToSpawn(), getSpawnerWorld());

            if(entity == null) {
              return;
            }

            int j = getSpawnerWorld().getEntitiesWithinAABB(
                entity.getClass(),
                AxisAlignedBB.getBoundingBox(getSpawnerX(), getSpawnerY(), getSpawnerZ(), getSpawnerX() + 1,
                    getSpawnerY() + 1, getSpawnerZ() + 1).expand(spawnRange * 2, 4.0D, spawnRange * 2)).size();

            if(j >= maxNearbyEntities) {
              resetTimer();
              return;
            }

            d2 = getSpawnerX() + (getSpawnerWorld().rand.nextDouble() - getSpawnerWorld().rand.nextDouble()) * spawnRange;
            double d3 = getSpawnerY() + getSpawnerWorld().rand.nextInt(3) - 1;
            double d4 = getSpawnerZ() + (getSpawnerWorld().rand.nextDouble() - getSpawnerWorld().rand.nextDouble()) * spawnRange;
            EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
            entity.setLocationAndAngles(d2, d3, d4, getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

            if(entityliving == null || canSpawnEntity(entityliving)) {
              func_98265_a(entity);
              getSpawnerWorld().playAuxSFX(2004, getSpawnerX(), getSpawnerY(), getSpawnerZ(), 0);

              if(entityliving != null)
              {
                entityliving.spawnExplosionParticle();
              }

              doTimerReset = true;
            }
          }

          if(doTimerReset) {
            resetTimer();
          }
        }
      }
    }

    void resetTimer() {
      spawnDelay = Integer.MAX_VALUE;
    }

  }

}
