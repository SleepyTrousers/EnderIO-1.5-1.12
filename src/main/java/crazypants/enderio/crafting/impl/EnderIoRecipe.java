package crazypants.enderio.crafting.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeComponent;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;

public class EnderIoRecipe implements IEnderIoRecipe {

  private final String crafterId;

  private final float requiredEnergy;

  private final List<IRecipeInput> inputs = new ArrayList<IRecipeInput>();
  private final List<IRecipeInput> inputsRO = Collections.unmodifiableList(inputs);
  private final List<IRecipeOutput> outputs = new ArrayList<IRecipeOutput>();
  private final List<IRecipeOutput> outputsRO = Collections.unmodifiableList(outputs);

  public EnderIoRecipe(String crafterId, float requiredEnergy, ItemStack input, ItemStack output) {
    this(crafterId, requiredEnergy);
    inputs.add(new RecipeInput(input));
    outputs.add(new RecipeOutput(output));
  }

  public EnderIoRecipe(String crafterId, float requiredEnergy, ItemStack input, IRecipeOutput... outputs) {
    this(crafterId, requiredEnergy);
    inputs.add(new RecipeInput(input));
    for (IRecipeOutput output : outputs) {
      this.outputs.add(output);
    }
  }

  public EnderIoRecipe(String crafterId, float requiredEnergy, IRecipeComponent... components) {
    this(crafterId, requiredEnergy);
    for (IRecipeComponent component : components) {
      if(component instanceof IRecipeOutput) {
        outputs.add((IRecipeOutput) component);
      } else if(component instanceof IRecipeInput) {
        inputs.add((IRecipeInput) component);
      }
    }
  }

  public EnderIoRecipe(String crafterId, float requiredEnergy, Collection<IRecipeComponent> components) {
    this(crafterId, requiredEnergy);
    for (IRecipeComponent component : components) {
      if(component instanceof IRecipeOutput) {
        outputs.add((IRecipeOutput) component);
      } else if(component instanceof IRecipeInput) {
        inputs.add((IRecipeInput) component);
      }
    }
  }

  private EnderIoRecipe(String crafterId, float requiredEnergy) {
    this.crafterId = crafterId;
    this.requiredEnergy = requiredEnergy;
  }

  @Override
  public String getCrafterId() {
    return crafterId;
  }

  @Override
  public List<IRecipeInput> getInputs() {
    return inputsRO;
  }

  @Override
  public List<IRecipeOutput> getOutputs() {
    return outputsRO;
  }

  @Override
  public boolean isInput(ItemStack input) {
    for (IRecipeComponent rc : inputs) {
      if(rc.isEquivalent(input)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isOutput(ItemStack output) {
    for (IRecipeComponent rc : outputs) {
      if(rc.isEquivalent(output)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public float getRequiredEnergy() {
    return requiredEnergy;
  }

}
