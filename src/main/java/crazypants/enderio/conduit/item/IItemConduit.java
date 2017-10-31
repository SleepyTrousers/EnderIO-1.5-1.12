package crazypants.enderio.conduit.item;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public interface IItemConduit extends IExtractor {

  TextureAtlasSprite getTextureForInputMode();

  TextureAtlasSprite getTextureForOutputMode();

  TextureAtlasSprite getTextureForInOutMode(boolean inputComponent);

  TextureAtlasSprite getTextureForInOutBackground();

  TextureAtlasSprite getEnderIcon();

  IItemHandler getExternalInventory(EnumFacing direction);

  int getMaximumExtracted(EnumFacing direction);

  float getTickTimePerItem(EnumFacing direction);

  void itemsExtracted(int numInserted, int slot);
  
  void setInputFilterUpgrade(EnumFacing dir, ItemStack stack);

  void setOutputFilterUpgrade(EnumFacing dir, ItemStack stack);

  ItemStack getInputFilterUpgrade(EnumFacing dir);

  ItemStack getOutputFilterUpgrade(EnumFacing dir);  

  void setInputFilter(EnumFacing dir, IItemFilter filter);

  void setOutputFilter(EnumFacing dir, IItemFilter filter);

  IItemFilter getInputFilter(EnumFacing dir);

  IItemFilter getOutputFilter(EnumFacing dir);
  
  void setSpeedUpgrade(EnumFacing dir, ItemStack upgrade);
  
  ItemStack getSpeedUpgrade(EnumFacing dir);

  void setFunctionUpgrade(EnumFacing dir, ItemStack upgrade);

  ItemStack getFunctionUpgrade(EnumFacing dir);

  boolean hasInventoryPanelUpgrade(EnumFacing dir);

  int getOutputPriority(EnumFacing dir);
  
  void setOutputPriority(EnumFacing dir, int priority);

  int getMetaData();

  boolean isExtractionRedstoneConditionMet(EnumFacing dir);

  boolean isSelfFeedEnabled(EnumFacing dir);

  void setSelfFeedEnabled(EnumFacing dir, boolean enabled);
  
  boolean isRoundRobinEnabled(EnumFacing dir);
  
  void setRoundRobinEnabled(EnumFacing dir, boolean enabled);

  DyeColor getInputColor(EnumFacing dir);

  DyeColor getOutputColor(EnumFacing dir);

  void setInputColor(EnumFacing dir, DyeColor col);

  void setOutputColor(EnumFacing dir, DyeColor col);

  boolean isConnectedToNetworkAwareBlock(EnumFacing dir);

}
