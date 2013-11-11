package crazypants.enderio.conduit.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;

public interface IItemConduit extends IConduit {

  Icon getTextureForInputMode();

  Icon getTextureForOutputMode();

  Icon getTextureForInOutMode();

  Icon getEnderIcon();

  IInventory getExternalInventory(ForgeDirection direction);

  int getMaximumExtracted(int slot);

  void itemsExtracted(int numInserted, int slot);

}
