package crazypants.enderio.capability;

import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class LegacyKillerJoeWrapper extends LegacyMachineWrapper {

  public LegacyKillerJoeWrapper(TileKillerJoe machine, EnumFacing side) {
    super(machine, side);
  }

  @Override
  public int getSlots() {
    int result = 0;
    final IoMode ioMode = machine.getIoMode(side);
    if (ioMode.canRecieveInput() || ioMode.canOutput()) {
      result += machine.getSlotDefinition().getNumInputSlots();
    }
    return result;
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
  public ItemStack extractItem(int external, int amount, boolean simulate) {
    if (amount <= 0 || !machine.getIoMode(side).canOutput())
      return Prep.getEmpty();

    int slot = extSlot2intSlot(external);
    if (slot < 0) {
      return Prep.getEmpty();
    }

    return doExtractItem(slot, amount, simulate);
  }

}
