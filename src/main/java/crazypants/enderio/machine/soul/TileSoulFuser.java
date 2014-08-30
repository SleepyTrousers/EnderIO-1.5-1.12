package crazypants.enderio.machine.soul;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileSoulFuser extends AbstractPoweredTaskEntity {

  public static final float POWER_PER_TICK_ONE = (float)Config.soulFuserLevelOnePowerPerTick;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor((int) (POWER_PER_TICK_ONE * 1.25), 
      Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final float POWER_PER_TICK_TWO = (float)Config.soulFuserLevelTwoPowerPerTick;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor((int) (POWER_PER_TICK_TWO * 1.25),
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored());

  public static final float POWER_PER_TICK_THREE = (float)Config.soulFuserLevelThreePowerPerTick;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor((int) (POWER_PER_TICK_THREE * 1.25),
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored());
  
  public TileSoulFuser() {
    super(new SlotDefinition(2, 2, 1));
  }

  @Override
  public String getMachineName() {    
    return ModObject.blockSoulFuser.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slot, ItemStack item) {
    return SoulFuserSpawnerRecipe.instance.isValidInput(new MachineRecipeInput(slot, item));
  }

  public void setCapacitor(Capacitors capacitorType) {
    this.capacitorType = capacitorType;
    switch (capacitorType) {
    case BASIC_CAPACITOR:
      PowerHandlerUtil.configure(powerHandler, CAP_ONE);
      break;
    case ACTIVATED_CAPACITOR:
      PowerHandlerUtil.configure(powerHandler, CAP_TWO);
      break;
    case ENDER_CAPACITOR:
      PowerHandlerUtil.configure(powerHandler, CAP_THREE);
      break;
    default:
      PowerHandlerUtil.configure(powerHandler, CAP_ONE);
      break;
    }
    forceClientUpdate = true;
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
  public int getProgressScaled(int scale) {
    int res = super.getProgressScaled(scale);
    if(currentTask != null) {
      res = Math.max(1, res);
    }
    return res;
  }
  
  
  

}
