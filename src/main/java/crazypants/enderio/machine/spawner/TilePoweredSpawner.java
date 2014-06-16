package crazypants.enderio.machine.spawner;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.PoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.Capacitors;

public class TilePoweredSpawner extends AbstractPoweredTaskEntity {

  public static final int MIN_SPAWN_DELAY_BASE = Config.poweredSpawnerMinDelayTicks;
  public static final int MAX_SPAWN_DELAY_BASE = Config.poweredSpawnerMaxDelayTicks;
  public static final float POWER_PER_TICK_ONE = Config.poweredSpawnerLevelOnePowerPerTick;
  public static final float POWER_PER_TICK_TWO = Config.poweredSpawnerLevelTwoPowerPerTick;
  public static final float POWER_PER_TICK_THREE = Config.poweredSpawnerLevelThreePowerPerTick;
  public static final int MIN_PLAYER_DISTANCE = Config.poweredSpawnerMaxPlayerDistance;
  public static final boolean USE_VANILLA_SPAWN_CHECKS = Config.poweredSpawnerUseVanillaSpawChecks;
  
  private final MobSpawnerBaseLogic logic = new SpawnerLogic();

  public TilePoweredSpawner() {
    super(new SlotDefinition(0, 0));
    //logic.setEntityName("Zombie");
    logic.setEntityName("Skeleton");
  }

  @Override
  protected void taskComplete() {
    super.taskComplete();
    logic.spawnDelay = 0;    
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
    return new DummyRecipe();
  }

  @Override
  protected boolean startNextTask(IMachineRecipe nextRecipe, float chance) {
    return super.startNextTask(nextRecipe, chance);
  }

  @Override
  public float getPowerUsePerTick() {
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

  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    logic.readFromNBT(nbtRoot);
  }

  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    logic.writeToNBT(nbtRoot);
  }

  public void updateEntity() {
    logic.updateSpawner();
    super.updateEntity();    
  }

  /**
   * Called when a client event is received with the event number and argument,
   * see World.sendClientEvent
   */
  public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
    return logic.setDelayToMin(p_145842_1_) ? true : super.receiveClientEvent(p_145842_1_, p_145842_2_);
  }
  
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    PoweredTask res = new PoweredTask(nextRecipe, chance, getInputs());
    
    int ticksDelay = TilePoweredSpawner.MIN_SPAWN_DELAY_BASE + (int)Math.round((TilePoweredSpawner.MAX_SPAWN_DELAY_BASE - TilePoweredSpawner.MIN_SPAWN_DELAY_BASE) * Math.random());
    if(capacitorType.ordinal() == 1) {
      ticksDelay /= 2;
    } else if(capacitorType.ordinal() == 2) {
      ticksDelay /= 4;
    }
    int powerPerTick = (int)getPowerUsePerTick();
    res.setRequiredEnergy(powerPerTick * ticksDelay);    
    return res;
  }
  
  protected boolean canSpawnEntity(EntityLiving entityliving) {   
    boolean spaceClear = worldObj.checkNoEntityCollision(entityliving.boundingBox) && worldObj.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).isEmpty() && !worldObj.isAnyLiquid(entityliving.boundingBox);
    if(spaceClear && USE_VANILLA_SPAWN_CHECKS) {
      //Full checks for lighting, dimension etc 
      spaceClear = entityliving.getCanSpawnHere();
    }
    return spaceClear;
  }

  class SpawnerLogic extends MobSpawnerBaseLogic {

    private int spawnCount = 4;    
    private int maxNearbyEntities = 6;
    private int spawnRange = 4;
    
    public void func_98267_a(int par1) {
      worldObj.addBlockEvent(xCoord, yCoord, zCoord, EnderIO.blockPoweredSpawner, par1, 0);
    }

    public World getSpawnerWorld() {
      return worldObj;
    }

    public int getSpawnerX() {
      return xCoord;
    }

    public int getSpawnerY() {
      return yCoord;
    }

    public int getSpawnerZ() {
      return zCoord;
    }

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
    public boolean isActivated() {
      if(MIN_PLAYER_DISTANCE > 0) {
        //TODO: Add this to main 'hasPower' like check so turn of the machine if the player is out of range?
        boolean playerInRange = worldObj.getClosestPlayer((double)getSpawnerX() + 0.5D, (double)getSpawnerY() + 0.5D, (double)getSpawnerZ() + 0.5D, (double)MIN_PLAYER_DISTANCE) != null;
        if(!playerInRange) {
          return false;
        }
      }
      return isActive();      
    }

    public void updateSpawner() {
      
      if(isActivated()) {
        double d2;

        if(getSpawnerWorld().isRemote) {
          double d0 = (double) ((float) getSpawnerX() + getSpawnerWorld().rand.nextFloat());
          double d1 = (double) ((float) getSpawnerY() + getSpawnerWorld().rand.nextFloat());
          d2 = (double) ((float) getSpawnerZ() + getSpawnerWorld().rand.nextFloat());
          getSpawnerWorld().spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
          getSpawnerWorld().spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);

          if(spawnDelay > 0)
          {
            --spawnDelay;
          }

          field_98284_d = field_98287_c;
          field_98287_c = (field_98287_c + (double) (1000.0F / ((float) spawnDelay + 200.0F))) % 360.0D;
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
                AxisAlignedBB
                    .getAABBPool()
                    .getAABB((double) getSpawnerX(), (double) getSpawnerY(), (double) getSpawnerZ(), (double) (getSpawnerX() + 1),
                        (double) (getSpawnerY() + 1), (double) (getSpawnerZ() + 1)).expand((double) (spawnRange * 2), 4.0D, (double) (spawnRange * 2))).size();

            if(j >= maxNearbyEntities) {
              resetTimer();
              return;
            }

            d2 = (double) getSpawnerX() + (getSpawnerWorld().rand.nextDouble() - getSpawnerWorld().rand.nextDouble()) * (double) spawnRange;
            double d3 = (double) (getSpawnerY() + getSpawnerWorld().rand.nextInt(3) - 1);
            double d4 = (double) getSpawnerZ() + (getSpawnerWorld().rand.nextDouble() - getSpawnerWorld().rand.nextDouble()) * (double) spawnRange;
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
