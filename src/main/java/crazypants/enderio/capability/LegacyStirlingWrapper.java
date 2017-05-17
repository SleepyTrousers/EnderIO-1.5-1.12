package crazypants.enderio.capability;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;

public class LegacyStirlingWrapper extends LegacyMachineWrapper {

  public LegacyStirlingWrapper(@Nonnull TileEntityStirlingGenerator machine, @Nonnull EnumFacing side) {
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
      return 1;
    }
    return 0;
  }

  @Override
  protected int extSlot2intSlot(int external) {
    final IoMode ioMode = machine.getIoMode(side);
    if (external == 0 && ioMode.canRecieveInput() || (ioMode.canOutput() && hasBucket())) {
      return machine.getSlotDefinition().getMinInputSlot();
    }
    return -1;
  }

  @Override
  public @Nonnull ItemStack extractItem(int external, int amount, boolean simulate) {
    if (amount > 0 && external == 0 && machine.getIoMode(side).canOutput() && hasBucket()) {
      return doExtractItem(machine.getSlotDefinition().getMinInputSlot(), amount, simulate);
    }
    return Prep.getEmpty();
  }

}
