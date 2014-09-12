package crazypants.enderio.machine.spawnguard;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.wireless.WirelessChargerController;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.vecmath.Vector3d;

public class TileSpawnGuard extends AbstractMachineEntity {

  private ICapacitor capacitor;
  private int powerPerTick;
  private int range;
  private int rangeSqu;
  private boolean registered = false;
  
  public TileSpawnGuard() {
    super(new SlotDefinition(12, 0));
    setUpdrade(Capacitors.BASIC_CAPACITOR);
  }
  
  @Override
  public void invalidate() {
    super.invalidate();    
    SpawnGuardController.instance.deregisterGuard(this);
    registered = false;
  }

  @Override
  public void setCapacitor(Capacitors capacitorType) {
    setUpdrade(capacitorType);
    super.setCapacitor(capacitorType);
  }

  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
  }

  private void setUpdrade(Capacitors capacitorType) {    
    switch (capacitorType) {
    case ACTIVATED_CAPACITOR:
      range = Config.spawnGuardRangeLevelTwo;
      powerPerTick = Config.spawnGuardPowerPerTickLevelTwo;
      break;
    case ENDER_CAPACITOR:
      range = Config.spawnGuardRangeLevelThree;
      powerPerTick = Config.spawnGuardPowerPerTickLevelThree;
      break;
    case BASIC_CAPACITOR:
    default:
      range = Config.spawnGuardRangeLevelOne;
      powerPerTick = Config.spawnGuardPowerPerTickLevelOne;
      break;
    }
    rangeSqu = range * range;    
    capacitor = new BasicCapacitor(powerPerTick * 8, capacitorType.capacitor.getMaxEnergyStored(), powerPerTick);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockSpawnGuard.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if(!slotDefinition.isInputSlot(i)) {
      return false;
    }
    String mob = EnderIO.itemSoulVessel.getMobTypeFromStack(itemstack);
    if(mob == null) {
      return false;
    }
    Class<?> cl = (Class<?>) EntityList.stringToClassMapping.get(mob);
    if(cl == null) {
      return false;
    }
    return EntityLiving.class.isAssignableFrom(cl);
  }

  @Override
  public boolean isActive() {
    return hasPower();
  }

  @Override
  public float getProgress() {
    return 0;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    if(redstoneCheckPassed && hasPower()) {
      if(!registered) {
        SpawnGuardController.instance.registerGuard(this);
        registered = true;
      }
      usePower();
    } 
    return false;    
  }

  protected double usePower() {
    return usePower(getPowerUsePerTick());
  }

  protected int usePower(int wantToUse) {
    int used = Math.min(getEnergyStored(), wantToUse);
    setEnergyStored(Math.max(0, getEnergyStored() - used));
    return used;
  }

  @Override
  public int getPowerUsePerTick() {
    return powerPerTick;
  }

  public boolean isSpawnPrevented(EntityLivingBase mob) {
    return redstoneCheckPassed && hasPower() && isMobInRange(mob) && isMobInFilter(mob);
  }
  
  private boolean isMobInRange(EntityLivingBase mob) {
    if(mob == null) {
      return false;
    }    
    return new Vector3d(mob.posX, mob.posY, mob.posZ).distanceSquared(new Vector3d(xCoord, yCoord, zCoord)) <= rangeSqu;
  }

  private boolean isMobInFilter(EntityLivingBase ent) {
    return isMobInFilter(EntityList.getEntityString(ent));
  }

  private boolean isMobInFilter(String entityId) {
    for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
      if(inventory[i] != null) {
        String mob = EnderIO.itemSoulVessel.getMobTypeFromStack(inventory[i]);
        if(mob != null && mob.equals(entityId)) {
          return true;
        }
      }
    }
    return false;
  }

  
}
