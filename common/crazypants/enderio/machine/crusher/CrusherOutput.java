package crazypants.enderio.machine.crusher;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CrusherOutput {

  private final ItemStack output;
  private final float chance;

  public CrusherOutput(Block output) {
    this(new ItemStack(output), 1);
  }

  public CrusherOutput(Block output, float chance) {
    this(new ItemStack(output), chance);
  }

  public CrusherOutput(Item output, float chance) {
    this(new ItemStack(output), chance);
  }

  public CrusherOutput(Item output) {
    this(new ItemStack(output), 1);
  }

  public CrusherOutput(ItemStack output) {
    this(output, 1);
  }

  public CrusherOutput(ItemStack output, float chance) {
    this.output = output.copy();
    this.chance = chance;
  }

  public float getChance() {
    return chance;
  }

  public ItemStack getOutput() {
    return output;
  }

}
