package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

@Storable
public abstract class AbstractPoweredTaskEntity extends AbstractPowerConsumerEntity implements IProgressTile {

  @Store({ StoreFor.SAVE, StoreFor.ITEM })
  protected IPoweredTask currentTask = null;
  @Store({ StoreFor.SAVE, StoreFor.ITEM })
  protected IMachineRecipe lastCompletedRecipe;
  protected IMachineRecipe cachedNextRecipe;

  protected final Random random = new Random();

  protected int ticksSinceCheckedRecipe = 0;
  protected boolean startFailed = false;
  protected float nextChance = Float.NaN;

  @Deprecated
  protected AbstractPoweredTaskEntity(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }
  
  protected AbstractPoweredTaskEntity(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractPoweredTaskEntity(SlotDefinition slotDefinition, ModObject modObject) {
    super(slotDefinition, modObject);
  }

  @Override
  public @Nonnull int[] getSlotsForFace(EnumFacing dir) {
    IoMode mode = dir == null ? IoMode.DISABLED : getIoMode(dir);
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
  public boolean isActive() {
    return currentTask == null ? false : currentTask.getProgress() >= 0 && hasPower() && redstoneCheckPassed;
  }

  @Override
  public float getProgress() {
    return currentTask == null ? -1 : currentTask.getProgress();
  }

  @Override
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

  public float getExperienceForOutput(ItemStack output) {
    if(lastCompletedRecipe == null) {
      return 0;
    }
    return lastCompletedRecipe.getExperienceForOutput(output);
  }

  public boolean getRedstoneChecksPassed() {
    return redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {

    if(!redstoneChecksPassed) {
      return false;
    }

    boolean requiresClientSync = false;
    // Process any current items
    requiresClientSync |= checkProgress(redstoneChecksPassed);

    if(currentTask != null || !hasPower() || !hasInputStacks()) {
      return requiresClientSync;
    }

    if(startFailed) {
      ticksSinceCheckedRecipe++;
      if(ticksSinceCheckedRecipe < 20) {
        return false;
      }
    }
    ticksSinceCheckedRecipe = 0;

    // Get a new chance when we don't have one yet
    // If a recipe could not be started we will try with the same chance next time
    if(Float.isNaN(nextChance)) {
      nextChance = random.nextFloat();
    }

    // Then see if we need to start a new one
    IMachineRecipe nextRecipe = canStartNextTask(nextChance);
    if(nextRecipe != null) {
      boolean started = startNextTask(nextRecipe, nextChance);
      if(started) {
        // this chance value has been used up
        nextChance = Float.NaN;
      }
      startFailed = !started;
    } else {
      startFailed = true;
    }
    sendTaskProgressPacket();

    return requiresClientSync;
  }

  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if(currentTask == null || !hasPower()) {
      return false;
    }
    if (redstoneChecksPassed && !currentTask.isComplete()) {
      usePower();
    }
    // then check if we are done
    if(currentTask.isComplete()) {
      taskComplete();
      return false;
    }

    return false;
  }

  protected double usePower() {
    return usePower(getPowerUsePerTick());
  }
  
  @Override
  public int getEnergyStored() {
    return getEnergyStored(null);
  }

  public int usePower(int wantToUse) {
    int used = Math.min(getEnergyStored(), wantToUse);
    setEnergyStored(Math.max(0, getEnergyStored() - used));
    if(currentTask != null) {
      currentTask.update(used);
    }
    return used;
  }

  protected void taskComplete() {
    if(currentTask != null) {
      lastCompletedRecipe = currentTask.getRecipe();
      ResultStack[] output = currentTask.getCompletedResult();
      if(output != null && output.length > 0) {
        mergeResults(output);
      }
    }
    markDirty();
    currentTask = null;
    lastProgressScaled = 0;
  }

  protected void mergeResults(ResultStack[] results) {
    final int numOutputSlots = slotDefinition.getNumOutputSlots();
    if(numOutputSlots > 0) {

      List<ItemStack> outputStacks = new ArrayList<ItemStack>(numOutputSlots);
      for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
        ItemStack it = inventory[i];
        if (Prep.isValid(it)) {
          it = it.copy();
        }
        outputStacks.add(it);
      }

      for (ResultStack result : results) {
        if (Prep.isValid(result.item)) {
          int numMerged = mergeItemResult(result.item, outputStacks);
          if (numMerged > 0) {
            result.item.stackSize -= numMerged;
          }
        } else if (result.fluid != null) {
          mergeFluidResult(result);
        }
      }

      int listIndex = 0;
      for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
        ItemStack st = outputStacks.get(listIndex);
        if (Prep.isValid(st)) {
          st = st.copy();
        }
        inventory[i] = st;
        listIndex++;
      }

    }
    cachedNextRecipe = null;
  }

  protected void mergeFluidResult(ResultStack result) {
  }

  protected void drainInputFluid(MachineRecipeInput fluid) {
  }

  protected boolean canInsertResultFluid(ResultStack fluid) {
    return false;
  }

  protected int mergeItemResult(ItemStack item, List<ItemStack> outputStacks) {

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
        outStack.stackSize += num;
        copy.stackSize -= num;
        if (copy.stackSize <= 0) {
          return item.stackSize;
        }
      } else if (firstFreeSlot < 0) {
        firstFreeSlot = i;
      }
    }

    // Try and add it to an empty slot
    if (firstFreeSlot >= 0) {
      outputStacks.set(firstFreeSlot, copy);
      return item.stackSize;
    }

    return 0;
  }

  protected MachineRecipeInput[] getRecipeInputs() {
    MachineRecipeInput[] res = new MachineRecipeInput[slotDefinition.getNumInputSlots()];
    int fromSlot = slotDefinition.minInputSlot;
    for (int i = 0; i < res.length; i++) {
      res[i] = new MachineRecipeInput(fromSlot, inventory[fromSlot]);
      fromSlot++;
    }
    return res;
  }

  protected IMachineRecipe getNextRecipe() {
    if (cachedNextRecipe == null) {
      cachedNextRecipe = MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(), getRecipeInputs());
    }
    return cachedNextRecipe;
  }

  protected IMachineRecipe canStartNextTask(float chance) {
    IMachineRecipe nextRecipe = getNextRecipe();
    if(nextRecipe == null) {
      return null; // no template
    }
    // make sure we have room for the next output
    return canInsertResult(chance, nextRecipe) ? nextRecipe : null;
  }

  protected boolean canInsertResult(float chance, IMachineRecipe nextRecipe) {

    final int numOutputSlots = slotDefinition.getNumOutputSlots();
    if (numOutputSlots <= 0) {
      return false;
    }

    ResultStack[] nextResults = nextRecipe.getCompletedResult(chance, getRecipeInputs());
    List<ItemStack> outputStacks = new ArrayList<ItemStack>(numOutputSlots);
    boolean allFull = true;

    for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
      ItemStack st = inventory[i];
      if (Prep.isValid(st)) {
        st = st.copy();
        if (allFull && st.stackSize < st.getMaxStackSize()) {
          allFull = false;
        }
      } else {
        allFull = false;
      }
      outputStacks.add(st);
    }
    if (allFull) {
      return false;
    }

    for (ResultStack result : nextResults) {
      if (Prep.isValid(result.item)) {
        if (mergeItemResult(result.item, outputStacks) == 0) {
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
    if(hasPower() && nextRecipe.isRecipe(getRecipeInputs())) {
      // then get our recipe and take away the source items
      currentTask = createTask(nextRecipe, chance);
      List<MachineRecipeInput> consumed = nextRecipe.getQuantitiesConsumed(getRecipeInputs());
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

  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return new PoweredTask(nextRecipe, chance, getRecipeInputs());
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    cachedNextRecipe = null;
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack res = super.decrStackSize(fromSlot, amount);
    if(slotDefinition.isInputSlot(fromSlot)) {
      cachedNextRecipe = null;
    }
    return res;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nullable ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if(slotDefinition.isInputSlot(slot)) {
      cachedNextRecipe = null;
    }
  }
  
  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }

}
