package crazypants.enderio.machine.alloy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.MachineRecipeInput;
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
    if(this.furnaceRecipesEnabled != furnaceRecipesEnabled) {
      this.furnaceRecipesEnabled = furnaceRecipesEnabled;
      forceClientUpdate = true;
    }
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    IMachineRecipe result = super.canStartNextTask(chance);
    if(!furnaceRecipesEnabled && result instanceof VanillaSmeltingRecipe) {
      result = null;
    }
    return result;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if(i >= slotDefinition.getNumSlots()) {
      return false;
    }
    return !MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(i, itemstack)).isEmpty();
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
