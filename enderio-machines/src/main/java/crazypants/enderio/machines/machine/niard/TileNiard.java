package crazypants.enderio.machines.machine.niard;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class TileNiard extends AbstractPoweredTaskEntity {

  protected TileNiard(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    // TODO Auto-generated constructor stub
  }

  @Override
  @Nonnull
  public String getMachineName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    // TODO Auto-generated method stub
    return false;
  }

  public BlockPos offset() {
    // TODO Auto-generated method stub
    return null;
  }

}
