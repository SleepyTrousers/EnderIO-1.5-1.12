package crazypants.enderio.machine.slicensplice;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.alloy.VanillaSmeltingRecipe;
import crazypants.enderio.machine.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;

public class TileSliceAndSplice extends AbstractPoweredTaskEntity {

  public static final int POWER_PER_TICK_ONE = Config.sliceAndSpliceLevelOnePowerPerTickRF;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor(POWER_PER_TICK_ONE * 2,
      Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored(),
      POWER_PER_TICK_ONE);

  public static final int POWER_PER_TICK_TWO = Config.sliceAndSpliceLevelTwoPowerPerTickRF;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor(POWER_PER_TICK_TWO * 2,
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored(),
      POWER_PER_TICK_TWO);

  public static final int POWER_PER_TICK_THREE = Config.sliceAndSpliceLevelThreePowerPerTickRF;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor(POWER_PER_TICK_THREE * 2,
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored(),
      POWER_PER_TICK_THREE);

  private int axeIndex = 6;
  private int shearsIndex = 7;
  private EntityLivingBase fakePlayer;

  private ICapacitor capacitor;

  public TileSliceAndSplice() {
    super(new SlotDefinition(8, 1));
    capacitor = CAP_ONE;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockSliceAndSplice.unlocalisedName;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  public void setCapacitor(Capacitors capacitorType) {
    switch (capacitorType) {
    case BASIC_CAPACITOR:
      capacitor = CAP_ONE;
      break;
    case ACTIVATED_CAPACITOR:
      capacitor = CAP_TWO;
      break;
    case ENDER_CAPACITOR:
      capacitor = CAP_THREE;
      break;
    default:
      capacitor = CAP_ONE;
      break;
    }
    super.setCapacitor(capacitorType);
  }

  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
  }

  @Override
  public int getPowerUsePerTick() {
    if(capacitorType.ordinal() == 0) {
      return POWER_PER_TICK_ONE;
    } else if(capacitorType.ordinal() == 1) {
      return POWER_PER_TICK_TWO;
    }
    return POWER_PER_TICK_THREE;
  }

  @Override
  public int getProgressScaled(int scale) {
    int res = super.getProgressScaled(scale);
    if(currentTask != null) {
      res = Math.max(1, res);
    }
    return res;
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    if(getAxe() == null || getShears() == null) {
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
  protected void taskComplete() {
    super.taskComplete();
    damageTool(getAxe(), axeIndex);
    damageTool(getShears(), shearsIndex);
  }

  private void damageTool(ItemStack tool, int toolIndex) {
    if(tool != null && tool.isItemStackDamageable()) {
      tool.damageItem(1, getFakePlayer());
      if(tool.getItemDamage() >= tool.getMaxDamage()) {
        inventory[toolIndex] = null;
      }
    }
  }

  private EntityLivingBase getFakePlayer() {
    if(fakePlayer == null) {
      fakePlayer = FakePlayerFactory.getMinecraft(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId));
    }
    return fakePlayer;
  }

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
  protected boolean isMachineItemValidForSlot(int slot, ItemStack itemstack) {
    if(itemstack == null || itemstack.getItem() == null) {
      return false;
    }
    if(!slotDefinition.isInputSlot(slot)) {
      return false;
    }
    if(slot == axeIndex) {
      return itemstack.getItem() instanceof ItemAxe;
    }
    if(slot == shearsIndex) {
      return itemstack.getItem() instanceof ItemShears;
    }

    ItemStack currentStackInSlot = inventory[slot];
    if(currentStackInSlot != null) {
      return currentStackInSlot.isItemEqual(itemstack);
    }

    int numSlotsFilled = 0;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if(i >= 0 && i < inventory.length && i != axeIndex && i != shearsIndex) {
        if(inventory[i] != null && inventory[i].stackSize > 0) {
          numSlotsFilled++;
        }
      }
    }
    List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(slot, itemstack));
    if(numSlotsFilled == 0 && !recipes.isEmpty()) {
      return true;
    }
    return isValidInputForAlloyRecipe(slot, itemstack, numSlotsFilled, recipes);
  }

  private boolean isValidInputForAlloyRecipe(int slot, ItemStack itemstack, int numSlotsFilled, List<IMachineRecipe> recipes) {

    ItemStack[] resultInv = new ItemStack[slotDefinition.getNumInputSlots()];
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if(i >= 0 && i < inventory.length && i != axeIndex && i != shearsIndex) {
        if(i == slot) {
          resultInv[i] = itemstack;
        } else {
          resultInv[i] = inventory[i];
        }
      }
    }

    for (IMachineRecipe recipe : recipes) {
      if(recipe instanceof ManyToOneMachineRecipe) {
        if(((ManyToOneMachineRecipe) recipe).isValidRecipeComponents(resultInv)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isItemAlreadyInASlot(ItemStack itemstack) {
    ItemStack currentStackType = null;
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot() && currentStackType == null; i++) {
      currentStackType = inventory[i];
      if(currentStackType != null && currentStackType.isItemEqual(itemstack)) {
        return true;
      }
    }
    return false;
  }

}
