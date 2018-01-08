package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * This interface for {@link Item}s marks them as being eligable for {@link IDarkSteelUpgrade}s. Ender IO will also handle repairing them if
 * {@link #isItemForRepair(ItemStack)} returns true.
 * 
 * @author Henry Loenwind
 *
 */
public interface IDarkSteelItem {

  /**
   * @return The number of {@link #isItemForRepair(ItemStack)} items it takes to repair this item fully. Usually as many as are used in the crafting recipe.
   */
  default int getIngotsRequiredForFullRepair() {
    return 9;
  }

  /**
   * Checks if the given item (anvil slot "right") can be used to repair this.
   * <p>
   * If this never returns true, Ender IO will leave the item alone.
   * 
   * @param right
   *          The item to test.
   * @return True if this is a repair item (e.g. dark steel ingots).
   */
  default boolean isItemForRepair(@Nonnull ItemStack right) {
    return false;
  }

}
