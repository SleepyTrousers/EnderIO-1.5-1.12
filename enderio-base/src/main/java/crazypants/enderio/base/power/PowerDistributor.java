package crazypants.enderio.base.power;

import java.util.ListIterator;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerDistributor implements Callback<EnumFacing> {

  private final @Nonnull NNList<IEnergyStorage> receptors = new NNList<>();
  private ListIterator<IEnergyStorage> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private final @Nonnull BlockPos bc;
  private TileEntity transmitter;

  public PowerDistributor(@Nonnull BlockPos bc) {
    this.bc = bc;
  }

  public void neighboursChanged() {
    receptorsDirty = true;
  }

  public int transmitEnergy(@Nonnull World world, int available) {
    checkReceptors(world);
    if (receptors.isEmpty()) {
      return 0;
    }

    int transmitted = 0;
    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (available > 0 && appliedCount < numReceptors) {
      if (!receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }

      int used = receptorIterator.next().receiveEnergy(available, false);
      transmitted += used;
      available -= used;
      if (available <= 0) {
        break;
      }
      appliedCount++;
    }
    return transmitted;
  }

  private void checkReceptors(@Nonnull World world) {
    if (!receptorsDirty) {
      return;
    }
    receptors.clear();
    transmitter = world.getTileEntity(bc);
    NNList.FACING.apply(this);
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;
  }

  @Override
  public void apply(@Nonnull EnumFacing dir) {
    if (!(transmitter instanceof AbstractMachineEntity) || ((AbstractMachineEntity) transmitter).getIoMode(dir).canOutput()) {
      receptors.addIf(PowerHandlerUtil.getCapability(transmitter.getWorld().getTileEntity(bc.offset(dir)), dir.getOpposite()));
    }
  }

}
