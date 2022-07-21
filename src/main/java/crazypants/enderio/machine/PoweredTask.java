package crazypants.enderio.machine;

import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;

public class PoweredTask implements IPoweredTask {

    public static final String KEY_INPUT_STACKS = "inputsStacks";

    public static final String KEY_RECIPE = "recipeUid";
    public static final String KEY_USED_ENERGY = "usedEnergy";
    private static final String KEY_CHANCE = "chance";

    private float usedEnergy = 0;

    private MachineRecipeInput[] inputs;

    private float requiredEnergy;

    private RecipeBonusType bonusType;

    private IMachineRecipe recipe;

    private float chance;

    public PoweredTask(IMachineRecipe recipe, float chance, MachineRecipeInput... inputs) {
        this(recipe, 0, chance, inputs);
    }

    protected PoweredTask(IMachineRecipe recipe, float usedEnergy, float chance, MachineRecipeInput... inputsIn) {
        this.inputs = inputsIn;
        int numInputs = 0;
        for (int i = 0; i < inputsIn.length; i++) {
            if (inputsIn[i] != null && (inputsIn[i].item != null || inputsIn[i].fluid != null)) {
                numInputs++;
            }
        }

        inputs = new MachineRecipeInput[numInputs];
        int index = 0;
        for (int i = 0; i < inputsIn.length; i++) {
            if (inputsIn[i] != null) {
                if (inputsIn[i].item != null) {
                    inputs[index] = new MachineRecipeInput(inputsIn[i].slotNumber, inputsIn[i].item.copy());
                    index++;
                } else if (inputsIn[i].fluid != null) {
                    inputs[index] = new MachineRecipeInput(inputsIn[i].slotNumber, inputsIn[i].fluid.copy());
                    index++;
                }
            }
        }

        this.recipe = recipe;
        this.usedEnergy = usedEnergy;
        this.chance = MathHelper.clamp_float(chance, 0, 1);
        requiredEnergy = recipe.getEnergyRequired(inputsIn);
        bonusType = recipe.getBonusType(inputsIn);
    }

    @Override
    public void update(float availableEnergy) {
        usedEnergy += availableEnergy;
    }

    @Override
    public boolean isComplete() {
        return usedEnergy >= requiredEnergy;
    }

    @Override
    public float getProgress() {
        return MathHelper.clamp_float(usedEnergy / requiredEnergy, 0, 1);
    }

    @Override
    public ResultStack[] getCompletedResult() {
        return recipe.getCompletedResult(chance, inputs);
    }

    @Override
    public MachineRecipeInput[] getInputs() {
        return inputs;
    }

    public void setInputs(MachineRecipeInput[] inputs) {
        this.inputs = inputs;
    }

    /* (non-Javadoc)
     * @see crazypants.enderio.machine.IPoweredTask#getRequiredEnergy()
     */
    @Override
    public float getRequiredEnergy() {
        return requiredEnergy;
    }

    public void setRequiredEnergy(float requiredEnergy) {
        this.requiredEnergy = requiredEnergy;
    }

    /* (non-Javadoc)
     * @see crazypants.enderio.machine.IPoweredTask#getChance()
     */
    @Override
    public float getChance() {
        return chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    @Override
    public RecipeBonusType getBonusType() {
        return bonusType;
    }

    /* (non-Javadoc)
     * @see crazypants.enderio.machine.IPoweredTask#writeToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void writeToNBT(NBTTagCompound nbtRoot) {
        NBTTagCompound stackRoot;

        NBTTagList inputItems = new NBTTagList();
        for (MachineRecipeInput ri : inputs) {
            stackRoot = new NBTTagCompound();
            ri.writeToNbt(stackRoot);
            inputItems.appendTag(stackRoot);
        }

        nbtRoot.setTag(KEY_INPUT_STACKS, inputItems);

        nbtRoot.setString(KEY_RECIPE, recipe.getUid());
        nbtRoot.setFloat(KEY_USED_ENERGY, usedEnergy);

        nbtRoot.setFloat(KEY_CHANCE, chance);
    }

    public static IPoweredTask readFromNBT(NBTTagCompound nbtRoot) {
        IMachineRecipe recipe;

        float usedEnergy = nbtRoot.getFloat(KEY_USED_ENERGY);
        float chance = nbtRoot.getFloat(KEY_CHANCE);

        NBTTagList inputItems = (NBTTagList) nbtRoot.getTag(KEY_INPUT_STACKS);
        if (inputItems == null) {
            return null;
        }

        List<MachineRecipeInput> ins = new ArrayList<MachineRecipeInput>(3);
        for (int i = 0; i < inputItems.tagCount(); i++) {
            NBTTagCompound stackTag = inputItems.getCompoundTagAt(i);
            MachineRecipeInput mi = MachineRecipeInput.readFromNBT(stackTag);
            ins.add(mi);
        }

        String uid = nbtRoot.getString(KEY_RECIPE);
        recipe = MachineRecipeRegistry.instance.getRecipeForUid(uid);
        if (recipe != null) {
            return new PoweredTask(recipe, usedEnergy, chance, ins.toArray(new MachineRecipeInput[ins.size()]));
        }
        return null;
    }

    public IMachineRecipe getRecipe() {
        return recipe;
    }
}
