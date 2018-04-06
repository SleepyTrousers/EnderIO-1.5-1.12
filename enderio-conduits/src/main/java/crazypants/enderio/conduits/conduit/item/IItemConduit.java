package crazypants.enderio.conduits.conduit.item;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IExtractor;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.filter.item.IItemFilter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public interface IItemConduit extends IExtractor, IServerConduit, IClientConduit {

  // Textures
  TextureAtlasSprite getTextureForInputMode();

  TextureAtlasSprite getTextureForOutputMode();

  TextureAtlasSprite getTextureForInOutMode(boolean inputComponent);

  TextureAtlasSprite getTextureForInOutBackground();

  TextureAtlasSprite getEnderIcon();

  IItemHandler getExternalInventory(@Nonnull EnumFacing direction);

  int getMaximumExtracted(@Nonnull EnumFacing direction);

  float getTickTimePerItem(@Nonnull EnumFacing direction);

  void itemsExtracted(int numInserted, int slot);

  void setInputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack);

  void setOutputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack);

  @Nonnull
  ItemStack getInputFilterUpgrade(@Nonnull EnumFacing dir);

  @Nonnull
  ItemStack getOutputFilterUpgrade(@Nonnull EnumFacing dir);

  void setInputFilter(@Nonnull EnumFacing dir, @Nonnull IItemFilter filter);

  void setOutputFilter(@Nonnull EnumFacing dir, @Nonnull IItemFilter filter);

  IItemFilter getInputFilter(@Nonnull EnumFacing dir);

  IItemFilter getOutputFilter(@Nonnull EnumFacing dir);

  void setFunctionUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack upgrade);

  @Nonnull
  ItemStack getFunctionUpgrade(@Nonnull EnumFacing dir);

  int getOutputPriority(@Nonnull EnumFacing dir);

  void setOutputPriority(@Nonnull EnumFacing dir, int priority);

  int getMetaData();

  boolean isExtractionRedstoneConditionMet(@Nonnull EnumFacing dir);

  boolean isSelfFeedEnabled(@Nonnull EnumFacing dir);

  void setSelfFeedEnabled(@Nonnull EnumFacing dir, boolean enabled);

  boolean isRoundRobinEnabled(@Nonnull EnumFacing dir);

  void setRoundRobinEnabled(@Nonnull EnumFacing dir, boolean enabled);

  DyeColor getInputColor(@Nonnull EnumFacing dir);

  DyeColor getOutputColor(@Nonnull EnumFacing dir);

  void setInputColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col);

  void setOutputColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col);

  // TODO Inventory
  // boolean isConnectedToNetworkAwareBlock(@Nonnull EnumFacing dir);
  //
  // boolean hasInventoryPanelUpgrade(@Nonnull EnumFacing dir);
  //
  // void setFunctionUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack upgrade);
  //
  // @Nonnull
  // ItemStack getFunctionUpgrade(@Nonnull EnumFacing dir);

}
