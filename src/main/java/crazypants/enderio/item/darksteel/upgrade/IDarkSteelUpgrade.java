package crazypants.enderio.item.darksteel.upgrade;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.item.IHasPlayerRenderer;
import net.minecraft.item.ItemStack;

public interface IDarkSteelUpgrade extends IAdvancedTooltipProvider, IHasPlayerRenderer {

  String getUnlocalizedName();

  int getLevelCost();

  boolean isUpgradeItem(ItemStack stack);

  boolean canAddToItem(ItemStack stack);

  boolean hasUpgrade(ItemStack stack);

  void writeToItem(ItemStack stack);

  void removeFromItem(ItemStack stack);

  ItemStack getUpgradeItem();

  String getUpgradeItemName();

}
