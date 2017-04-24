package crazypants.enderio.machine.slicensplice;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.recipe.IManyToOneRecipe;
import crazypants.enderio.machine.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import static crazypants.enderio.capacitor.CapacitorKey.SLICE_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.SLICE_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.SLICE_POWER_USE;
import static crazypants.enderio.config.Config.slicenspliceToolDamageChance;

@Storable
public class TileSliceAndSplice extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  protected final static int axeIndex = 6;
  protected final static int shearsIndex = 7;
  private EntityLivingBase fakePlayer;

  public TileSliceAndSplice() {
    super(new SlotDefinition(8, 1), SLICE_POWER_INTAKE, SLICE_POWER_BUFFER, SLICE_POWER_USE);
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockSliceAndSplice.getUnlocalisedName();
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    if (!hasTools()) {
      return null;
    }
    return super.canStartNextTask(chance);
  }

  private ItemStack getAxe() {
    return inventory[axeIndex];
  }

  private ItemStack getShears() {
    return inventory[shearsIndex];
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (!hasTools()) {
      return false;
    }
    return super.checkProgress(redstoneChecksPassed);
  }

  private boolean hasTools() {
    return Prep.isValid(getAxe()) && Prep.isValid(getShears());
  }

  @Override
  protected void taskComplete() {
    super.taskComplete();
    damageTool(getAxe(), axeIndex);
    damageTool(getShears(), shearsIndex);
  }

  @Override
  protected double usePower() {
    if (random.nextFloat() < slicenspliceToolDamageChance) {
      damageTool(getAxe(), axeIndex);
    }
    if (random.nextFloat() < slicenspliceToolDamageChance) {
      damageTool(getShears(), shearsIndex);
    }
    return super.usePower();
  }

  private void damageTool(ItemStack tool, int toolIndex) {
    if (Prep.isValid(tool) && tool.isItemStackDamageable()) {
      tool.damageItem(1, getFakePlayer());
      if (tool.getItemDamage() >= tool.getMaxDamage()) {
        inventory[toolIndex] = Prep.getEmpty();
      }
    }
  }

  private EntityLivingBase getFakePlayer() {
    if (fakePlayer == null) {
      fakePlayer = FakePlayerFactory.getMinecraft(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(worldObj.provider.getDimension()));
    }
    return fakePlayer;
  }

  @Override
  protected MachineRecipeInput[] getRecipeInputs() {
    MachineRecipeInput[] res = new MachineRecipeInput[slotDefinition.getNumInputSlots() - 2];
    int fromSlot = slotDefinition.minInputSlot;
    for (int i = 0; i < res.length; i++) {
      res[i] = new MachineRecipeInput(fromSlot, inventory[fromSlot]);
      fromSlot++;
    }
    return res;
  }

  @Override
  public boolean isMachineItemValidForSlot(int slot, ItemStack itemstack) {
    if (Prep.isInvalid(itemstack)) {
      return false;
    }
    if (!slotDefinition.isInputSlot(slot)) {
      return false;
    }
    if (slot == axeIndex) {
      return itemstack.getItem() instanceof ItemAxe;
    }
    if (slot == shearsIndex) {
      return itemstack.getItem() instanceof ItemShears;
    }

    ItemStack currentStackInSlot = inventory[slot];
    if (Prep.isValid(currentStackInSlot)) {
      return currentStackInSlot.isItemEqual(itemstack);
    }

    int numSlotsFilled = 0;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if (i >= 0 && i < inventory.length && i != axeIndex && i != shearsIndex) {
        if (Prep.isValid(inventory[i]) && inventory[i].stackSize > 0) {
          numSlotsFilled++;
        }
      }
    }
    List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(slot, itemstack));
    if (numSlotsFilled == 0 && !recipes.isEmpty()) {
      return true;
    }
    return isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
  }

  private boolean isValidInputForAlloyRecipe(int slot, ItemStack itemstack, int numSlotsFilled, List<IMachineRecipe> recipes) {

    ItemStack[] resultInv = new ItemStack[slotDefinition.getNumInputSlots()];
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if (i >= 0 && i < inventory.length && i != axeIndex && i != shearsIndex) {
        if (i == slot) {
          resultInv[i] = itemstack;
        } else {
          resultInv[i] = inventory[i];
        }
      }
    }

    for (IMachineRecipe recipe : recipes) {
      if (recipe instanceof ManyToOneMachineRecipe) {
        RECIPE: for (IManyToOneRecipe oneRecipe : ((ManyToOneMachineRecipe) recipe).getRecipesThatHaveTheseAsValidRecipeComponents(resultInv)) {
          for (int i = 0; i < resultInv.length; i++) {
            if (resultInv[i] != null) {
              for (RecipeInput ri : oneRecipe.getInputs()) {
                if (ri.getSlotNumber() == i && !ri.isInput(resultInv[i])) {
                  continue RECIPE;
                }
              }
            }
          }
          return true;
        }
      }
    }
    return false;
  }

}
