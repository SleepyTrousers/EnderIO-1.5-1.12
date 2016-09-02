package crazypants.enderio.item.darksteel;

import net.minecraft.item.ItemStack;

public interface IDarkSteelItem {

  int getIngotsRequiredForFullRepair();
  
  String getItemName();

  boolean isItemForRepair(ItemStack right);
  
}
