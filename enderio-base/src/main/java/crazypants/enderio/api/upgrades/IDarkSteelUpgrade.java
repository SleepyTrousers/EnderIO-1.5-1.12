package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

/**
 * This class is an upgrade that can be applied to Dark Steel items.
 * <p>
 * The upgrade needs to be registered in the {@link net.minecraftforge.event.RegistryEvent.Register}&lt;IDarkSteelUpgrade&gt; event.
 * <p>
 * Upgrades themselves are stateless. They determine if they are present on an item or not. By themselves, upgrades can not do very much---they are ticked and
 * optionally can render on the player. Further functionality needs to be implemented on an event handler that checks for the upgrade's existence.
 * <p>
 * Other interfaces upgrades can implement: IAdvancedTooltipProvider (from endercore), {@link IHasPlayerRenderer}
 * 
 * @author Henry Loenwind
 *
 */
public interface IDarkSteelUpgrade extends IForgeRegistryEntry<IDarkSteelUpgrade> {

  /**
   * @return The unlocalized name to display in the tooltip.
   */
  @Nonnull
  String getUnlocalizedName();

  /**
   * @return The amount of levels it costs to apply this upgrade.
   */
  int getLevelCost();

  /**
   * @return The item that is shown in the JEI recipe for this upgrade.
   */
  @Nonnull
  ItemStack getUpgradeItem();

  /**
   * @return The item name that is shown in the tooltip for available upgrades.
   */
  default @Nonnull String getUpgradeItemName() {
    return getUpgradeItem().getDisplayName();
  }

  /**
   * Checks if the given item can be used to apply this upgrade. This is used in the anvil to select the upgrade.
   * 
   * @param stack
   *          The "right" item in the anvil.
   * @return True if this the the upgrade's recipe item.
   */
  default boolean isUpgradeItem(@Nonnull ItemStack stack) {
    return !stack.isEmpty() && stack.isItemEqual(getUpgradeItem()) && stack.getCount() == getUpgradeItem().getCount();
  }

  /**
   * Checks if the given stack has this upgrade.
   * 
   * @param stack
   *          An itemstack to test.
   * @return True if the given stack has this upgrade.
   */
  boolean hasUpgrade(@Nonnull ItemStack stack);

  /**
   * Checks if this upgrade can be applied to the given item. This is called by the anvil crafting for the "left" item after {@link #isUpgradeItem(ItemStack)}
   * return true for the "right" item.
   * 
   * @param stack
   *          An itemstack to test.
   * @return True if this upgrade can be applied to the given item.
   */
  boolean canAddToItem(@Nonnull ItemStack stack);

  /**
   * Applies the upgrade to the item's nbt.
   * 
   * @param stack
   *          The item to upgrade.
   */
  void addToItem(@Nonnull ItemStack stack);

  /**
   * See {@link PlayerTickEvent}. Called when the given item is equipped in any {@link EntityEquipmentSlot} and has this upgrade.
   * 
   * @param stack
   *          The stack that has the upgrade.
   * @param player
   *          The player.
   */
  default void onPlayerTick(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
  }

}
