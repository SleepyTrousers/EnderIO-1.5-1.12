package crazypants.enderio.machine.crusher;

import net.minecraft.item.ItemStack;

public class CrusherRecipe {

  private final ItemStack input;
  private final CrusherOutput[] output;
  private final float energyRequired;

  public CrusherRecipe(ItemStack input, float energyRequired, CrusherOutput... outupt) {
    this.input = input;
    this.output = outupt;
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

}
