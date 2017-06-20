package crazypants.enderio.machine.soul;

import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;

public class BasicSoulBinderRecipe extends AbstractSoulBinderRecipe {

  private ItemStack inputStack;
  private ItemStack outputStack;

  public BasicSoulBinderRecipe(ItemStack inputStack, ItemStack outputStack, int energyRequired, int xpRequired, String uid, String... entityNames) {
    super(energyRequired, xpRequired, uid, entityNames);
    this.inputStack = inputStack.copy();
    this.outputStack = outputStack.copy();
  }

  @Override
  public ItemStack getInputStack() {
    return inputStack.copy();
  }

  @Override
  public ItemStack getOutputStack() {
    return outputStack.copy();
  }

  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return getOutputStack();
  }

}
