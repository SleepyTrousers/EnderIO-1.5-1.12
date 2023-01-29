package crazypants.enderio.machine.painter;

import static crazypants.enderio.machine.MachineRecipeInput.getInputForSlot;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.Util;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.RecipeBonusType;

public abstract class BasicPainterTemplate implements IMachineRecipe {

    public static int DEFAULT_ENERGY_PER_TASK = Config.painterEnergyPerTaskRF;

    protected final Block[] validTargets;

    protected BasicPainterTemplate(Block... validTargetBlocks) {
        validTargets = validTargetBlocks;
    }

    @Override
    public int getEnergyRequired(MachineRecipeInput... inputs) {
        return DEFAULT_ENERGY_PER_TASK;
    }

    @Override
    public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
        return RecipeBonusType.NONE;
    }

    @Override
    public boolean isRecipe(MachineRecipeInput... inputs) {
        return isValidTarget(getTarget(inputs)) && isValidPaintSource(getPaintSource(inputs));
    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
        ItemStack target = getTarget(inputs);
        ItemStack paintSource = getPaintSource(inputs);
        if (target == null || paintSource == null) {
            return null;
        }
        ItemStack result = new ItemStack(getResultId(target), 1, target.getItemDamage());
        PainterUtil.setSourceBlock(result, Util.getBlockFromItemId(paintSource), paintSource.getItemDamage());
        return new ResultStack[] { new ResultStack(result) };
    }

    public ItemStack getTarget(MachineRecipeInput... inputs) {
        return getInputForSlot(0, inputs);
    }

    public ItemStack getPaintSource(MachineRecipeInput... inputs) {
        return getInputForSlot(1, inputs);
    }

    @Override
    public boolean isValidInput(MachineRecipeInput input) {
        if (input == null) {
            return false;
        }
        if (input.slotNumber == 0) {
            return isValidTarget(input.item);
        }
        if (input.slotNumber == 1) {
            return isValidPaintSource(input.item);
        }
        return false;
    }

    @Override
    public String getMachineName() {
        return ModObject.blockPainter.unlocalisedName;
    }

    public boolean isValidPaintSource(ItemStack paintSource) {
        return PaintSourceValidator.instance.isValidSourceDefault(paintSource);
    }

    public boolean isValidTarget(ItemStack target) {
        // first check for exact matches, then check for item blocks
        if (target == null) {
            return false;
        }

        Block blk = Block.getBlockFromItem(target.getItem());
        if (blk == null) {
            return false;
        }

        for (int i = 0; i < validTargets.length; i++) {
            if (validTargets[i] == blk) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getUid() {
        return getClass().getCanonicalName();
    }

    protected Item getResultId(ItemStack target) {
        return target.getItem();
    }

    public int getQuantityConsumed(MachineRecipeInput input) {
        return input.slotNumber == 0 ? 1 : 0;
    }

    @Override
    public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
        MachineRecipeInput consume = null;
        for (MachineRecipeInput input : inputs) {
            if (input != null && input.slotNumber == 0 && input.item != null) {
                ItemStack consumed = input.item.copy();
                consumed.stackSize = 1;
                consume = new MachineRecipeInput(input.slotNumber, consumed);
            }
        }
        if (consume != null) {
            return Collections.singletonList(consume);
        }
        return null;
    }

    @Override
    public float getExperienceForOutput(ItemStack output) {
        return 0;
    }
}
