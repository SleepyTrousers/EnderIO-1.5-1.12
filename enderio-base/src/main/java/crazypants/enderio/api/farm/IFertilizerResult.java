package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * The result of a fertilizing action, see {@link IFertilizer}.
 * 
 * @author Henry Loenwind
 *
 */
public interface IFertilizerResult {

  /**
   * @return The remaining fertilizer itemStack. Will be ignored if {@link #wasApplied()} is false.
   */
  @Nonnull
  ItemStack getStack();

  /**
   * @return true if the action was successful. In this case, the stack from {@link #getStack()} will return the stack in the fertilizer slot and the Farming
   *         Station will use energy and set its delays accordingly.
   */
  boolean wasApplied();

}