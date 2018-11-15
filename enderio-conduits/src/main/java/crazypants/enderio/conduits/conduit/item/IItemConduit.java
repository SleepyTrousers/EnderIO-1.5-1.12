package crazypants.enderio.conduits.conduit.item;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.IExtractor;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.conduits.conduit.IEnderConduit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public interface IItemConduit extends IExtractor, IServerConduit, IClientConduit, IEnderConduit {

  // Textures
  IConduitTexture getEnderIcon();

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

  int getMetaData();

  boolean isExtractionRedstoneConditionMet(@Nonnull EnumFacing dir);

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
