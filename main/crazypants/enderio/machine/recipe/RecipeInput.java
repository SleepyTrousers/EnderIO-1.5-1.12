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
      return test.itemID == input.itemID && test.getItemDamage() == input.getItemDamage();
    }
    return test.itemID == input.itemID;
  }

  public ItemStack[] getEquivelentInputs() {
    return null;
  }

  @Override
  public String toString() {
    return "RecipeInput [input=" + input + ", useMeta=" + useMeta + "]";
  }

}
