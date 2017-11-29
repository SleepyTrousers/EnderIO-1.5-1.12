package crazypants.enderio.util;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Utils to prepare for the next version change.
 *
 */
public class Prep {

  private static final @Nonnull ItemStack EMPTY = new ItemStack(Items.DIAMOND, -1);

  private Prep() {
  }

  /**
   * Checks if an ItemStack exists.
   */
  public static boolean isValid(@Nonnull ItemStack stack) {
    return !stack.isEmpty();
  }

  /**
   * Checks if an ItemStack does not exist.
   */
  public static boolean isInvalid(@Nonnull ItemStack stack) {
    return stack.isEmpty();
  }

  /**
   * Return the placeholder that tells that an ItemStack variable is empty.
   */
  public static @Nonnull ItemStack getEmpty() {
    return EMPTY;
  }

  /**
   * Sets the creative tab to null. Helper to avoid annotation nonsense everywhere.
   */
  @SuppressWarnings("null")
  public static void setNoCreativeTab(Block block) {
    block.setCreativeTab(null);
  }

  /**
   * Sets the creative tab to null. Helper to avoid annotation nonsense everywhere.
   */
  @SuppressWarnings("null")
  public static void setNoCreativeTab(Item item) {
    item.setCreativeTab(null);
  }

}
