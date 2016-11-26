package crazypants.util;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * Utils to prepare for the next version change.
 *
 */
public class Prep {

  private Prep() {
  }

  /**
   * Checks if an ItemStack exists.
   * <p>
   * 1.11: replace with !stack.isEmpty() and inline.
   */
  public static boolean isValid(@Nullable ItemStack stack) {
    return stack != null && stack.getItem() != null;
  }

  /**
   * Checks if an ItemStack does not exist.
   * <p>
   * 1.11: replace with stack.isEmpty() and inline.
   */
  public static boolean isInvalid(@Nullable ItemStack stack) {
    return stack == null || stack.getItem() == null;
  }

  /**
   * Return the placeholder that tells that an ItemStack variable is empty.
   * <p>
   * 1.11: replace with new ItemStack(Items.DIAMOND, -1) and inline.
   */
  public static ItemStack getEmpty() {
    return null;
  }

}
