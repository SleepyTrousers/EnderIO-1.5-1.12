package crazypants.enderio.item.darksteel.upgrade;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.item.IHasPlayerRenderer;
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
