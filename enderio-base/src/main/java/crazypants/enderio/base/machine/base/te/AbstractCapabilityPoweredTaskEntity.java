package crazypants.enderio.base.machine.base.te;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.NBTAction;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.task.PoweredTask;
import crazypants.enderio.base.machine.task.PoweredTaskProgress;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractCapabilityPoweredTaskEntity extends AbstractCapabilityPoweredMachineEntity implements IProgressTile {

  @Store({ NBTAction.SAVE, NBTAction.ITEM }) protected IPoweredTask currentTask = null;
  @Store({ NBTAction.SAVE, NBTAction.ITEM }) protected IMachineRecipe lastCompletedRecipe;
  protected IMachineRecipe cachedNextRecipe;

  protected int ticksSinceCheckedRecipe = 0;
  protected boolean startFailed = false;
  private Long theNextSeed = null;

  public AbstractCapabilityPoweredTaskEntity(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractCapabilityPoweredTaskEntity(@Nonnull EnderInventory inv, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(inv, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  public boolean isActive() {
    return currentTask != null && currentTask.getProgress() >= 0 && hasPower() && redstoneCheckPassed;
  }

  @Override
  public float getProgress() {
    return currentTask == null ? -1 : currentTask.getProgress();
  }

  @Override
  @Nonnull
  public TileEntity getTileEntity() {
    return this;
  }

  @Override
  public void setProgress(float progress) {
    this.currentTask = progress < 0 ? null : new PoweredTaskProgress(progress);
  }

  public IPoweredTask getCurrentTask() {
    return currentTask;
  }

  public float getExperienceForOutput(@Nonnull ItemStack output) {
    if (lastCompletedRecipe == null) {
      return 0;
    }
    return lastCompletedRecipe.getExperienceForOutput(output);
  }

  public boolean getRedstoneChecksPassed() {
    return redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {

    if (!redstoneChecksPassed) {
      return false;
    }

    boolean requiresClientSync = false;
    // Process any current items
    requiresClientSync |= checkProgress(redstoneChecksPassed);

    if (currentTask != null || !hasPower() || !hasInputStacks()) {
      return requiresClientSync;
    }

    if (startFailed) {
      ticksSinceCheckedRecipe++;
      if (ticksSinceCheckedRecipe < 20) {
        return false;
      }
    }
    ticksSinceCheckedRecipe = 0;

    // Get a new chance when we don't have one yet
    // If a recipe could not be started we will try with the same chance next time
    if (theNextSeed == null) {
      theNextSeed = random.nextLong();
    }

    // Then see if we need to start a new one
    IMachineRecipe nextRecipe = canStartNextTask(theNextSeed);
    if (nextRecipe != null) {
      boolean started = startNextTask(nextRecipe, theNextSeed);
      if (started) {
        // this chance value has been used up
        theNextSeed = null;
      }
      startFailed = !started;
    } else {
      startFailed = true;
    }

    return requiresClientSync;
  }

  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (currentTask == null || !hasPower()) {
      return false;
    }
    if (redstoneChecksPassed && !currentTask.isComplete()) {
      getEnergy().useEnergy();
      if (shouldDoubleTick(currentTask, getEnergy().getMaxUsage())) {
        getEnergy().useEnergy();
      }
    }
    // then check if we are done
    if (currentTask.isComplete()) {
      taskComplete();
      return false;
    }

    return false;
  }

  protected void taskComplete() {
    if (currentTask != null) {
      lastCompletedRecipe = currentTask.getRecipe();
      ResultStack[] output = currentTask.getCompletedResult();
      if (output.length > 0) {
        mergeResults(output);
      }
    }
    damageCapacitor();
    markDirty();
    currentTask = null;
  }

  protected void mergeResults(@Nonnull ResultStack[] results) {
    EnderInventory.View outputSlots = getInventory().getView(EnderInventory.Type.OUTPUT);
    final int numOutputSlots = outputSlots.getSlots();
    if (numOutputSlots > 0) {

      NNList<ItemStack> outputStacks = new NNList<ItemStack>(numOutputSlots, ItemStack.EMPTY);
      for (int i = 0; i < numOutputSlots; i++) {
        ItemStack stack = outputSlots.getStackInSlot(i);
        if (!stack.isEmpty()) {
          stack = stack.copy();
        }
        outputStacks.add(stack);
      }

      for (ResultStack result : results) {
        if (Prep.isValid(result.item)) {
          int numMerged = mergeItemResult(result.item, outputStacks);
          if (numMerged > 0) {
            result.item.shrink(numMerged);
          }
        } else if (result.fluid != null) {
          mergeFluidResult(result);
        }
      }

      int listIndex = 0;
      for (int i = 0; i < numOutputSlots; i++) {
        ItemStack stack = outputStacks.get(listIndex);
        if (!stack.isEmpty()) {
          stack = stack.copy();
        }
        outputSlots.getSlot(i).set(stack);
        listIndex++;
      }

    } else {
      for (ResultStack result : results) {
        if (Prep.isValid(result.item)) {
          Block.spawnAsEntity(world, pos, result.item.copy());
          result.item.setCount(0);
        } else if (result.fluid != null) {
          mergeFluidResult(result);
        }
      }
    }
    cachedNextRecipe = null;
  }

  protected void mergeFluidResult(@Nonnull ResultStack result) {
  }

  protected void drainInputFluid(@Nonnull MachineRecipeInput fluid) {
  }

  protected boolean canInsertResultFluid(@Nonnull ResultStack fluid) {
    return false;
  }

  protected int mergeItemResult(@Nonnull ItemStack item, @Nonnull NNList<ItemStack> outputStacks) {

    ItemStack copy = item.copy();
    if (Prep.isInvalid(copy)) {
      return 0;
    }
    int firstFreeSlot = -1;

    // try to add it to existing stacks first
    for (int i = 0; i < outputStacks.size(); i++) {
      ItemStack outStack = outputStacks.get(i);
      if (Prep.isValid(outStack)) {
        int num = getNumCanMerge(outStack, copy);
        outStack.grow(num);
        copy.shrink(num);
        if (Prep.isInvalid(copy)) {
          return item.getCount();
        }
      } else if (firstFreeSlot < 0) {
        firstFreeSlot = i;
      }
    }

    // Try and add it to an empty slot
    if (firstFreeSlot >= 0) {
      outputStacks.set(firstFreeSlot, copy);
      return item.getCount();
    }

    return 0;
  }

  @Nonnull
  protected NNList<MachineRecipeInput> getRecipeInputs() {
    NNList<MachineRecipeInput> res = new NNList<>();
    EnderInventory.View inputSlots = getInventory().getView(EnderInventory.Type.INPUT);
    for (int slot = 0; slot < inputSlots.getSlots(); slot++) {
      final ItemStack item = inputSlots.getStackInSlot(slot);
      if (Prep.isValid(item)) {
        res.add(new MachineRecipeInput(slot, item));
      }
    }
    return res;
  }

  @Nullable
  protected IMachineRecipe getNextRecipe() {
    if (cachedNextRecipe == null) {
      cachedNextRecipe = MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(), getRecipeInputs());
    }
    return cachedNextRecipe;
  }

  @Nullable
  protected IMachineRecipe canStartNextTask(long nextSeed) {
    IMachineRecipe nextRecipe = getNextRecipe();
    if (nextRecipe == null) {
      return null; // no template
    }
    // make sure we have room for the next output
    return canInsertResult(nextSeed, nextRecipe) ? nextRecipe : null;
  }

  protected boolean canInsertResult(long nextSeed, @Nonnull IMachineRecipe nextRecipe) {
    final IPoweredTask task = createTask(nextRecipe, nextSeed);
    if (task == null) {
      return false;
    }
    ResultStack[] nextResults = task.getCompletedResult();
    NNList<ItemStack> outputStacks = null;

    EnderInventory.View outputSlots = getInventory().getView(EnderInventory.Type.OUTPUT);

    final int numOutputSlots = outputSlots.getSlots();
    if (numOutputSlots > 0) {

      outputStacks = new NNList<ItemStack>(numOutputSlots, ItemStack.EMPTY);
      boolean allFull = true;

      for (int i = 0; i < numOutputSlots; i++) {
        ItemStack stack = outputSlots.getStackInSlot(i);
        if (Prep.isValid(stack)) {
          stack = stack.copy();
          if (allFull && stack.getCount() < stack.getMaxStackSize()) {
            allFull = false;
          }
        } else {
          allFull = false;
        }
        outputStacks.add(stack);
      }
      if (allFull) {
        return false;
      }
    }

    for (ResultStack result : nextResults) {
      if (Prep.isValid(result.item)) {
        if (outputStacks == null || mergeItemResult(result.item, outputStacks) == 0) {
          return false;
        }
      } else if (result.fluid != null) {
        if (!canInsertResultFluid(result)) {
          return false;
        }
      }
    }

    return true;
  }

  protected boolean hasInputStacks() {
    EnderInventory.View inputSlots = getInventory().getView(EnderInventory.Type.INPUT);
    for (int i = 0; i < inputSlots.getSlots(); i++) {
      final ItemStack itemStack = inputSlots.getStackInSlot(i);
      if (Prep.isValid(itemStack)) {
        return true;
      }
    }
    return false;
  }

  protected int getNumCanMerge(@Nonnull ItemStack itemStack, @Nonnull ItemStack result) {
    if (!itemStack.isItemEqual(result) || !ItemStack.areItemStackTagsEqual(itemStack, result)) {
      return 0;
    }
    return Math.min(itemStack.getMaxStackSize() - itemStack.getCount(), result.getCount());
  }

  protected boolean startNextTask(@Nonnull IMachineRecipe nextRecipe, long nextSeed) {
    if (hasPower() && nextRecipe.isRecipe(getRecipeInputs())) {
      // then get our recipe and take away the source items
      currentTask = createTask(nextRecipe, nextSeed);
      List<MachineRecipeInput> consumed = nextRecipe.getQuantitiesConsumed(getRecipeInputs());
      for (MachineRecipeInput item : consumed) {
        if (item != null) {
          if (Prep.isValid(item.item)) {
            getInventory().getStackInSlot(item.slotNumber).shrink(item.item.getCount());
          } else if (item.fluid != null) {
            drainInputFluid(item);
          }

        }
      }
      return true;
    }
    return false;
  }

  @Nullable
  protected IPoweredTask createTask(@Nonnull IMachineRecipe nextRecipe, long nextSeed) {
    return new PoweredTask(nextRecipe, nextSeed, getRecipeInputs());
  }

  @Override
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    cachedNextRecipe = null;
  }

  // task machines need to return a valid constant from MachineRecipeRegistry
  @Override
  @Nonnull
  public abstract String getMachineName();

  protected boolean shouldDoubleTick(@Nonnull IPoweredTask task, int usedEnergy) {
    return false;
  }

}
