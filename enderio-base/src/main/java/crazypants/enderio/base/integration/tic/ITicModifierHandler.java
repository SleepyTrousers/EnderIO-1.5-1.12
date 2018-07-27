package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface ITicModifierHandler {

  boolean isBroken(ItemStack itemStack);

  boolean isTinkerItem(@Nonnull ItemStack itemStack);

  int getBehadingLevel(@Nonnull ItemStack itemStack);

}