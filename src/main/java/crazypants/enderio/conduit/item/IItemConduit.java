package crazypants.enderio.conduit.item;

import cofh.api.transport.IItemDuct;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import crazypants.util.DyeColor;

public interface IItemConduit extends IConduit, IItemDuct, IExtractor {

  IIcon getTextureForInputMode();

  IIcon getTextureForOutputMode();

  IIcon getTextureForInOutMode(boolean inputComponent);

  IIcon getTextureForInOutBackground();

  IIcon getEnderIcon();

  IInventory getExternalInventory(ForgeDirection direction);

  int getMaximumExtracted();

  float getTickTimePerItem();

  void itemsExtracted(int numInserted, int slot);

  void setInputFilter(ForgeDirection dir, ItemFilter filter);

  void setOutputFilter(ForgeDirection dir, ItemFilter filter);

  ItemFilter getInputFilter(ForgeDirection dir);

  ItemFilter getOutputFilter(ForgeDirection dir);

  int getMetaData();

  boolean isExtractionRedstoneConditionMet(ForgeDirection dir);

  boolean isSelfFeedEnabled(ForgeDirection dir);

  void setSelfFeedEnabled(ForgeDirection dir, boolean enabled);

  DyeColor getInputColor(ForgeDirection dir);

  DyeColor getOutputColor(ForgeDirection dir);

  void setInputColor(ForgeDirection dir, DyeColor col);

  void setOutputColor(ForgeDirection dir, DyeColor col);

}
