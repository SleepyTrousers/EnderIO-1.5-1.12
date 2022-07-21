package crazypants.enderio.machine.recipe;

import crazypants.enderio.machine.MachineRecipeInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Recipe implements IRecipe {

    private final RecipeInput[] inputs;
    private final RecipeOutput[] outputs;
    private final int energyRequired;
    private final RecipeBonusType bonusType;

    public Recipe(RecipeOutput output, int energyRequired, RecipeBonusType bonusType, RecipeInput... input) {
        this(input, new RecipeOutput[] {output}, energyRequired, bonusType);
    }

    public Recipe(RecipeInput input, int energyRequired, RecipeBonusType bonusType, RecipeOutput... output) {
        this(new RecipeInput[] {input}, output, energyRequired, bonusType);
    }

    public Recipe(RecipeInput[] input, RecipeOutput[] output, int energyRequired, RecipeBonusType bonusType) {
        this.inputs = input;
        this.outputs = output;
        this.energyRequired = energyRequired;
        this.bonusType = bonusType;
    }

    @Override
    public boolean isInputForRecipe(MachineRecipeInput... machineInputs) {
        if (machineInputs == null || machineInputs.length == 0) {
            return false;
        }

        List<RecipeInput> requiredInputs = new ArrayList<RecipeInput>();
        for (RecipeInput input : inputs) {
            if (input.getFluidInput() != null || input.getInput() != null) {
                requiredInputs.add(input.copy());
            }
        }

        for (MachineRecipeInput input : machineInputs) {
            if (input != null && (input.fluid != null || input.item != null)) {
                RecipeInput required = null;
                for (int i = 0; i < requiredInputs.size() && required == null; i++) {
                    RecipeInput tst = requiredInputs.get(i);
                    if ((tst.isInput(input.item) && tst.getInput().stackSize > 0) || tst.isInput(input.fluid)) {
                        required = tst;
                    }
                }
                if (required == null) {
                    return false;
                }
                // reduce the required input quantity by the available amount
                if (input.isFluid()) {
                    required.getFluidInput().amount -= input.fluid.amount;
                } else {
                    required.getInput().stackSize -= input.item.stackSize;
                }
            }
        }

        for (RecipeInput required : requiredInputs) {
            if (required.isFluid() && required.getFluidInput().amount > 0) {
                return false;
            } else if (!required.isFluid() && required.getInput().stackSize > 0) {
                return false;
            }
        }
        return true;
    }

    protected int getMinNumInputs() {
        return inputs.length;
    }

    @Override
    public boolean isValidInput(int slot, ItemStack item) {
        return getInputForStack(item) != null;
    }

    @Override
    public boolean isValidInput(FluidStack fluid) {
        return getInputForStack(fluid) != null;
    }

    private RecipeInput getInputForStack(FluidStack input) {
        for (RecipeInput ri : inputs) {
            if (ri.isInput(input)) {
                return ri;
            }
        }
        return null;
    }

    private RecipeInput getInputForStack(ItemStack input) {
        for (RecipeInput ri : inputs) {
            if (ri.isInput(input)) {
                return ri;
            }
        }
        return null;
    }

    @Override
    public List<ItemStack> getInputStacks() {
        if (inputs == null) {
            return Collections.emptyList();
        }
        List<ItemStack> res = new ArrayList<ItemStack>(inputs.length);
        for (int i = 0; i < inputs.length; i++) {
            RecipeInput in = inputs[i];
            if (in != null && in.getInput() != null) {
                res.add(in.getInput());
            }
        }
        return res;
    }

    @Override
    public List<FluidStack> getInputFluidStacks() {
        if (inputs == null) {
            return Collections.emptyList();
        }
        List<FluidStack> res = new ArrayList<FluidStack>(inputs.length);
        for (int i = 0; i < inputs.length; i++) {
            RecipeInput in = inputs[i];
            if (in != null && in.getFluidInput() != null) {
                res.add(in.getFluidInput());
            }
        }
        return res;
    }

    @Override
    public RecipeInput[] getInputs() {
        return inputs;
    }

    @Override
    public RecipeOutput[] getOutputs() {
        return outputs;
    }

    @Override
    public RecipeBonusType getBonusType() {
        return bonusType;
    }

    public boolean hasOuput(ItemStack result) {
        if (result == null) {
            return false;
        }
        for (RecipeOutput output : outputs) {
            ItemStack os = output.getOutput();
            if (os != null && os.isItemEqual(result)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getEnergyRequired() {
        return energyRequired;
    }

    @Override
    public boolean isValid() {
        return inputs != null && outputs != null && energyRequired > 0;
    }

    @Override
    public String toString() {
        return "Recipe [input=" + Arrays.toString(inputs) + ", output=" + Arrays.toString(outputs) + ", energyRequired="
                + energyRequired + "]";
    }
}
