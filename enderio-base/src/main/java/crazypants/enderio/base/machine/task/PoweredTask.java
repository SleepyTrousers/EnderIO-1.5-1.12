package crazypants.enderio.base.machine.task;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.util.Prep;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public class PoweredTask implements IPoweredTask {

  public static final @Nonnull String KEY_INPUT_STACKS = "inputsStacks";

  public static final @Nonnull String KEY_RECIPE = "recipeUid";
  public static final @Nonnull String KEY_USED_ENERGY = "usedEnergy";
  public static final @Nonnull String KEY_CUSTOM_ENERGY = "customEnergy";
  private static final @Nonnull String KEY_SEED = "seed";
  private static final @Nonnull String KEY_CHANCE_OUTPUT = "chance1";
  private static final @Nonnull String KEY_CHANCE_MULTI = "chance2";

  private float usedEnergy = 0;

  private @Nonnull NNList<MachineRecipeInput> inputs;

  private float requiredEnergy;
  private boolean hasCustomEnergyCost = false;

  private @Nonnull RecipeBonusType bonusType;

  private @Nonnull IMachineRecipe recipe;

  private long nextSeed;
  private float outputMultiplier;
  private float chanceMultiplier;

  public PoweredTask(@Nonnull IMachineRecipe recipe, long nextSeed, @Nonnull NNList<MachineRecipeInput> inputs) {
    this(recipe, 0, nextSeed, 1f, 1f, inputs);
  }

  public PoweredTask(@Nonnull IMachineRecipe recipe, long nextSeed, float outputMultiplier, float chanceMultiplier,
      @Nonnull NNList<MachineRecipeInput> inputs) {
    this(recipe, 0, nextSeed, outputMultiplier, chanceMultiplier, inputs);
  }

  protected PoweredTask(@Nonnull IMachineRecipe recipe, float usedEnergy, long nextSeed, float outputMultiplier, float chanceMultiplier,
      @Nonnull NNList<MachineRecipeInput> inputsIn) {
    inputs = new NNList<>();
    for (int i = 0; i < inputsIn.size(); i++) {
      if (Prep.isValid(inputsIn.get(i).item)) {
        inputs.add(new MachineRecipeInput(inputsIn.get(i).slotNumber, inputsIn.get(i).item.copy()));
      } else if (inputsIn.get(i).fluid != null) {
        inputs.add(new MachineRecipeInput(inputsIn.get(i).slotNumber, inputsIn.get(i).fluid.copy()));
      }
    }

    this.recipe = recipe;
    this.usedEnergy = usedEnergy;
    this.nextSeed = nextSeed;
    this.outputMultiplier = outputMultiplier;
    this.chanceMultiplier = chanceMultiplier;
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
    return MathHelper.clamp(usedEnergy / requiredEnergy, 0, 1);
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult() {
    Random rand = new Random(nextSeed);
    NNList<ResultStack> result = new NNList<>();
    result.addAll(recipe.getCompletedResult(rand.nextLong(), getBonusType().doChances() ? chanceMultiplier : 1f, inputs));
    if (getBonusType().doMultiply()) {
      float mul = outputMultiplier - 1f;
      while (mul > 0) {
        if (rand.nextFloat() < mul) {
          result.addAll(recipe.getCompletedResult(rand.nextLong(), getBonusType().doChances() ? chanceMultiplier : 1f, inputs));
        }
        mul--;
      }
    }
    return result.toArray(new ResultStack[0]);
  }

  @Override
  public @Nonnull NNList<MachineRecipeInput> getInputs() {
    return inputs;
  }

  public void setInputs(@Nonnull NNList<MachineRecipeInput> inputs) {
    this.inputs = inputs;
  }

  @Override
  public float getRequiredEnergy() {
    return requiredEnergy;
  }

  public void setRequiredEnergy(float requiredEnergy) {
    this.requiredEnergy = requiredEnergy;
    this.hasCustomEnergyCost = true;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType() {
    return bonusType;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagList inputItems = new NBTTagList();
    for (MachineRecipeInput ri : inputs) {
      NBTTagCompound stackRoot = new NBTTagCompound();
      ri.writeToNbt(stackRoot);
      inputItems.appendTag(stackRoot);
    }

    nbtRoot.setTag(KEY_INPUT_STACKS, inputItems);

    nbtRoot.setString(KEY_RECIPE, recipe.getUid());
    nbtRoot.setFloat(KEY_USED_ENERGY, usedEnergy);
    if (hasCustomEnergyCost) {
      nbtRoot.setFloat(KEY_CUSTOM_ENERGY, requiredEnergy);
    }

    nbtRoot.setLong(KEY_SEED, nextSeed);
    nbtRoot.setFloat(KEY_CHANCE_OUTPUT, outputMultiplier);
    nbtRoot.setFloat(KEY_CHANCE_MULTI, chanceMultiplier);
  }

  public static @Nullable IPoweredTask readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    IMachineRecipe recipe;

    float usedEnergy = nbtRoot.getFloat(KEY_USED_ENERGY);
    long seed = nbtRoot.getLong(KEY_SEED);
    float outputMultiplier = nbtRoot.getFloat(KEY_CHANCE_OUTPUT);
    float chanceMultiplier = nbtRoot.getFloat(KEY_CHANCE_MULTI);

    boolean hasCustomEnergyCost = false;
    float requiredEnergy = 0;
    if (nbtRoot.hasKey(KEY_CUSTOM_ENERGY)) {
      hasCustomEnergyCost = true;
      requiredEnergy = nbtRoot.getFloat(KEY_CUSTOM_ENERGY);
    }

    NBTTagList inputItems = (NBTTagList) nbtRoot.getTag(KEY_INPUT_STACKS);

    NNList<MachineRecipeInput> ins = new NNList<MachineRecipeInput>();
    for (int i = 0; i < inputItems.tagCount(); i++) {
      NBTTagCompound stackTag = inputItems.getCompoundTagAt(i);
      MachineRecipeInput mi = MachineRecipeInput.readFromNBT(stackTag);
      ins.add(mi);
    }

    String uid = nbtRoot.getString(KEY_RECIPE);
    recipe = MachineRecipeRegistry.instance.getRecipeForUid(uid);
    if (recipe != null) {
      final PoweredTask poweredTask = new PoweredTask(recipe, usedEnergy, seed, outputMultiplier, chanceMultiplier, ins);
      if (hasCustomEnergyCost) {
        poweredTask.setRequiredEnergy(requiredEnergy);
      }
      return poweredTask;
    }
    return null;

  }

  @Override
  public IMachineRecipe getRecipe() {
    return recipe;
  }

}
