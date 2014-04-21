package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;

public abstract class AbstractPoweredTaskEntity extends AbstractMachineEntity implements ISidedInventory {

  protected PoweredTask currentTask = null;
  protected IMachineRecipe lastCompletedRecipe;

  private final Random random = new Random();

  public AbstractPoweredTaskEntity(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    ForgeDirection dir = ForgeDirection.getOrientation(var1);
    IoMode mode = getIoMode(dir);
    if(mode == IoMode.DISABLED) {
      return new int[0];
    }

    int[] res = new int[inventory.length - slotDefinition.getNumUpgradeSlots()];
    int index = 0;
    for (int i = 0; i < inventory.length; i++) {
      if(!slotDefinition.isUpgradeSlot(i)) {
        res[index] = i;
        index++;
      }
    }
    return res;
  }

  @Override
  public boolean canInsertItem(int i, ItemStack itemstack, int j) {
    if(!super.canInsertItem(i, itemstack, j)) {
      return false;
    }
    if(!isItemValidForSlot(i, itemstack)) {
      return false;
    }
    if(inventory[i] == null) {
      return true;
    }
    return inventory[i].isItemEqual(itemstack);
  }

  @Override
  public boolean isActive() {
    return currentTask == null ? false : currentTask.getProgress() > 0 && hasPower() && redstoneCheckPassed;
  }

  @Override
  public float getProgress() {
    return currentTask == null ? 0 : currentTask.getProgress();
  }

  public float getExperienceForOutput(ItemStack output) {
    if(lastCompletedRecipe == null) {
      return 0;
    }
    return lastCompletedRecipe.getExperianceForOutput(output);
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {

    if(!redstoneChecksPassed) {
      return false;
    }

    boolean requiresClientSync = false;
    // Process any current items
    requiresClientSync |= checkProgress();

    if(currentTask != null || !hasPower() || !hasInputStacks()) {
      return requiresClientSync;
    }

    float chance = random.nextFloat();
    // Then see if we need to start a new one
    IMachineRecipe nextRecipe = canStartNextTask(chance);
    if(nextRecipe != null) {
      requiresClientSync |= startNextTask(nextRecipe, chance);
    }
    return requiresClientSync;
  }

  protected boolean checkProgress() {
    if(currentTask == null || !hasPower()) {
      return false;
    }

    double used = Math.min(powerHandler.getEnergyStored(), getPowerUsePerTick());
    powerHandler.setEnergy(powerHandler.getEnergyStored() - used);
    currentTask.update((float) used);

    // then check if we are done
    if(currentTask.isComplete()) {
      taskComplete();
      return true;
    }
    return worldObj.getWorldTime() % 10 == 0;
  }

  protected void taskComplete() {

    if(currentTask != null) {
      lastCompletedRecipe = currentTask.getRecipe();
      ResultStack[] output = currentTask.getCompletedResult();

      if(output != null && output.length > 0) {
        ResultStack[] results = currentTask.getCompletedResult();

        List<ItemStack> outputStacks = new ArrayList<ItemStack>(slotDefinition.getNumOutputSlots());
        if(slotDefinition.getNumOutputSlots() > 0) {
          for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
            ItemStack it = inventory[i];
            if(it != null) {
              it = it.copy();
            }
            outputStacks.add(it);
          }
        }

        for (ResultStack result : results) {
          if(result.item != null) {
            mergeItemResult(result.item, outputStacks);
          } else if(result.fluid != null) {
            mergeFluidResult(result);
          }
        }

        if(slotDefinition.getNumOutputSlots() > 0) {
          int listIndex = 0;
          for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
            ItemStack st = outputStacks.get(listIndex);
            inventory[i] = st;
            listIndex++;
          }
        }

      }
    }
    currentTask = null;
  }

  protected void mergeFluidResult(ResultStack result) {
  }

  protected void drainInputFluid(MachineRecipeInput fluid) {
  }

  protected boolean canInsertResultFluid(ResultStack fluid) {
    return false;
  }

  private boolean mergeItemResult(ItemStack item, List<ItemStack> outputStacks) {

    //try to add it to existing stacks first
    item = item.copy();
    for (ItemStack outStack : outputStacks) {
      if(outStack != null && item != null) {
        int num = getNumCanMerge(outStack, item);
        outStack.stackSize += num;
        item.stackSize -= num;
        if(item.stackSize <= 0) {
          return true;
        }
      }
    }

    //Try and add it to an empty slot
    for (int i = 0; i < outputStacks.size(); i++) {
      ItemStack outStack = outputStacks.get(i);
      if(outStack == null) {
        outputStacks.set(i, item);
        return true;
      }
    }

    return false;
  }

  protected MachineRecipeInput[] getInputs() {
    MachineRecipeInput[] res = new MachineRecipeInput[slotDefinition.getNumInputSlots()];
    int fromSlot = slotDefinition.minInputSlot;
    for (int i = 0; i < res.length; i++) {
      res[i] = new MachineRecipeInput(fromSlot, inventory[fromSlot]);
      fromSlot++;
    }

    return res;
  }

  protected IMachineRecipe canStartNextTask(float chance) {
    IMachineRecipe nextRecipe = MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(), getInputs());
    if(nextRecipe == null) {
      return null; // no template
    }
    // make sure we have room for the next output
    return canInsertResult(chance, nextRecipe) ? nextRecipe : null;
  }

  protected boolean canInsertResult(float chance, IMachineRecipe nextRecipe) {

    ResultStack[] nextResults = nextRecipe.getCompletedResult(chance, getInputs());
    List<ItemStack> outputStacks = new ArrayList<ItemStack>(slotDefinition.getNumOutputSlots());
    if(slotDefinition.getNumOutputSlots() > 0) {
      for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
        ItemStack st = inventory[i];
        if(st != null) {
          st = st.copy();
        }
        outputStacks.add(st);
      }
    }

    for (ResultStack result : nextResults) {
      if(result.item != null) {
        if(!mergeItemResult(result.item, outputStacks)) {
          return false;
        }
      } else if(result.fluid != null) {
        if(!canInsertResultFluid(result)) {
          return false;
        }
      }
    }

    return true;
  }

  protected boolean hasInputStacks() {
    int fromSlot = slotDefinition.minInputSlot;
    for (int i = 0; i < slotDefinition.getNumInputSlots(); i++) {
      if(inventory[fromSlot] != null) {
        return true;
      }
      fromSlot++;
    }
    return false;
  }

  protected int getNumCanMerge(ItemStack itemStack, ItemStack result) {
    if(!itemStack.isItemEqual(result)) {
      return 0;
    }
    return Math.min(itemStack.getMaxStackSize() - itemStack.stackSize, result.stackSize);
  }

  protected boolean startNextTask(IMachineRecipe nextRecipe, float chance) {
    if(hasPower() && nextRecipe.isRecipe(getInputs())) {
      // then get our recipe and take away the source items
      currentTask = new PoweredTask(nextRecipe, chance, getInputs());

      List<MachineRecipeInput> consumed = nextRecipe.getQuantitiesConsumed(getInputs());
      for (MachineRecipeInput item : consumed) {
        if(item != null) {
          if(item.item != null && item.item.stackSize > 0) {
            decrStackSize(item.slotNumber, item.item.stackSize);
          } else if(item.fluid != null) {
            drainInputFluid(item);
          }

        }
      }
      return true;
    }
    return false;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    currentTask = PoweredTask.readFromNBT(nbtRoot.getCompoundTag("currentTask"));
    String uid = nbtRoot.getString("lastCompletedRecipe");
    lastCompletedRecipe = MachineRecipeRegistry.instance.getRecipeForUid(uid);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    if(currentTask != null) {
      NBTTagCompound currentTaskNBT = new NBTTagCompound();
      currentTask.writeToNBT(currentTaskNBT);
      nbtRoot.setTag("currentTask", currentTaskNBT);
    }
    if(lastCompletedRecipe != null) {
      nbtRoot.setString("lastCompletedRecipe", lastCompletedRecipe.getUid());
    }
  }

}
