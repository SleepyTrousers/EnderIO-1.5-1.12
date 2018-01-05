package crazypants.enderio.base.machine.interfaces;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * Interface to be implemented on items or blocks to indicate that the item can be cleared of configuration in the crafting grid.
 * <p>
 * Note: When this is done, all nbt of the item will be deleted.
 *
 */
public interface IClearableConfiguration {

  /**
   * Sub-interface for items or blocks that want to handle the clearing themselves.
   *
   */
  public static interface Handler extends IClearableConfiguration {

    /**
     * Removes all configuration from the NBT of the given stack.
     */
    void clearConfiguration(@Nonnull ItemStack stack);

  }

}
