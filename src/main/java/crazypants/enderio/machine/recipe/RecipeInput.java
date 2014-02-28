package crazypants.enderio.machine.recipe;

import net.minecraft.item.ItemStack;

public class RecipeInput {

  private final ItemStack input;
  private final boolean useMeta;

  public RecipeInput(ItemStack input) {
    this(input, true);
  }

  public RecipeInput(ItemStack input, boolean useMeta) {
    this.input = input;
    this.useMeta = useMeta;
  }

  public ItemStack getInput() {
    return input;
  }

  public boolean isInput(ItemStack test) {
    if(useMeta) {
      return test.getItem() == input.getItem() && test.getItemDamage() == input.getItemDamage();
    }
    return test.getItem() == input.getItem();
  }

  public ItemStack[] getEquivelentInputs() {
    return null;
  }

  @Override
  public String toString() {
    return "RecipeInput [input=" + input + ", useMeta=" + useMeta + "]";
  }

}
