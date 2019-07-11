package crazypants.enderio.conduits.capability;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * 
 * 
 */
public interface IUpgradeHolder {

  @Nonnull
  ItemStack getUpgradeStack(int param1);

  void setUpgradeStack(int param1, @Nonnull ItemStack stack);

  /**
   * Defaults to <code>true</code>.
   *
   * @param stack
   *          Is an instance of ItemFunctionUpgrade. Passed in case more detailed checking wants to be performed by the conduit.
   * @return <code>true</code> if this conduit can accept the given ItemStack as a function upgrade.
   */
  default boolean isFunctionUpgradeAccepted(@Nonnull ItemStack stack) {
    return true;
  }

  default int getUpgradeSlotLimit() {
    return 15;
  }

  default int getUpgradeSlotLimit(@Nonnull ItemStack stack) {
    return getUpgradeSlotLimit();
  }

  default @Nonnull List<String> getFunctionUpgradeToolTipText(@Nonnull EnumFacing dir) {
    return Collections.emptyList();
  }

}
