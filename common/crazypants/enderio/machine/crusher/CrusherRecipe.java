package crazypants.enderio.machine.crusher;

import java.util.Arrays;

import net.minecraft.item.ItemStack;

public class CrusherRecipe {

  private final ItemStack input;
  private final CrusherOutput[] output;
  private final float energyRequired;

  public CrusherRecipe(ItemStack input, float energyRequired, CrusherOutput... output) {
    this.input = input;
    this.output = output;
    this.energyRequired = energyRequired;
  }

  public boolean isInput(ItemStack test) {
    if(test == null) {
      return false;
    }
    return test.itemID == input.itemID && test.getItemDamage() == input.getItemDamage();
  }

  public ItemStack getInput() {
    return input;
  }

  public CrusherOutput[] getOutput() {
    return output;
  }

  public float getEnergyRequired() {
    return energyRequired;
  }

  public boolean isValid() {
    return input != null && output != null && energyRequired > 0;
  }

  @Override
  public String toString() {
    return "CrusherRecipe [input=" + input + ", output=" + Arrays.toString(output) + ", energyRequired=" + energyRequired + "]";
  }

}
