package crazypants.enderio.machines.capability;

import javax.annotation.Nonnull;

import crazypants.enderio.base.capability.LegacyMachineWrapper;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.machines.machine.generator.stirling.TileStirlingGenerator;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;

/**
 * Allows empty containers to be extracted from the input slot
 *
 */
public class LegacyStirlingWrapper extends LegacyMachineWrapper {

  public LegacyStirlingWrapper(@Nonnull TileStirlingGenerator machine, @Nonnull EnumFacing side) {
    super(machine, side);
  }

  private boolean hasBucket() {
    ItemStack stack = machine.getStackInSlot(machine.getSlotDefinition().getMinInputSlot());
    return Prep.isValid(stack) && !TileEntityFurnace.isItemFuel(stack);
  }

  @Override
  public int getSlots() {
    final IoMode ioMode = machine.getIoMode(side);
    if (ioMode.canRecieveInput() || (ioMode.canOutput() && hasBucket())) {
      return machine.getSlotDefinition().getNumInputSlots();
    }
    return 0;
  }

  @Override
  public @Nonnull ItemStack extractItem(int external, int amount, boolean simulate) {
    if (amount > 0 && external == 0 && machine.getIoMode(side).canOutput() && hasBucket()) {
      return doExtractItem(machine.getSlotDefinition().getMinInputSlot(), amount, simulate);
    }
    return Prep.getEmpty();
  }

}
