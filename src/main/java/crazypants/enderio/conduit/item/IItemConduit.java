package crazypants.enderio.conduit.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.transport.IItemDuct;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.conduit.item.filter.IItemFilter;

public interface IItemConduit extends IConduit, IItemDuct, IExtractor {

  IIcon getTextureForInputMode();

  IIcon getTextureForOutputMode();

  IIcon getTextureForInOutMode(boolean inputComponent);

  IIcon getTextureForInOutBackground();

  IIcon getEnderIcon();

  IInventory getExternalInventory(ForgeDirection direction);

  int getMaximumExtracted(ForgeDirection direction);

  float getTickTimePerItem(ForgeDirection direction);

  void itemsExtracted(int numInserted, int slot);
  
  void setInputFilterUpgrade(ForgeDirection dir, ItemStack stack);

  void setOutputFilterUpgrade(ForgeDirection dir, ItemStack stack);

  ItemStack getInputFilterUpgrade(ForgeDirection dir);

  ItemStack getOutputFilterUpgrade(ForgeDirection dir);  

  void setInputFilter(ForgeDirection dir, IItemFilter filter);

  void setOutputFilter(ForgeDirection dir, IItemFilter filter);

  IItemFilter getInputFilter(ForgeDirection dir);

  IItemFilter getOutputFilter(ForgeDirection dir);
  
  void setSpeedUpgrade(ForgeDirection dir, ItemStack upgrade);
  
  ItemStack getSpeedUpgrade(ForgeDirection dir);

  void setFunctionUpgrade(ForgeDirection dir, ItemStack upgrade);

  ItemStack getFunctionUpgrade(ForgeDirection dir);

  boolean hasInventoryPanelUpgrade(ForgeDirection dir);

  int getOutputPriority(ForgeDirection dir);
  
  void setOutputPriority(ForgeDirection dir, int priority);

  int getMetaData();

  boolean isExtractionRedstoneConditionMet(ForgeDirection dir);

  boolean isSelfFeedEnabled(ForgeDirection dir);

  void setSelfFeedEnabled(ForgeDirection dir, boolean enabled);
  
  boolean isRoundRobinEnabled(ForgeDirection dir);
  
  void setRoundRobinEnabled(ForgeDirection dir, boolean enabled);

  DyeColor getInputColor(ForgeDirection dir);

  DyeColor getOutputColor(ForgeDirection dir);

  void setInputColor(ForgeDirection dir, DyeColor col);

  void setOutputColor(ForgeDirection dir, DyeColor col);

}
