package crazypants.enderio.machines.machine.sagmill;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.task.PoweredTask;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.sagmill.IGrindingMultiplier;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.machines.capacitor.CapacitorKey.SAG_MILL_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SAG_MILL_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SAG_MILL_POWER_USE;

@Storable
public class TileSagMill extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  @Store
  protected IGrindingMultiplier gb;
  @Store
  protected int currGbUse = 0;
  @Store
  protected int maxGbUse = 0;
  @Store
  protected int lastSendGbScaled = 0;
  private boolean useGrindingBall;

  public TileSagMill() {
    super(new SlotDefinition(2, 4), SAG_MILL_POWER_INTAKE, SAG_MILL_POWER_BUFFER, SAG_MILL_POWER_USE);
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
    return (maxGbUse > 0) ? (int) (scale * (1 - ((float) currGbUse / (float) maxGbUse))) : 0;
  }

  @Override
  protected int usePower(int wantToUse) {
    int res = super.usePower(wantToUse);
    boolean sendGB = false;

    if (gb != null && useGrindingBall) {
      currGbUse += res;

      if (currGbUse >= gb.getDurationMJ()) {
        currGbUse = 0;
        maxGbUse = 0;
        gb = null;
        sendGB = true;
      } else {
        int newScaled = getBallDurationScaled(16);
        if (newScaled != lastSendGbScaled) {
          sendGB = true;
        }
      }
    }
    if (gb == null) {
      gb = SagMillRecipeManager.getInstance().getGrindballFromStack(getStackInSlot(1));
      if (gb != null) {
        maxGbUse = gb.getDurationMJ();
        decrStackSize(1, 1);
        markDirty();
        sendGB = false; // the tile update will also sync the grinding ball
        lastSendGbScaled = getBallDurationScaled(16);
      }
    }
    if (sendGB) {
      PacketHandler.sendToAllAround(new PacketGrindingBall(this), this);
      lastSendGbScaled = getBallDurationScaled(16);
    }
    return res;
  }

  @Override
  protected void taskComplete() {
    IPoweredTask ct = currentTask;
    super.taskComplete();
    // run it again if the ball says so
    if (gb != null && useGrindingBall && ct != null) {
      if (ct.getBonusType() == RecipeBonusType.MULTIPLY_OUTPUT) {
        float chance = random.nextFloat();
        float mul = gb.getGrindingMultiplier() - 1;
        while (mul > 0) {
          if (chance <= mul) {
            currentTask = ct;
            super.taskComplete();
          }
          mul--;
        }
      }
    }
  }

  @Override
  protected IPoweredTask createTask(@Nonnull IMachineRecipe nextRecipe, float chance) {
    PoweredTask res;
    useGrindingBall = false;
    if (gb != null) {
      useGrindingBall = !SagMillRecipeManager.getInstance().isExcludedFromBallBonus(getRecipeInputs());
      if (useGrindingBall) {
        res = new PoweredTask(nextRecipe, chance / gb.getChanceMultiplier(), getRecipeInputs());
        res.setRequiredEnergy(res.getRequiredEnergy() * gb.getPowerMultiplier());
      } else {
        res = new PoweredTask(nextRecipe, chance, getRecipeInputs());
      }
    } else {
      res = new PoweredTask(nextRecipe, chance, getRecipeInputs());
    }
    return res;
  }

  @Override
  public @Nonnull String getSoundName() {
    return "machine.sagmill";
  }

  @Override
  public float getVolume() {
    return super.getVolume() * 0.125f;
  }

}
