package crazypants.enderio.capability;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class LegacyKillerJoeWrapper extends LegacyMachineWrapper {

  public LegacyKillerJoeWrapper(@Nonnull TileKillerJoe machine, @Nonnull EnumFacing side) {
    super(machine, side);
  }

  @Override
  public int getSlots() {
    final IoMode ioMode = machine.getIoMode(side);
    if (ioMode.canRecieveInput() || ioMode.canOutput()) {
      return machine.getSlotDefinition().getNumInputSlots();
    }
    return 0;
  }

  @Override
  protected int extSlot2intSlot(int external) {
    if (external >= 0) {
      final IoMode ioMode = machine.getIoMode(side);
      if (ioMode.canRecieveInput() || ioMode.canOutput()) {
        int num = machine.getSlotDefinition().getNumInputSlots();
        if (external < num) {
          return external + machine.getSlotDefinition().getMinInputSlot();
        }
      }
    }
    return -1;
  }

  @Override
  public @Nonnull ItemStack extractItem(int external, int amount, boolean simulate) {
    if (amount <= 0 || !machine.getIoMode(side).canOutput())
      return Prep.getEmpty();

    int slot = extSlot2intSlot(external);
    if (slot < 0) {
      return Prep.getEmpty();
    }

    return doExtractItem(slot, amount, simulate);
  }

}
