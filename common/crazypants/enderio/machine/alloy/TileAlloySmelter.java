package crazypants.enderio.machine.alloy;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;

public class TileAlloySmelter extends AbstractPoweredTaskEntity {

  public static enum Mode {
    ALL,
    ALLOY,
    FURNACE;

    Mode next() {
      int nextOrd = ordinal() + 1;
      if(nextOrd >= values().length) {
        nextOrd = 0;
      }
      return values()[nextOrd];
    }
  }

  private Mode mode;

  public TileAlloySmelter() {
    super(new SlotDefinition(3, 1));
    mode = Mode.ALL;
  }

  @Override
  public String getInvName() {
    return "Alloy Smelter";
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    if(mode == null) {
      mode = Mode.ALL;
    }
    if(this.mode != mode) {
      this.mode = mode;
      forceClientUpdate = true;
    }

  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    IMachineRecipe result = super.canStartNextTask(chance);
    if(mode == Mode.ALLOY && result instanceof VanillaSmeltingRecipe) {
      result = null;
    }
    if(mode == Mode.FURNACE && !(result instanceof VanillaSmeltingRecipe)) {
      result = null;
    }
    return result;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  @Override
  public boolean isMachineItemValidForSlot(int slot, ItemStack itemstack) {
    if(!slotDefinition.isInputSlot(slot)) {
      return false;
    }

    //We will assume anything that is in a slot is valid, so just return whether the new input can be stacked with the current one
    ItemStack currentStackInSlot = inventory[slot];
    if(currentStackInSlot != null) {
      return currentStackInSlot.isItemEqual(itemstack);
    }

    int numSlotsFilled = 0;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if(i >= 0 && i < inventory.length) {
        if(inventory[i] != null && inventory[i].stackSize > 0) {
          numSlotsFilled++;
        }
      }
    }
    List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(slot, itemstack));

    if(mode == Mode.FURNACE) {
      return isValidInputForFurnaceRecipe(itemstack, numSlotsFilled, recipes);
    } else if(mode == Mode.ALLOY) {
      return isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
    }
    return isValidInputForFurnaceRecipe(itemstack, numSlotsFilled, recipes) || isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
  }

  private boolean isValidInputForAlloyRecipe(int slot, ItemStack itemstack, int numSlotsFilled, List<IMachineRecipe> recipes) {
    if(numSlotsFilled == 0) {
      return containsAlloyRecipe(recipes);
    }
    for (IMachineRecipe recipe : recipes) {
      if(!(recipe instanceof VanillaSmeltingRecipe)) {

        if(recipe instanceof AlloyMachineRecipe) {
          ItemStack[] resultInv = new ItemStack[slotDefinition.getNumInputSlots()];
          for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
            if(i >= 0 && i < inventory.length) {
              if(i == slot) {
                resultInv[i] = itemstack;
              } else {
                resultInv[i] = inventory[i];
              }
            }
          }
          if(((AlloyMachineRecipe) recipe).isValidRecipeComponents(resultInv)) {
            return true;
          }

        } else {
          Log.warn("TileAlloySmelter.isMachineItemValidForSlot: A non alloy recipe was returned for the alloy smelter");
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValidInputForFurnaceRecipe(ItemStack itemstack, int numSlotsFilled, List<IMachineRecipe> recipes) {
    if(numSlotsFilled == 0) {
      return containsFurnaceRecipe(recipes);
    }
    return containsFurnaceRecipe(recipes) && isItemAlreadyInASlot(itemstack);
  }

  private boolean isItemAlreadyInASlot(ItemStack itemstack) {
    ItemStack currentStackType = null;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot() && currentStackType == null; i++) {
      currentStackType = inventory[i];
      if(currentStackType != null && currentStackType.isItemEqual(itemstack)) {
        return true;
      }
    }
    return false;
  }

  private boolean containsFurnaceRecipe(List<IMachineRecipe> recipes) {
    for (IMachineRecipe rec : recipes) {
      if(rec instanceof VanillaSmeltingRecipe) {
        return true;
      }
    }
    return false;
  }

  private boolean containsAlloyRecipe(List<IMachineRecipe> recipes) {
    for (IMachineRecipe rec : recipes) {
      if(!(rec instanceof VanillaSmeltingRecipe)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    short mb = nbtRoot.getShort("mode");
    mode = Mode.values()[mb];
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("mode", (short) mode.ordinal());
  }

}
