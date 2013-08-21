package crazypants.enderio.machine;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class AbstractPoweredTaskEntity extends AbstractMachineEntity implements ISidedInventory {

  protected PoweredTask currentTask = null;
  
  public AbstractPoweredTaskEntity(int inventorySize) {
    super(inventorySize);
  }
  
  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    int[] res = new int[inventory.length];
    for(int i=0;i<res.length;i++) {
      res[i] = i;
    }
    return res;
  }

  @Override
  public boolean canInsertItem(int i, ItemStack itemstack, int j) {
    int maxInputIndex = inventory.length - 2;
    if (i < 0 || i > maxInputIndex) {
      return false;
    }
    if (!isStackValidForSlot(i, itemstack)) {
      return false;
    }
    if (inventory[i] == null) {
      return true;
    }
    if (inventory[i].stackSize + itemstack.stackSize > inventory[i].getMaxStackSize()) {
      return false;
    }
    return inventory[i].isItemEqual(itemstack);
  }
  
  @Override
  public boolean canExtractItem(int i, ItemStack itemstack, int j) {
    int outputIndex = inventory.length - 2;
    if (outputIndex != i) {
      return false;
    }
    if (inventory[outputIndex] == null || inventory[outputIndex].stackSize < itemstack.stackSize) {
      return false;
    }
    return itemstack.itemID == inventory[outputIndex].itemID;
  }
  
  @Override
  public boolean isActive() {
    return currentTask == null ? false : currentTask.getProgress() > 0 && hasPower() && redstoneCheckPassed;
  }

  @Override
  public float getProgress() {
    return currentTask == null ? 0 : currentTask.getProgress();
  }
  
  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
        
    if(!redstoneChecksPassed) {
      return false;
    }
    
    boolean requiresClientSync = false;
    // Process any current items
    requiresClientSync |= checkProgress();
    
    // Then see if we need to start a new one
    IMachineRecipe nextRecipe = canStartNextTask();
    if (nextRecipe != null) {
      requiresClientSync |= startNextTask(nextRecipe);  
    }   
    return requiresClientSync;
  }

  protected boolean checkProgress() {
    if (currentTask == null || !hasPower()) {
      return false;
    }
    float used = powerHandler.useEnergy(0, getPowerUsePerTick(), true);
    currentTask.update(used);
    // then check if we are done
    if (currentTask.isComplete()) {
      taskComplete();
    }
    return true;
  }
  
  protected void taskComplete() {
    int outputIndex = inventory.length - 2;
    if (currentTask != null) {
      ItemStack[] output = currentTask.getCompletedResult();
      if (output != null && output.length > 0) {
        ItemStack result = currentTask.getCompletedResult()[0];
        if (inventory[outputIndex] == null) {
          inventory[outputIndex] = result.copy();
        } else {
          int newStackSize = inventory[outputIndex].stackSize += result.stackSize;
          inventory[outputIndex] = result.copy();
          inventory[outputIndex].stackSize = newStackSize;
        }
      }
    }
    currentTask = null;
  }
  
  protected RecipeInput[] getInputs() {
    RecipeInput[] res = new RecipeInput[inventorySize - 2];
    for(int i=0;i < res.length;i++) {
      res[i] = new RecipeInput(i, inventory[i]);
    }
    return res;
  }

  protected IMachineRecipe canStartNextTask() {
    if (currentTask != null) {
      return null; // already cooking something
    }
    if (!hasPower()) {
      return null; // no heat to cook
    }

    IMachineRecipe nextRecipe = MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(), getInputs());
    if (nextRecipe == null) {
      return null; // no template
    }

    int outputIndex = inventory.length - 2;
    // make sure we can merge the recipe output with our result
    if (inventory[outputIndex] == null) {
      return nextRecipe;
    }
    ItemStack nextResult = nextRecipe.getCompletedResult(getInputs())[0];
    if (inventory[outputIndex].stackSize + nextResult.stackSize > inventory[outputIndex].getMaxStackSize()) {
      return null; // no room for output
    }

    if(!canMergeWithCurrentOuput(nextResult)) {
      return null;
    }

    return nextRecipe;
  }
    
  protected boolean canMergeWithCurrentOuput(ItemStack nextResult) {
    if (!nextResult.isItemEqual(inventory[inventory.length - 2])) {
      // next result is a different item type
      return false;
    }
    return true;
  }

  protected boolean startNextTask(IMachineRecipe nextRecipe) {    
    if (hasPower() && nextRecipe.isRecipe(getInputs())) {
      // then get our recipe and take away the source items
      currentTask = new PoweredTask(nextRecipe, getInputs());
      
      RecipeInput[] consumed = nextRecipe.getQuantitiesConsumed(getInputs());
      for(RecipeInput item : consumed) {
        if(item != null && item.item != null && item.item.stackSize > 0) {
          decrStackSize(item.slotNumber, item.item.stackSize);
        }
      }           
      return true;
    }
    return false;
  }
  

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    currentTask = PoweredTask.readFromNBT(nbtRoot.getCompoundTag("currentTask"));
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    if (currentTask != null) {
      NBTTagCompound currentTaskNBT = new NBTTagCompound();
      currentTask.writeToNBT(currentTaskNBT);
      nbtRoot.setCompoundTag("currentTask", currentTaskNBT);
    }
  }
  
  

}
