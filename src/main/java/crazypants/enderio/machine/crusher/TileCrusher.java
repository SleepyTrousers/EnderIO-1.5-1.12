package crazypants.enderio.machine.crusher;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.PoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import crazypants.enderio.network.PacketHandler;

public class TileCrusher extends AbstractPoweredTaskEntity {

    protected IGrindingMultiplier gb;
    protected int currGbUse = 0;
    protected int maxGbUse = 0;

    protected int lastSendGbScaled = 0;
    private boolean useGrindingBall;

    public TileCrusher() {
        super(new SlotDefinition(2, 4));
    }

    @Override
    public String getInventoryName() {
        return ModObject.blockSagMill.unlocalisedName;
    }

    @Override
    public String getMachineName() {
        return ModObject.blockSagMill.unlocalisedName;
    }

    @Override
    protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        return CrusherRecipeManager.instance.isValidInput(new MachineRecipeInput(i, itemstack));
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
            gb = CrusherRecipeManager.getInstance().getGrindballFromStack(inventory[1]);
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
            useGrindingBall = !CrusherRecipeManager.getInstance().isExcludedFromBallBonus(getRecipeInputs());
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
    public void readCustomNBT(NBTTagCompound nbtRoot) {
        super.readCustomNBT(nbtRoot);
        gb = GrindingMultiplierNBT.readFromNBT(nbtRoot);
        currGbUse = nbtRoot.getInteger("currGbUse");
        maxGbUse = (gb != null) ? gb.getDurationMJ() : 0;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
        super.writeCustomNBT(nbtRoot);
        if (gb != null) {
            GrindingMultiplierNBT.writeToNBT(gb, nbtRoot);
        }
        nbtRoot.setInteger("currGbUse", currGbUse);

        lastSendGbScaled = getBallDurationScaled(16);
    }

    @Override
    public boolean hasCustomInventoryName() {
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
