package crazypants.enderio.base.filter.fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.filter.IFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidFilter extends IFilter {

  /**
   * Checks if the filter has no fluid stacks
   * 
   * @return true if the filter has no fluids
   */
  boolean isEmpty();

  /**
   * Gets the number of fluids in the filter
   * 
   * @return The number of fluids in the filter
   */
  int size();

  /**
   * Gets the fluid stack at the given index
   * 
   * @param index
   *          The index of the fluid
   * @return FluidStack at the given index, null if there is none
   */
  @Nullable
  FluidStack getFluidStackAt(int index);

  /**
   * Sets the fluid in the given slot
   * 
   * @param index
   *          Index of the slot
   * @param fluid
   *          Fluid to insert. Fluid can be null to make the slot empty
   * @return true if the fluid was successfully set
   */
  boolean setFluid(int index, @Nullable FluidStack fluid);

  /**
   * Sets the fluid from the ItemStack
   * 
   * @param index
   *          Index of the fluid filter
   * @param stack
   *          The ItemStack to get the fluid from
   * @return true if the fluid is successfully set
   */
  boolean setFluid(int index, @Nonnull ItemStack stack);

  /**
   * Removes a fluid at the given index
   * 
   * @param index
   *          Index of the fluid to remove
   * @return true if the fluid is successfully removed
   */
  boolean removeFluid(int index);

  /**
   * Checks the whitelist/blacklist setting of the filter
   * 
   * @return true if the blacklist is active
   */
  boolean isBlacklist();

  /**
   * Sets the blacklist/whitelist button
   * 
   * @param isBlacklist
   *          true if it should be a blacklist, false for whitelist
   */
  void setBlacklist(boolean isBlacklist);

  /**
   * Checks if the filter matches the default filter setting
   * 
   * @return true if the filter has no different settings to a freshly made one
   */
  boolean isDefault();

  /**
   * Checks if the fluid matches the filter
   * 
   * @param drained
   *          Fluid to check
   * @return true if it matches the filter settings
   */
  boolean matchesFilter(FluidStack drained);
}
