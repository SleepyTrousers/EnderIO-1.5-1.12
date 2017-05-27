package crazypants.enderio.farming;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.machine.fakeplayer.FakePlayerEIO;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFarmer {

  @Nonnull
  ItemStack getSeedTypeInSuppliesFor(@Nonnull BlockPos pos);

  @Nonnull
  FakePlayerEIO getFakePlayer();

  @Nonnull
  World getWorld();

  @Nonnull
  ItemStack takeSeedFromSupplies(@Nonnull BlockPos pos);

  boolean hasTool(@Nonnull FarmingTool tool);

  void setNotification(@Nonnull FarmNotification notification);

  int getLootingValue(@Nonnull FarmingTool tool);

  @Nonnull
  IBlockState getBlockState(@Nonnull BlockPos pos);

  @Nonnull
  ItemStack getTool(@Nonnull FarmingTool tool);

  /**
   * Prepares the fake player to be used for interacting with the world using the given item in the main hand.
   */
  FakePlayerEIO startUsingItem(@Nonnull ItemStack stack);

  /**
   * Prepares the fake player to be used for interacting with the world using the given tool in the main hand.
   */
  FakePlayerEIO startUsingItem(@Nonnull FarmingTool tool);

  /**
   * Reverts the fake player back into its dormant state and returns all items it collected in the meantime.
   * 
   * @param trashHandItem
   *          If true then the main hand item (the one that was set with startUsingItem()) is trashed and not returned.
   * @return A list of items that were in the fake player's inventory.
   */
  @Nonnull
  NNList<ItemStack> endUsingItem(boolean trashHandItem);

  /**
   * Reverts the fake player back into its dormant state and returns all items it collected in the meantime.
   * 
   * @param FarmingTool
   *          Where the main hand item (the one that was set with startUsingItem()) should be returned to.
   * @return A list of items that were in the fake player's inventory.
   */
  @Nonnull
  NNList<ItemStack> endUsingItem(@Nonnull FarmingTool tool);

  /**
   * Adds the given items to the farm's inventory if possible or drops them into the world (at the given location or, if none was given, at the farm's location)
   * otherwise.
   * <p>
   * This is only intended for actions that cannot put them into the result list.
   * 
   * @param items
   */
  void handleExtraItems(@Nonnull NNList<ItemStack> items, @Nullable BlockPos pos);

  /**
   * Checks if the farm can do the given action. Checks:
   * <ul>
   * <li>That there is a matching tool
   * <li>There is enough power
   * </ul>
   */
  boolean checkAction(@Nonnull FarmingAction action, @Nonnull FarmingTool tool);

  /**
   * Notifies the farm that the given action has been performed. This will use power but not damage the tool.
   */
  void registerAction(@Nonnull FarmingAction action, @Nonnull FarmingTool tool);

  /**
   * Notifies the farm that the given action has been performed. This will use power and damage the tool.
   */
  void registerAction(@Nonnull FarmingAction action, @Nonnull FarmingTool tool, @Nonnull IBlockState state, @Nonnull BlockPos pos);

  boolean hasSeed(@Nonnull ItemStack seeds, @Nonnull BlockPos pos);

  boolean tillBlock(@Nonnull BlockPos pos);

}
