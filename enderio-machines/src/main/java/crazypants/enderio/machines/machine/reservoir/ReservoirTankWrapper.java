package crazypants.enderio.machines.machine.reservoir;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

class ReservoirTankWrapper implements ITankAccess {

  private final @Nonnull ITankAccess owner;
  private final @Nonnull NNList<ITankAccess> parents = new NNList<>();
  private SmartTank tank;
  private final @Nonnull World world;
  private final @Nonnull BlockPos pos;
  private final boolean allowFluidVoiding;

  ReservoirTankWrapper(@Nonnull ITankAccess parent, @Nonnull World world, @Nonnull BlockPos pos, boolean allowFluidVoiding) {
    this.owner = parent;
    this.world = world;
    this.pos = pos;
    this.allowFluidVoiding = allowFluidVoiding;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    FluidTank ownerTank = owner.getInputTank(forFluidType);
    if (ownerTank == null) {
      return null;
    }
    parents.clear();
    int free = ownerTank.getCapacity() - ownerTank.getFluidAmount();
    if (free > 0) {
      parents.add(owner);
    }
    for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
      TileEntity neighbor = world.getTileEntity(pos.offset(itr.next()));
      if (neighbor instanceof ITankAccess) {
        FluidTank tank2 = ((ITankAccess) neighbor).getInputTank(forFluidType);
        if (tank2 != null) {
          free += tank2.getCapacity() - tank2.getFluidAmount();
          parents.add(((ITankAccess) neighbor));
        }
      }
    }
    if (allowFluidVoiding && free < Fluid.BUCKET_VOLUME) {
      free = Fluid.BUCKET_VOLUME;
    }
    final FluidStack fluid = ownerTank.getFluid();
    return tank = new SmartTank(fluid != null ? fluid.getFluid() : null, free);
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return owner.getOutputTanks();
  }

  @Override
  public void setTanksDirty() {
    if (tank != null) {
      FluidStack stack = tank.getFluid();
      if (stack != null && stack.amount > 0) {
        for (ITankAccess parent : parents) {
          FluidTank ptank = parent.getInputTank(stack);
          if (ptank != null) {
            stack.amount -= ptank.fill(stack, true);
            parent.setTanksDirty();
            if (stack.amount <= 0) {
              return;
            }
          }
        }
      }
      tank.setCapacity(0);
      parents.clear();
    }
  }

}