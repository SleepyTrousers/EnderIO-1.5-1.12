package crazypants.enderio.conduit.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.redstone.SignalColor;
import crazypants.enderio.machine.RedstoneControlMode;

public interface IItemConduit extends IConduit, cofh.api.transport.IItemConduit {

  Icon getTextureForInputMode();

  Icon getTextureForOutputMode();

  Icon getTextureForInOutMode(boolean inputComponent);

  Icon getTextureForInOutBackground();

  Icon getEnderIcon();

  IInventory getExternalInventory(ForgeDirection direction);

  int getMaximumExtracted();

  float getTickTimePerItem();

  void itemsExtracted(int numInserted, int slot);

  void setInputFilter(ForgeDirection dir, ItemFilter filter);

  void setOutputFilter(ForgeDirection dir, ItemFilter filter);

  ItemFilter getInputFilter(ForgeDirection dir);

  ItemFilter getOutputFilter(ForgeDirection dir);

  int getMetaData();

  void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir);

  RedstoneControlMode getExtractioRedstoneMode(ForgeDirection dir);

  void setExtractionSignalColor(ForgeDirection dir, SignalColor col);

  SignalColor getExtractionSignalColor(ForgeDirection dir);

  boolean isExtractionRedstoneConditionMet(ForgeDirection dir);

  boolean isSelfFeedEnabled(ForgeDirection dir);

  void setSelfFeedEnabled(ForgeDirection dir, boolean enabled);

  SignalColor getInputColor(ForgeDirection dir);

  SignalColor getOutputColor(ForgeDirection dir);

  void setInputColor(ForgeDirection dir, SignalColor col);

  void setOutputColor(ForgeDirection dir, SignalColor col);

}
