package crazypants.enderio.capacitor;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface ICapacitorDataItem {

  @Nonnull
  ICapacitorData getCapacitorData(@Nonnull ItemStack stack);

}
