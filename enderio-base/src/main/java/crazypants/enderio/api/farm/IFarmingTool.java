package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * This represent different types of tools the Farming Station can handle.
 * <p>
 * Please note that the given list is final, no new object implementing this interface may be used.
 * 
 * @author Henry Loenwind
 *
 */
public interface IFarmingTool {

  public static class Tools {
    /**
     * These are all tools the Farming Station can handle. The fields will be filled at runtime.
     */
    public static IFarmingTool HAND = null, HOE = null, AXE = null, TREETAP = null, SHEARS = null, NONE = null;
  }

  /**
   * @return True if the given item matche steh tool type.
   */
  boolean itemMatches(@Nonnull ItemStack item);

}