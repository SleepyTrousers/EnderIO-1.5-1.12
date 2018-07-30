package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * This interface for {@link Item}s marks them as being eligible for {@link IDarkSteelUpgrade}s. Ender IO will also handle repairing them if
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
   * If this never returns true, Ender IO will leave the item alone and not try to repair it.
   * 
   * @param right
   *          The item to test.
   * @return True if this is a repair item (e.g. dark steel ingots).
   */
  default boolean isItemForRepair(@Nonnull ItemStack right) {
    return false;
  }

  /**
   * Checks if this item is for the given equipment slot.
   * <p>
   * Used by upgrades to determine if they can be applied to an item.
   */
  default boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return false;
  }

  /**
   * Checks if this item is a pickaxe.
   * <p>
   * Used by upgrades to determine if they can be applied to an item.
   * <p>
   * Note: Items that return true here should also do so for {@link #isForSlot(EntityEquipmentSlot)} with {@link EntityEquipmentSlot#MAINHAND}.
   */
  default boolean isPickaxe() {
    return false;
  }

  /**
   * Checks if this item is an axe.
   * <p>
   * Used by upgrades to determine if they can be applied to an item.
   * <p>
   * Note: Items that return true here should also do so for {@link #isForSlot(EntityEquipmentSlot)} with {@link EntityEquipmentSlot#MAINHAND}.
   */
  default boolean isAxe() {
    return false;
  }

  /**
   * Checks if this item has the needed code support for the given upgrade.
   * <p>
   * Used by some upgrades to determine if they can be applied to an item. Only upgrades that need the item to have supporting code will call this, e.g. the
   * Spoon upgrade needs the item to have a specialized canHarvestBlock() and getToolClasses().
   * <p>
   * Note that the energy upgrade is implicit---all dark steel item must support it.
   */
  default boolean hasUpgradeCallbacks(@Nonnull IDarkSteelUpgrade upgrade) {
    return false;
  }

  /**
   * Returns an {@link IEquipmentData} that describes the item.
   * <p>
   * Used by upgrades to determine if they can be applied to an item
   */
  @Nonnull
  IEquipmentData getEquipmentData();

  default @Nonnull ICapacitorKey getEnergyStorageKey() {
    return CapacitorKey.DARK_STEEL_ENERGY_BUFFER;
  }

  default @Nonnull ICapacitorKey getEnergyInputKey() {
    return CapacitorKey.DARK_STEEL_ENERGY_INPUT;
  }

  // This is used when extracting energy, limiting the amount that can be extracted at once
  default @Nonnull ICapacitorKey getEnergyUseKey() {
    return CapacitorKey.DARK_STEEL_ENERGY_USE;
  }

  default @Nonnull ICapacitorKey getAbsobtionRatioKey() {
    return CapacitorKey.DARK_STEEL_ABSORBTION_RATIO;
  }

}
