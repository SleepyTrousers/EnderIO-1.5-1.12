package crazypants.enderio.machine.fuel;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface ISolidFuelHandler {

  /**
   * True if the current GUI belongs to this handler.
   */
  boolean isInGUI();

  int getPowerUsePerTick();

  long getBurnTime(@Nonnull ItemStack itemstack);

  public static interface Provider {

    @Nonnull
    ISolidFuelHandler getSolidFuelHandler();

  }

}
