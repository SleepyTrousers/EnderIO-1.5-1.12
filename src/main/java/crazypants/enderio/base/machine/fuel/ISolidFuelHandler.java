package crazypants.enderio.base.machine.fuel;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface ISolidFuelHandler {

  /**
   * True if the current GUI belongs to this handler.
   */
  default boolean isInGUI() {
    return false;
  }

  default int getPowerUsePerTick() {
    return 0;
  }

  long getBurnTime(@Nonnull ItemStack itemstack);

  public static interface Provider {

    @Nonnull
    ISolidFuelHandler getSolidFuelHandler();

  }

}
