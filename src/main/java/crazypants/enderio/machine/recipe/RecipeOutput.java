package crazypants.enderio.machine.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeOutput {

  private final ItemStack output;
  private final float chance;
  private float exp;

  public RecipeOutput(Block output) {
    this(new ItemStack(output), 1);
  }

  public RecipeOutput(Block output, float chance) {
    this(new ItemStack(output), chance);
  }

  public RecipeOutput(Item output, float chance) {
    this(new ItemStack(output), chance);
  }

  public RecipeOutput(Item output) {
    this(new ItemStack(output), 1);
  }

  public RecipeOutput(ItemStack output) {
    this(output, 1);
  }

  public RecipeOutput(ItemStack output, float chance) {
    this(output, chance, 0);
  }

  public RecipeOutput(ItemStack output, float chance, float exp) {
    this.output = output.copy();
    this.chance = chance;
    this.exp = exp;
  }

  public float getChance() {
    return chance;
  }

  public float getExperiance() {
    return exp;
  }

  public ItemStack getOutput() {
    return output;
  }

}
