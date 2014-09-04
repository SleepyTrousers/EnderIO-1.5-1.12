package crazypants.enderio.machine.soul;

import scala.xml.persistent.SetStorage;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileSoulBinder extends AbstractPoweredTaskEntity {

  public static final int POWER_PER_TICK_ONE = Config.soulBinderLevelOnePowerPerTickRF;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor((int) (POWER_PER_TICK_ONE * 2), 
      Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int POWER_PER_TICK_TWO = Config.soulBinderLevelTwoPowerPerTickRF;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor((int) (POWER_PER_TICK_TWO * 2),
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final int  POWER_PER_TICK_THREE = Config.soulBinderLevelThreePowerPerTickRF;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor((int) (POWER_PER_TICK_THREE * 2),
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored());
  
  private ICapacitor capacitor;
  
  public TileSoulBinder() {
    super(new SlotDefinition(2, 2, 1));
    capacitor = CAP_ONE;
  }

  @Override
  public String getMachineName() {    
    return ModObject.blockSoulBinder.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slot, ItemStack item) {
    return SoulBinderSpawnerRecipe.instance.isValidInput(new MachineRecipeInput(slot, item));
  }

  public void setCapacitor(Capacitors capacitorType) {
    this.capacitorType = capacitorType;
    switch (capacitorType) {
    case BASIC_CAPACITOR:
      capacitor = CAP_ONE;
      break;
    case ACTIVATED_CAPACITOR:
      capacitor = CAP_TWO;
      break;
    case ENDER_CAPACITOR:
      capacitor = CAP_THREE;
      break;
    default:
      capacitor = CAP_ONE;
      break;
    }
    //Force a check that the new value is in bounds
    setEnergyStored(getEnergyStored());
    forceClientUpdate = true;
  }
  
  
  
  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
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
  public int getProgressScaled(int scale) {
    int res = super.getProgressScaled(scale);
    if(currentTask != null) {
      res = Math.max(1, res);
    }
    return res;
  }
  
  
  

}
