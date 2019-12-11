package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

/**
 * This represents a call back mechanism into the Farming Station.
 * 
 * @author Henry Loenwind
 *
 */
public interface IFarmer {

  /**
   * Get the itemstack in the seed slot for the given location.
   * 
   * @param pos
   *          The target location.
   * @return An itemstack, may be empty.
   */
  @Nonnull
  ItemStack getSeedTypeInSuppliesFor(@Nonnull BlockPos pos);

  /**
   * @return The fake player for the Farming Station.
   */
  @Nonnull
  FakePlayer getFakePlayer();

  /**
   * @return The Farming Station's world.
   */
  @Nonnull
  World getWorld();

  /**
   * The location of the Farming Station.
   */
  @Nonnull
  BlockPos getLocation();

  /**
   * Get the itemstack in the seed slot for the given location if it matches the given template.
   * 
   * @param seeds
   *          The template to check for.
   * @param pos
   *          The location to check for.
   * @param simulate
   *          if <code>true</code>, the seed is returned but not removed from the inventory
   * @return An itemstack of size 1 or an empty stack.
   */

  @Nonnull
  ItemStack takeSeedFromSupplies(@Nonnull ItemStack seeds, @Nonnull BlockPos pos, boolean simulate);

  /**
   * Removes a single item from the seed slot for the given location ant returns it.
   * 
   * @param pos
   *          The target location.
   * @param simulate
   *          if <code>true</code>, the seed is returned but not removed from the inventory
   * @return An itemstack with a size of one or an empty itemstack.
   */
  default @Nonnull ItemStack takeSeedFromSupplies(@Nonnull BlockPos pos, boolean simulate) {
    return takeSeedFromSupplies(ItemStack.EMPTY, pos, simulate);
  }

  default @Nonnull ItemStack takeSeedFromSupplies(@Nonnull BlockPos pos) {
    return takeSeedFromSupplies(pos, false);
  }

  /**
   * Checks if the Farming Station has a usable tool of that type.
   * 
   * @param tool
   *          The tool type to check for.
   * @return True if a tool is available
   */
  boolean hasTool(@Nonnull IFarmingTool tool);

  /**
   * Sets a notification. Notifications are cleared automatically.
   * 
   * @param notification
   *          The notification to set.
   */
  void setNotification(@Nonnull FarmNotification notification);

  /**
   * Get the looting/fortune value the tool of the given type.
   * 
   * @param tool
   *          The tool type to check for.
   * @return The looting/fortune level of the tool.
   */
  int getLootingValue(@Nonnull IFarmingTool tool);

  /**
   * Shortcut for {@link #getWorld()}.getBlockState()
   */
  @Nonnull
  IBlockState getBlockState(@Nonnull BlockPos pos);

  /**
   * Gets the tool of the given type.
   * 
   * @param tool
   *          The tool type to get.
   * @return The tool or an empty stack.
   */
  @Nonnull
  ItemStack getTool(@Nonnull IFarmingTool tool);

  /**
   * Prepares the fake player to be used for interacting with the world using the given item in the main hand.
   */
  @Nonnull
  FakePlayer startUsingItem(@Nonnull ItemStack stack);

  /**
   * Prepares the fake player to be used for interacting with the world using the given tool in the main hand.
   */
  @Nonnull
  FakePlayer startUsingItem(@Nonnull IFarmingTool tool);

  /**
   * Reverts the fake player back into its dormant state and returns all items it collected in the meantime.
   * 
   * @param trashHandItem
   *          If true then the main hand item (the one that was set with startUsingItem()) is trashed and not returned.
   * @return A list of items that were in the fake player's inventory.
   */
  @Nonnull
  NonNullList<ItemStack> endUsingItem(boolean trashHandItem);

  /**
   * Reverts the fake player back into its dormant state and returns all items it collected in the meantime.
   * 
   * @param tool
   *          Where the main hand item (the one that was set with startUsingItem()) should be returned to.
   * @return A list of items that were in the fake player's inventory.
   */
  @Nonnull
  NonNullList<ItemStack> endUsingItem(@Nonnull IFarmingTool tool);

  /**
   * Adds the given items to the farm's inventory if possible or drops them into the world (at the given location or, if none was given, at the farm's location)
   * otherwise.
   * <p>
   * This is only intended for actions that cannot put them into the result list.
   * 
   * @param items
   */
  void handleExtraItems(@Nonnull NonNullList<ItemStack> items, @Nullable BlockPos pos);

  /**
   * Checks if the farm can do the given action. Checks:
   * <ul>
   * <li>That there is a matching tool
   * <li>There is enough power
   * </ul>
   */
  boolean checkAction(@Nonnull FarmingAction action, @Nonnull IFarmingTool tool);

  /**
   * Notifies the farm that the given action has been performed. This will use power but not damage the tool.
   */
  void registerAction(@Nonnull FarmingAction action, @Nonnull IFarmingTool tool);

  /**
   * Notifies the farm that the given action has been performed. This will use power and damage the tool.
   */
  void registerAction(@Nonnull FarmingAction action, @Nonnull IFarmingTool tool, @Nonnull IBlockState state, @Nonnull BlockPos pos);

  /**
   * Checks if the Farming Station has seeds of the given type for the given loaction.
   * 
   * @param seeds
   *          The template to check for.
   * @param pos
   *          The location to check for.
   * @return True if the seed slot for the given location has an itemstack of the given type.
   */
  boolean hasSeed(@Nonnull ItemStack seeds, @Nonnull BlockPos pos);

  /**
   * Tills the given block. Handles everything for you.
   * 
   * @param pos
   *          The location to till.
   * @return True if the block was tilled or already was farmland.
   */
  boolean tillBlock(@Nonnull BlockPos pos);

  /**
   * Determines if the Farming Station is low on saplings. The lower it is, the higher the return value (max 90). If there are more saplings than the reserve
   * value, the result is negative.
   * 
   * @param pos
   *          The location to check for.
   * @return see above
   */
  int isLowOnSaplings(@Nonnull BlockPos pos);

  /**
   * Check if the slot for the given locations is locked.
   * 
   * @param pos
   *          The location to check for.
   * @return True if it is locked.
   */
  boolean isSlotLocked(@Nonnull BlockPos pos);

  /**
   * @return The size of the farm.
   */
  int getFarmSize();

  /**
   * Adds the given item to the farm's inventory if possible or drops it into the world (at the given location or, if none was given, at the farm's location)
   * otherwise.
   * <p>
   * This is only intended for actions that cannot put them into the result list.
   * 
   * @param stack
   *          The stack to put into the farm.
   * @param drop
   *          The location to drop it if it cannot be inserted. If null, the drop location is the Farming Station.
   */

  void handleExtraItem(@Nonnull ItemStack stack, @Nullable BlockPos drop);

}
