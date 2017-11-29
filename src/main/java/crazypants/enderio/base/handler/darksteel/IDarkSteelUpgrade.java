package crazypants.enderio.base.handler.darksteel;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.base.render.IHasPlayerRenderer;
import net.minecraft.item.ItemStack;

public interface IDarkSteelUpgrade extends IAdvancedTooltipProvider, IHasPlayerRenderer {

  @Nonnull
  String getUnlocalizedName();

  int getLevelCost();

  boolean isUpgradeItem(@Nonnull ItemStack stack);

  boolean canAddToItem(@Nonnull ItemStack stack);

  boolean hasUpgrade(@Nonnull ItemStack stack);

  void writeToItem(@Nonnull ItemStack stack);

  void removeFromItem(@Nonnull ItemStack stack);

  @Nonnull
  ItemStack getUpgradeItem();

  @Nonnull
  String getUpgradeItemName();

}
