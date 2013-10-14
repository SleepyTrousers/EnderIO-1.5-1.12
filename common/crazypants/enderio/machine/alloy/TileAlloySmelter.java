package crazypants.enderio.machine.alloy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;

public class TileAlloySmelter extends AbstractPoweredTaskEntity {

  private boolean furnaceRecipesEnabled;

  public TileAlloySmelter() {
    super(new SlotDefinition(3, 1));
    furnaceRecipesEnabled = true;
  }

  @Override
  public String getInvName() {
    return "Alloy Smelter";
  }

  public boolean areFurnaceRecipesEnabled() {
    return furnaceRecipesEnabled;
  }

  public void setFurnaceRecipesEnabled(boolean furnaceRecipesEnabled) {
    if (this.furnaceRecipesEnabled != furnaceRecipesEnabled) {
      this.furnaceRecipesEnabled = furnaceRecipesEnabled;
      forceClientUpdate = true;
    }
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    IMachineRecipe result = super.canStartNextTask(chance);
    if (!furnaceRecipesEnabled && result instanceof VanillaSmeltingRecipe) {
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
    if (slot >= slotDefinition.getNumSlots()) {
      return false;
    }

    // if we are already processing a recipe and have more ingrediaents for it,
    // only allow more items for that same recipe to be added

    boolean slotsEmpty = true;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if (i >= 0 && i < inventory.length) {
        if (inventory[i] != null && inventory[i].stackSize > 0) {
          slotsEmpty = false;
        }
      }
    }

    // No task or all teh slots are empty so just check for a new recipe
    if (slotsEmpty || currentTask == null) {
      return !MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(slot, itemstack)).isEmpty();
    }

    // If we are processing as vanilla recipe, allow all the slots to be filled
    // with a single item
    if (currentTask.getRecipe() instanceof VanillaSmeltingRecipe) {
      ItemStack currentStackType = null;
      for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot() && currentStackType == null; i++) {
        currentStackType = inventory[i];
      }
      if (currentStackType != null && currentStackType.isItemEqual(itemstack)) {
        return true;
      }
      return false;

    } else {
      // Its an alloy so only allow existing stacks to be added to
      ItemStack stackInSlot = inventory[slot];
      if (stackInSlot == null) {
        return false;
      }

      return stackInSlot.isItemEqual(itemstack);
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    furnaceRecipesEnabled = nbtRoot.getBoolean("furnaceRecipesEnabled");
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setBoolean("furnaceRecipesEnabled", furnaceRecipesEnabled);
  }

}
