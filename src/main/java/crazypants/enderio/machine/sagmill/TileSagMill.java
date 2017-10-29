package crazypants.enderio.machine.sagmill;

import javax.annotation.Nonnull;

import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machine.interfaces.IPoweredTask;
import crazypants.enderio.machine.task.PoweredTask;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.RecipeBonusType;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@Storable
public class TileSagMill extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  @Store
  protected IGrindingMultiplier gb;
  @Store
  protected int currGbUse = 0;
  @Store
  protected int maxGbUse = 0;

  protected int lastSendGbScaled = 0;
  private boolean useGrindingBall;

  public TileSagMill() {
    super(new SlotDefinition(2, 4), CapacitorKey.SAG_MILL_POWER_INTAKE, CapacitorKey.SAG_MILL_POWER_BUFFER, CapacitorKey.SAG_MILL_POWER_USE);
  }

  @Override
  public @Nonnull String getName() {
    return MachineObject.blockSagMill.getUnlocalisedName();
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.blockSagMill.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (itemStack.isEmpty()) {
      return false;
    }
    return SagMillRecipeManager.instance.isValidInput(new MachineRecipeInput(i, itemstack));
  }

  public int getBallDurationScaled(int scale) {
    return (maxGbUse > 0) ? (int) (scale * (1 - ((float) currGbUse / (float) maxGbUse))) : 0;
  }

  @Override
  protected double usePower() {
    double res = super.usePower();
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
      gb = SagMillRecipeManager.getInstance().getGrindballFromStack(inventory[1]);
      if (gb != null) {
        maxGbUse = gb.getDurationMJ();
        decrStackSize(1, 1);
        markDirty();
        sendGB = false; // the tile update will also sync the grinding ball
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
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
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
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    lastSendGbScaled = getBallDurationScaled(16);
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public String getSoundName() {
    return "machine.sagmill";
  }

  @Override
  public float getVolume() {
    return super.getVolume() * 0.125f;
  }

}
