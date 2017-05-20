package crazypants.enderio.power;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.machine.AbstractMachineEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PowerDistributor implements Callback<EnumFacing> {

  private final @Nonnull List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
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

    int transmitted = 0;
    checkReceptors(world);
    if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && available > 0 && appliedCount < numReceptors) {
      Receptor receptor = receptorIterator.next();
      IPowerInterface pp = receptor.receptor;
      int used = pp.receiveEnergy(available, false);
      transmitted += used;
      available -= used;
      if (available <= 0) {
        break;
      }

      if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
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
      IPowerInterface pi = PowerHandlerUtil.getPowerInterface(transmitter.getWorld().getTileEntity(bc.offset(dir)), dir.getOpposite());
      if (pi != null) {
        receptors.add(new Receptor(pi, dir));
      }
    }
  }

  static class Receptor {
    @Nonnull
    IPowerInterface receptor;
    @Nonnull
    EnumFacing fromDir;

    private Receptor(@Nonnull IPowerInterface rec, @Nonnull EnumFacing fromDir) {
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

}
