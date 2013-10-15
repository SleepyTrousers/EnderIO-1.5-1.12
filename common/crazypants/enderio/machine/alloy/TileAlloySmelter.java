package crazypants.enderio.machine.alloy;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
    if(slot >= slotDefinition.getNumSlots()) {
      return false;
    }

    //if we are already processing a recipe and have more ingredients for it, only allow more items for that same recipe to be added

    int numSlotsFilled = 0;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if(i >= 0 && i < inventory.length) {
        if(inventory[i] != null && inventory[i].stackSize > 0) {
          numSlotsFilled++;
        }
      }
    }

    //No task or all the slots are empty so just check for a new recipe
    if(numSlotsFilled == 0 || currentTask == null) {
      List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(slot, itemstack));
      if(mode == Mode.ALLOY) {
        return containsAlloyRecipe(recipes);
      } else if(mode == mode.FURNACE) {
        return containsFurnaceRecipe(recipes);
      }
      return !recipes.isEmpty();
    }

    //If we are processing as vanilla recipe, allow all the slots to be filled with a single item
    if(currentTask.getRecipe() instanceof VanillaSmeltingRecipe || mode == Mode.FURNACE) {
      ItemStack currentStackType = null;
      for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot() && currentStackType == null; i++) {
        currentStackType = inventory[i];
      }
      if(currentStackType != null && currentStackType.isItemEqual(itemstack)) {
        return true;
      }
      return false;

    } else {
      //Its an alloy so only allow existing stacks to be added to
      ItemStack stackInSlot = inventory[slot];
      if(stackInSlot == null) {
        return false;
      }

      return stackInSlot.isItemEqual(itemstack);
    }
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
