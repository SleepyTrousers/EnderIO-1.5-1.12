package crazypants.enderio.api.upgrades;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;

import crazypants.enderio.base.handler.darksteel.PacketDarkSteelSFXPacket;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

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

  default @Nonnull String getDisplayName() {
    return I18n.translateToLocal(getUnlocalizedName() + ".name");
  }

  /**
   * @return A {@link Pair}<code>&lt;{@link String}, {@link Integer}&gt;</code> that is used to sort upgrades for display in JEI and the Creative Menu. The
   *         sorting is done on the concatenation of both. When determining if two upgrades should be grouped together, the String is compared and the upgrades
   *         are ordered by the number.
   */
  default @Nonnull Pair<String, Integer> getSortKey() {
    return Pair.of(getUnlocalizedName(), 0);
  }

  /**
   * @return The amount of levels it costs to apply this upgrade.
   */
  int getLevelCost();

  /**
   * Checks if the given stack has this upgrade.
   * <p>
   * <em>final</em>
   * 
   * @param stack
   *          An itemstack to test.
   * @return True if the given stack has this upgrade.
   */
  default boolean hasUpgrade(@Nonnull ItemStack stack) {
    return stack.getItem() instanceof IDarkSteelItem ? hasUpgrade(stack, (IDarkSteelItem) stack.getItem()) : false;
  }

  /**
   * Checks if the given stack has this upgrade.
   * 
   * @param stack
   *          An itemstack to test.
   * @param item
   *          The item of the stack, pre-cast to {@link IDarkSteelItem}
   * @return True if the given stack has this upgrade.
   */
  boolean hasUpgrade(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item);

  /**
   * Checks if this upgrade can be applied to the given item. This is called by the anvil crafting for the "left" item.
   * <p>
   * This is also called by the upgrade GUI to check if the slot for this upgrade should be open or blocked.
   * <p>
   * <em>final</em>
   * 
   * @param stack
   *          An itemstack to test.
   * @param item
   *          The item of the stack, pre-cast to {@link IDarkSteelItem}
   * @return True if this upgrade can be applied to the given item.
   */
  default boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return !hasUpgrade(stack, item) && getRules().stream().allMatch(rule -> rule.check(stack, item).passes());
  }

  /**
   * Checks if the other upgrade can be removed or if it is a prerequisite for this upgrade. This is called to build the JEI recipes, so it must mirror
   * {@link #canAddToItem(ItemStack, IDarkSteelItem)}.
   * 
   * @param stack
   *          An itemstack to test.
   * @param item
   *          The item of the stack, pre-cast to {@link IDarkSteelItem}
   * @param other
   *          The upgrade that would be removed.
   * @return True if the other upgrade can be removed.
   */
  default boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return true;
  }

  /**
   * Applies the upgrade to the item's nbt.
   * 
   * @param stack
   *          The item to upgrade.
   * @param item
   *          The item of the stack, pre-cast to {@link IDarkSteelItem}
   */
  void addToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item);

  /**
   * Removes the upgrade from the item's nbt and returns the materials of the upgrade.
   * 
   * @param stack
   *          The stack to downgrade
   * @param item
   *          The item of the stack, pre-cast to {@link IDarkSteelItem}
   */
  void removeFromItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item);

  /**
   * See {@link PlayerTickEvent}. Called when the given item is equipped in any {@link EntityEquipmentSlot} and has this upgrade.
   * 
   * @param stack
   *          The stack that has the upgrade.
   * @param item
   *          The item of the stack, pre-cast to {@link IDarkSteelItem}
   * @param player
   *          The player.
   */
  default void onPlayerTick(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull EntityPlayer player) {
  }

  /**
   * Called via server when another player activates the SFX for this upgrade. That activation has to send a {@link PacketDarkSteelSFXPacket} to the server if
   * it wants this to fire on other clients, that is not happening auto-magically.
   * 
   * @param otherPlayer
   *          The player that needs SFX.
   */
  @SideOnly(Side.CLIENT)
  default void doMultiplayerSFX(@Nonnull EntityPlayer otherPlayer) {
  }

  /**
   * See {@link Item#getAttributeModifiers(EntityEquipmentSlot, ItemStack)}. This allows upgrades to also add attribute modifieres to the item.
   * 
   * @param slot
   *          See {@link Item#getAttributeModifiers(EntityEquipmentSlot, ItemStack)}
   * @param stack
   *          See {@link Item#getAttributeModifiers(EntityEquipmentSlot, ItemStack)}
   * @param map
   *          the pre-populated attribute modifier map
   */
  default void addAttributeModifiers(@Nonnull EntityEquipmentSlot slot, @Nonnull ItemStack stack, @Nonnull Multimap<String, AttributeModifier> map) {
  }

  /**
   * Returns a list of rules that determine if the upgrade can be applied to an item. The list should be stable and all rules must return the same result on
   * server and client.
   * <p>
   * Note: There is no need to include a rule to prevent applying the same upgrade twice.
   * <p>
   * Note 2: There should be at least one {@link IRule.ItemType} rule (for the upgrade item tooltip).
   * 
   */
  @Nonnull
  List<IRule> getRules();

}
