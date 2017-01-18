package crazypants.enderio.machine.alloy;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.paint.IPaintable;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_USE;

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
    super(new SlotDefinition(3, 1), ALLOY_SMELTER_POWER_INTAKE, ALLOY_SMELTER_POWER_BUFFER, ALLOY_SMELTER_POWER_USE);
    mode = Mode.ALL;
  }

  protected TileAlloySmelter(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  public @Nonnull String getName() {
    return "Alloy Smelter";
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
      VanillaSmeltingRecipe vr = AlloyRecipeManager.getInstance().vanillaRecipe;
      if (vr.isRecipe(getRecipeInputs())) {
        ResultStack[] res = vr.getCompletedResult(chance, getRecipeInputs());
        if (res == null || res.length == 0) {
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
  public @Nonnull String getMachineName() {
    return ModObject.blockAlloySmelter.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int slot, ItemStack itemstack) {
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
        if (inventory[i] != null && inventory[i].stackSize > 0) {
          numSlotsFilled++;
        }
      }
    }
    List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(slot, itemstack));

    if (mode == Mode.FURNACE) {
      return isValidInputForFurnaceRecipe(itemstack, numSlotsFilled, recipes);
    } else if (mode == Mode.ALLOY) {
      return isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
    }
    return isValidInputForFurnaceRecipe(itemstack, numSlotsFilled, recipes) || isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
  }

  private boolean isValidInputForAlloyRecipe(int slot, ItemStack itemstack, int numSlotsFilled, List<IMachineRecipe> recipes) {
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

  private boolean isValidInputForFurnaceRecipe(ItemStack itemstack, int numSlotsFilled, List<IMachineRecipe> recipes) {
    if (numSlotsFilled == 0) {
      return containsFurnaceRecipe(recipes);
    }
    return containsFurnaceRecipe(recipes) && isItemAlreadyInASlot(itemstack);
  }

  private boolean isItemAlreadyInASlot(ItemStack itemstack) {
    ItemStack currentStackType = null;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot() && currentStackType == null; i++) {
      currentStackType = inventory[i];
      if (currentStackType != null && currentStackType.isItemEqual(itemstack)) {
        return true;
      }
    }
    return false;
  }

  private boolean containsFurnaceRecipe(List<IMachineRecipe> recipes) {
    for (IMachineRecipe rec : recipes) {
      if (rec instanceof VanillaSmeltingRecipe) {
        return true;
      }
    }
    return false;
  }

  private boolean containsAlloyRecipe(List<IMachineRecipe> recipes) {
    for (IMachineRecipe rec : recipes) {
      if (!(rec instanceof VanillaSmeltingRecipe)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

}
