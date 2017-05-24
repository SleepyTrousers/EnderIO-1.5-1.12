package crazypants.enderio.item.darksteel;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IDarkSteelItem {

  int getIngotsRequiredForFullRepair();

  @Nonnull
  String getItemName();

  boolean isItemForRepair(@Nonnull ItemStack right);

}
