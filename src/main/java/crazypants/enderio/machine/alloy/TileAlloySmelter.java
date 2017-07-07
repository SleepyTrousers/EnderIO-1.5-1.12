package crazypants.enderio.machine.alloy;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.Log;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.recipe.alloysmelter.VanillaSmeltingRecipe;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.capacitor.CapacitorKey.LEGACY_ENERGY_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.LEGACY_ENERGY_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.LEGACY_ENERGY_USE;

@Storable
public class TileAlloySmelter extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

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
  private Mode mode;

  public TileAlloySmelter() {
    super(new SlotDefinition(3, 1), LEGACY_ENERGY_INTAKE, LEGACY_ENERGY_BUFFER, LEGACY_ENERGY_USE);
    mode = Mode.ALL;
  }

  protected TileAlloySmelter(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    if (mode == null) {
      mode = Mode.ALL;
    }
    if (this.mode != mode) {
      this.mode = mode;
      forceClientUpdate.set();
    }
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    if (mode == Mode.FURNACE) {
      VanillaSmeltingRecipe vr = AlloyRecipeManager.getInstance().getVanillaRecipe();
      if (vr.isRecipe(getRecipeInputs())) {
        IMachineRecipe.ResultStack[] res = vr.getCompletedResult(chance, getRecipeInputs());
        if (res.length == 0) {
          return null;
        }
        return canInsertResult(chance, vr) ? vr : null;
      }
      return null;
    }

    IMachineRecipe nextRecipe = getNextRecipe();
    if (mode == Mode.ALLOY && nextRecipe instanceof VanillaSmeltingRecipe) {
      nextRecipe = null;
    }
    if (nextRecipe == null) {
      return null; // no template
    }
    // make sure we have room for the next output
    return canInsertResult(chance, nextRecipe) ? nextRecipe : null;
  }

  @Override
  public boolean isMachineItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    if (!slotDefinition.isInputSlot(slot)) {
      return false;
    }

    // We will assume anything that is in a slot is valid, so just return whether the new input can be stacked with the current one
    ItemStack currentStackInSlot = inventory[slot];
    if (currentStackInSlot != null) {
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

    if (mode == Mode.FURNACE) {
      return isValidInputForFurnaceRecipe(itemstack, numSlotsFilled, recipes);
    } else if (mode == Mode.ALLOY) {
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

}
