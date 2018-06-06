package crazypants.enderio.machines.machine.sagmill;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.task.PoweredTask;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.sagmill.IGrindingMultiplier;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_SAG_MILL_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_SAG_MILL_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_SAG_MILL_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SAG_MILL_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SAG_MILL_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SAG_MILL_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_SAG_MILL_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_SAG_MILL_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_SAG_MILL_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_SAG_MILL_POWER_USE;

@Storable
public abstract class TileSagMill extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  @Storable
  public static class Simple extends TileSagMill {
    public Simple() {
      super(new SlotDefinition(2, 4, 0), SIMPLE_SAG_MILL_POWER_INTAKE, SIMPLE_SAG_MILL_POWER_BUFFER, SIMPLE_SAG_MILL_POWER_USE);
      setEnergyLoss(SIMPLE_SAG_MILL_POWER_LOSS);
    }

    @Override
    public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
      // Reject everything for the invisible ball slot
      return i != 1 && super.isMachineItemValidForSlot(i, itemstack);
    }
  }

  @Storable
  public static class Normal extends TileSagMill {
    public Normal() {
      super(new SlotDefinition(2, 4), SAG_MILL_POWER_INTAKE, SAG_MILL_POWER_BUFFER, SAG_MILL_POWER_USE);
    }
  }

  @Storable
  public static class Enhanced extends TileSagMill {
    public Enhanced() {
      super(new SlotDefinition(2, 4), ENHANCED_SAG_MILL_POWER_INTAKE, ENHANCED_SAG_MILL_POWER_BUFFER, ENHANCED_SAG_MILL_POWER_USE);
    }

    @Override
    protected boolean shouldDoubleTick(@Nonnull IPoweredTask task, int usedEnergy) {
      double chance = 3 * (usedEnergy / task.getRequiredEnergy());
      if (random.nextDouble() < chance) {
        return true;
      }
      return super.shouldDoubleTick(task, usedEnergy);
    }

    @Override
    public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
      return (faceHit != EnumFacing.UP || mode == IoMode.NONE) && super.supportsMode(faceHit, mode);
    }

  }

  @Store
  protected IGrindingMultiplier grindingBall;
  @Store
  protected int grindingBallDurabilityUsed = 0;
  @Store
  protected int grindingBallDurabilityMax = 0;
  @Store
  protected int lastSendGbScaled = 0;

  protected TileSagMill(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.SAGMILL;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return Prep.isValid(itemstack) && SagMillRecipeManager.getInstance().isValidInput(new MachineRecipeInput(i, itemstack));
  }

  public int getBallDurationScaled(int scale) {
    return MathHelper
        .clamp((grindingBallDurabilityMax > 0) ? (int) (scale * (1 - ((float) grindingBallDurabilityUsed / (float) grindingBallDurabilityMax))) : 0, 0, scale);
  }

  @Override
  protected int usePower(int wantToUse) {
    int res = super.usePower(wantToUse);

    if (currentTask != null && currentTask.getBonusType().useBalls()) {
      boolean sendGB = false;
      if (grindingBall != null) {
        grindingBallDurabilityUsed += res;
        if (grindingBallDurabilityUsed >= grindingBallDurabilityMax) {
          grindingBallDurabilityUsed -= grindingBallDurabilityMax;
          grindingBallDurabilityMax = 0;
          grindingBall = null;
          sendGB = true;
        } else {
          int newScaled = getBallDurationScaled(16);
          if (newScaled != lastSendGbScaled) {
            sendGB = true;
          }
        }
      }
      if (grindingBall == null) {
        final ItemStack ballInSlot = getStackInSlot(1);
        grindingBall = SagMillRecipeManager.getInstance().getGrindballFromStack(ballInSlot);
        if (grindingBall != null) {
          grindingBallDurabilityMax = grindingBall.getDurability();
          ballInSlot.shrink(1);
          markDirty();
          sendGB = false; // the tile update will also sync the grinding ball
        }
      }
      if (sendGB) {
        PacketHandler.sendToAllAround(new PacketGrindingBall(this), this);
        lastSendGbScaled = getBallDurationScaled(16);
      }
    }
    return res;
  }

  @Override
  protected IPoweredTask createTask(@Nonnull IMachineRecipe nextRecipe, long nextSeed) {
    PoweredTask res;
    if (grindingBall != null && nextRecipe.getBonusType(getRecipeInputs()).doChances()) {
      res = new PoweredTask(nextRecipe, nextSeed, grindingBall.getGrindingMultiplier(), grindingBall.getChanceMultiplier(), getRecipeInputs());
      res.setRequiredEnergy(res.getRequiredEnergy() * grindingBall.getPowerMultiplier());
    } else {
      res = new PoweredTask(nextRecipe, nextSeed, getRecipeInputs());
    }
    return res;
  }

  @Override
  public ResourceLocation getSound() {
    return new ResourceLocation(EnderIO.DOMAIN, "machine.sagmill");
  }

  @Override
  public float getVolume() {
    return super.getVolume() * 0.125f;
  }

}
