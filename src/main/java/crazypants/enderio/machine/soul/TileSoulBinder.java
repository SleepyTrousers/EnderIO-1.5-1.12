package crazypants.enderio.machine.soul;

import java.util.List;

import scala.xml.persistent.SetStorage;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileSoulBinder extends AbstractPoweredTaskEntity {

  public static final int POWER_PER_TICK_ONE = Config.soulBinderLevelOnePowerPerTickRF;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor(POWER_PER_TICK_ONE * 2, 
      Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_ONE);

  public static final int POWER_PER_TICK_TWO = Config.soulBinderLevelTwoPowerPerTickRF;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor(POWER_PER_TICK_TWO * 2,
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_TWO);

  public static final int  POWER_PER_TICK_THREE = Config.soulBinderLevelThreePowerPerTickRF;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor(POWER_PER_TICK_THREE * 2,
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_THREE);
  
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
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slot, ItemStack item) {
    if(!slotDefinition.isInputSlot(slot)) {
      return false;
    }
    MachineRecipeInput newInput = new MachineRecipeInput(slot, item);
    int otherSlot = slot == 0 ? 1 : 0;    
    if(inventory[otherSlot] == null) {
      List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), newInput);
      if(recipes.isEmpty()) {
        return false;
      }    
      for(IMachineRecipe rec : recipes) {
        if(rec != null && rec.isValidInput(newInput)) {
          return true;
        }
      }  
    } else {
      MachineRecipeInput[] inputs = new MachineRecipeInput[] {
          newInput,
          new MachineRecipeInput(otherSlot, inventory[otherSlot])
      };
      return MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(), inputs) != null;
    }
    return false;
    //return SoulBinderSpawnerRecipe.instance.isValidInput(new MachineRecipeInput(slot, item));
  }

  public void setCapacitor(Capacitors capacitorType) {    
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
    super.setCapacitor(capacitorType);
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
