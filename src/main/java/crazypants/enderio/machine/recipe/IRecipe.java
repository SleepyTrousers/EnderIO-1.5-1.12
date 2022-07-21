package crazypants.enderio.machine.recipe;

import crazypants.enderio.machine.MachineRecipeInput;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipe {

    boolean isValid();

    int getEnergyRequired();

    RecipeOutput[] getOutputs();

    RecipeInput[] getInputs();

    List<ItemStack> getInputStacks();

    List<FluidStack> getInputFluidStacks();

    RecipeBonusType getBonusType();

    //  boolean isInputForRecipe(List<ItemStack> test);
    //
    //  boolean isInputForRecipe(List<ItemStack> test, List<FluidStack> testFluids);

    boolean isInputForRecipe(MachineRecipeInput... inputs);

    boolean isValidInput(int slotNumber, ItemStack item);

    boolean isValidInput(FluidStack fluid);
}
