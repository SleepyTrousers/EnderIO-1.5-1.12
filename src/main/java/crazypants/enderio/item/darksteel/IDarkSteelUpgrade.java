package crazypants.enderio.item.darksteel;

import net.minecraft.item.ItemStack;
import crazypants.enderio.gui.IAdvancedTooltipProvider;

public interface IDarkSteelUpgrade extends IAdvancedTooltipProvider {

  String getUnlocalizedName();

  int getLevelCost();

  boolean isUpgradeItem(ItemStack stack);

  boolean canAddToItem(ItemStack stack);

  boolean hasUpgrade(ItemStack stack);

  void writeToItem(ItemStack stack);

  void removeFromItem(ItemStack stack);

  ItemStack getUpgradeItem();

}
