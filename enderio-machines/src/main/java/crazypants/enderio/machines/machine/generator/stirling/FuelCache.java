package crazypants.enderio.machines.machine.generator.stirling;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.integration.jei.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

/**
 * A cache of all items that have a burn time in the vanilla furnace.
 * <p>
 * Creating a list of all items and then getting the burn time for all of them can take a very long time---it involves many items and sub-items and throws an
 * event for every single one. This cache makes sure that that is only done one.
 * <p>
 * {@link #initialize(Collection)} can take a list of all items that has already been prepared by e.g. JEI. When it is not used, the first access to
 * {@link #getFuels()} will trigger our own item collection code.
 *
 */
public class FuelCache {

  private static final NNList<ItemStack> FUELS = new NNList<>();

  public static void initialize(@Nonnull Collection<ItemStack> stacks) {
    if (FUELS.isEmpty()) {
      for (ItemStack stack : stacks) {
        if (stack != null && TileEntityFurnace.isItemFuel(stack)) {
          FUELS.add(stack);
        }
      }
    }
  }

  /**
   * 
   * @return A list of all items that have a burn time. (Or at least had one the first time the cache was initialized. Mods are free to randomly change the burn
   *         time of items at any time.)
   */
  public static NNList<ItemStack> getFuels() {
    if (FUELS.isEmpty()) {
      initialize(ItemHelper.getValidItems());
    }
    return FUELS;
  }

}
