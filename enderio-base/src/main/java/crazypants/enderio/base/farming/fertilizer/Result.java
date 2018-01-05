package crazypants.enderio.base.farming.fertilizer;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public class Result {

  private final @Nonnull ItemStack stack;
  private final boolean wasApplied;

  public Result(@Nonnull ItemStack stack, boolean wasApplied) {
    this.stack = stack;
    this.wasApplied = wasApplied;
  }

  public @Nonnull ItemStack getStack() {
    return stack;
  }

  public boolean isWasApplied() {
    return wasApplied;
  }

}