package crazypants.enderio.conduits.capability;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * 
 * 
 */
public interface IUpgradeHolder {

  @Nonnull
  ItemStack getUpgradeStack(int param1);

  void setUpgradeStack(int param1, @Nonnull ItemStack stack);

}
