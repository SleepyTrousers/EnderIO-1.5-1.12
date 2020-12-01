package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineLevel;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.machines.config.config.AlloySmelterConfig;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import static crazypants.enderio.machines.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ALLOY_SMELTER_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_DOUBLE_OP_CHANCE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_POWER_EFFICIENCY;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_ALLOY_SMELTER_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_ALLOY_SMELTER_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_STIRLING_POWER_LOSS;

@Storable
public class TileAlloySmelter extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  public static class Simple extends TileAlloySmelter {

    public Simple() {
      super(AlloySmelterConfig.profileSimpleAlloy.get().get(), new SlotDefinition(3, 1, 0), SIMPLE_ALLOY_SMELTER_POWER_INTAKE,
          SIMPLE_ALLOY_SMELTER_POWER_BUFFER, SIMPLE_ALLOY_SMELTER_POWER_USE);
      setEnergyLoss(SIMPLE_ALLOY_SMELTER_POWER_LOSS);
    }

    @Override
    protected @Nonnull RecipeLevel getMachineLevel() {
      return MachineLevel.SIMPLE;
    }

  }

  public static class Furnace extends TileAlloySmelter {

    public Furnace() {
      super(AlloySmelterConfig.profileSimpleFurnace.get().get(), new SlotDefinition(3, 1, 0), SIMPLE_ALLOY_SMELTER_POWER_INTAKE,
          SIMPLE_ALLOY_SMELTER_POWER_BUFFER, SIMPLE_ALLOY_SMELTER_POWER_USE);
      setEnergyLoss(SIMPLE_STIRLING_POWER_LOSS);
    }

    @Override
    protected @Nonnull RecipeLevel getMachineLevel() {
      return MachineLevel.SIMPLE;
    }

  }

  public static class Enhanced extends TileAlloySmelter {
    public Enhanced() {
      super(AlloySmelterConfig.profileEnhancedAlloy.get().get(), new SlotDefinition(3, 1, 1), ENHANCED_ALLOY_SMELTER_POWER_INTAKE,
          ENHANCED_ALLOY_SMELTER_POWER_BUFFER, ENHANCED_ALLOY_SMELTER_POWER_USE);
      setEfficiencyMultiplier(ENHANCED_ALLOY_SMELTER_POWER_EFFICIENCY);
    }

    @Nonnull
    @Override
    public ICapacitorData getCapacitorData() {
      return CapacitorHelper.increaseCapacitorLevel(super.getCapacitorData(), 1f);
    }

    @Override
    protected boolean shouldDoubleTick(@Nonnull IPoweredTask task, int usedEnergy) {
      double chance = getCapacitorData().getUnscaledValue(ENHANCED_ALLOY_SMELTER_DOUBLE_OP_CHANCE) * (usedEnergy / task.getRequiredEnergy());
      if (random.nextDouble() < chance) {
        return true;
      }
      return super.shouldDoubleTick(task, usedEnergy);
    }

    @Override
    public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode modeIn) {
      return (faceHit != EnumFacing.UP || modeIn == IoMode.NONE) && super.supportsMode(faceHit, modeIn);
    }

    @Override
    protected @Nonnull RecipeLevel getMachineLevel() {
      return MachineLevel.ADVANCED;
    }

  }

  protected final @Nonnull OperatingProfile operatingProfile;

  @Store
  protected @Nonnull OperatingMode mode = OperatingMode.ALL;

  public TileAlloySmelter() {
    this(AlloySmelterConfig.profileNormal.get().get(), new SlotDefinition(3, 1), ALLOY_SMELTER_POWER_INTAKE, ALLOY_SMELTER_POWER_BUFFER,
        ALLOY_SMELTER_POWER_USE);
  }

  protected TileAlloySmelter(@Nonnull OperatingProfile operatingProfile, @Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    this.operatingProfile = operatingProfile;
  }

  @Override
  protected @Nonnull RecipeLevel getMachineLevel() {
    return MachineLevel.NORMAL;
  }

  public @Nonnull OperatingProfile getOperatingProfile() {
    return operatingProfile;
  }

  public @Nonnull OperatingMode getMode() {
    return operatingProfile.canSwitchProfiles() ? mode : operatingProfile.getOperatingMode();
  }

  public void setMode(@Nonnull OperatingMode mode) {
    if (this.mode != mode) {
      this.mode = mode;
      updateClients = true;
    }
  }

  @Override
  protected IMachineRecipe canStartNextTask(long nextSeed) {
    IMachineRecipe nextRecipe = null;
    if (getMode().doAlloyRecipes() && AlloyRecipeManager.getInstance().getRecipeForInputs(getMachineLevel(), getRecipeInputs()) != null) {
      nextRecipe = AlloyRecipeManager.getInstance();
    }
    if (nextRecipe == null && getMode().doFurnaceRecipes() && AlloyRecipeManager.getVanillaRecipe().isRecipe(getMachineLevel(), getRecipeInputs())) {
      nextRecipe = AlloyRecipeManager.getVanillaRecipe();
    }
    return nextRecipe != null && canInsertResult(nextSeed, nextRecipe) ? nextRecipe : null;
  }

  @Override
  public boolean isMachineItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    if (!slotDefinition.isInputSlot(slot)) {
      return false;
    }

    // We will assume anything that is in a slot is valid, so just return if the new input can be stacked with the current one
    if (getStackInSlot(slot).isItemEqual(itemstack)) {
      return true;
    }

    if (getMode().doAlloyRecipes()) {
      NNList<ItemStack> input = new NNList<>();
      for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
        input.add(i == slot ? itemstack : inventory[i]);
      }
      if (AlloyRecipeManager.getInstance().isValidRecipeComponents(getMachineLevel(), input)) {
        return true;
      }
    }

    if (getMode().doFurnaceRecipes()) {
      for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
        if (i != slot && Prep.isValid(getStackInSlot(i)) && !getStackInSlot(i).isItemEqual(itemstack)) {
          return false;
        }
      }
      if (AlloyRecipeManager.getVanillaRecipe().isValidInput(getMachineLevel(), itemstack)) {
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
