package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.alloysmelter.VanillaSmeltingRecipe;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.machines.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_USE;

@Storable
public class TileAlloySmelter extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  public static class Simple extends TileAlloySmelter {

    public Simple() {
      super(new SlotDefinition(3, 1, 0), SIMPLE_ALLOY_SMELTER_POWER_INTAKE, SIMPLE_ALLOY_SMELTER_POWER_BUFFER, SIMPLE_ALLOY_SMELTER_POWER_USE);
      setEnergyLoss(SIMPLE_ALLOY_SMELTER_POWER_LOSS);
      mode = Mode.ALLOY;
    }

    @Override
    public @Nonnull Mode getMode() {
      return Mode.ALLOY;
    }

  }

  public static class Enhanced extends TileAlloySmelter {
    public Enhanced() {
      super(new SlotDefinition(3, 1, 1), ENHANCED_ALLOY_SMELTER_POWER_INTAKE, ENHANCED_ALLOY_SMELTER_POWER_BUFFER, ENHANCED_ALLOY_SMELTER_POWER_USE);
    }
  }

  public static enum Mode {
    ALL,
    ALLOY,
    FURNACE;

    Mode next() {
      int nextOrd = ordinal() + 1;
      if (nextOrd >= values().length) {
        nextOrd = 0;
      }
      return values()[nextOrd];
    }

    Mode prev() {
      int nextOrd = ordinal() - 1;
      if (nextOrd < 0) {
        nextOrd = values().length - 1;
      }
      return values()[nextOrd];
    }
  }

  @Store
  protected @Nonnull Mode mode = Mode.ALL;

  public TileAlloySmelter() {
    this(new SlotDefinition(3, 1), ALLOY_SMELTER_POWER_INTAKE, ALLOY_SMELTER_POWER_BUFFER, ALLOY_SMELTER_POWER_USE);
  }

  protected TileAlloySmelter(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public @Nonnull Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    if (mode == null) {
      mode = Mode.ALL;
    }
    if (this.mode != mode) {
      this.mode = mode;
      updateClients = true;
    }
  }

  @Override
  protected IMachineRecipe canStartNextTask(long nextSeed) {
    if (getMode() == Mode.FURNACE) {
      VanillaSmeltingRecipe vr = AlloyRecipeManager.getInstance().getVanillaRecipe();
      if (vr.isRecipe(getRecipeInputs())) {
        final IPoweredTask task = createTask(vr, nextSeed);
        if (task == null) {
          return null;
        }
        IMachineRecipe.ResultStack[] res = task.getCompletedResult();
        if (res.length == 0) {
          return null;
        }
        return canInsertResult(nextSeed, vr) ? vr : null;
      }
      return null;
    }

    IMachineRecipe nextRecipe = getNextRecipe();
    if (getMode() == Mode.ALLOY && nextRecipe instanceof VanillaSmeltingRecipe) {
      nextRecipe = null;
    }
    if (nextRecipe == null) {
      return null; // no template
    }
    // make sure we have room for the next output
    return canInsertResult(nextSeed, nextRecipe) ? nextRecipe : null;
  }

  @Override
  public boolean isMachineItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    if (!slotDefinition.isInputSlot(slot)) {
      return false;
    }

    // We will assume anything that is in a slot is valid, so just return whether the new input can be stacked with the current one
    ItemStack currentStackInSlot = NullHelper.first(inventory[slot], Prep.getEmpty());
    if (Prep.isValid(currentStackInSlot)) {
      return currentStackInSlot.isItemEqual(itemstack);
    }

    int numSlotsFilled = 0;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if (i >= 0 && i < inventory.length) {
        if (inventory[i] != null && inventory[i].getCount() > 0) {
          numSlotsFilled++;
        }
      }
    }
    NNList<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(slot, itemstack));

    if (getMode() == Mode.FURNACE) {
      return isValidInputForFurnaceRecipe(itemstack, numSlotsFilled, recipes);
    } else if (getMode() == Mode.ALLOY) {
      return isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
    }
    return isValidInputForFurnaceRecipe(itemstack, numSlotsFilled, recipes) || isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
  }

  private boolean isValidInputForAlloyRecipe(int slot, @Nonnull ItemStack itemstack, int numSlotsFilled, NNList<IMachineRecipe> recipes) {
    if (numSlotsFilled == 0) {
      return containsAlloyRecipe(recipes);
    }
    for (IMachineRecipe recipe : recipes) {
      if (!(recipe instanceof VanillaSmeltingRecipe)) {

        if (recipe instanceof ManyToOneMachineRecipe) {
          ItemStack[] resultInv = new ItemStack[slotDefinition.getNumInputSlots()];
          for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
            if (i >= 0 && i < inventory.length) {
              if (i == slot) {
                resultInv[i] = itemstack;
              } else {
                resultInv[i] = inventory[i];
              }
            }
          }
          if (((ManyToOneMachineRecipe) recipe).isValidRecipeComponents(resultInv)) {
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

  private boolean isValidInputForFurnaceRecipe(@Nonnull ItemStack itemstack, int numSlotsFilled, NNList<IMachineRecipe> recipes) {
    if (numSlotsFilled == 0) {
      return containsFurnaceRecipe(recipes);
    }
    return containsFurnaceRecipe(recipes) && isItemAlreadyInASlot(itemstack);
  }

  private boolean isItemAlreadyInASlot(@Nonnull ItemStack itemstack) {
    ItemStack currentStackType = null;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot() && currentStackType == null; i++) {
      currentStackType = inventory[i];
      if (currentStackType != null && currentStackType.isItemEqual(itemstack)) {
        return true;
      }
    }
    return false;
  }

  private boolean containsFurnaceRecipe(NNList<IMachineRecipe> recipes) {
    for (IMachineRecipe rec : recipes) {
      if (rec instanceof VanillaSmeltingRecipe) {
        return true;
      }
    }
    return false;
  }

  private boolean containsAlloyRecipe(NNList<IMachineRecipe> recipes) {
    for (IMachineRecipe rec : recipes) {
      if (!(rec instanceof VanillaSmeltingRecipe)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.ALLOYSMELTER;
  }

}
