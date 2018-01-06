package crazypants.enderio.base.handler.darksteel;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IDarkSteelItem {

  int getIngotsRequiredForFullRepair();

  boolean isItemForRepair(@Nonnull ItemStack right);

}
