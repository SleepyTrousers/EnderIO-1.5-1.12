package crazypants.enderio.machines.machine.basin;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredTaskEntity;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.basin.BasinRecipe;
import crazypants.enderio.base.recipe.basin.BasinRecipeManager;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class TileBasin extends AbstractCapabilityPoweredTaskEntity implements ITankAccess {
  
  private class BasinTank extends SmartTank {
    
    private final Plane orientation;
    private final int index;
    
    BasinTank(Plane orientation, int index) {
      super(4000);
      this.orientation = orientation;
      this.index = index;
      setCanDrain(false);
      setTileEntity(TileBasin.this);
    }
    
    @Override
    public boolean canFillFluidType(@Nullable FluidStack resource) {
      if (resource == null) {
        return false;
      }
      BasinRecipe recipe = BasinRecipeManager.getInstance().getRecipeForInput(resource, orientation);
      if (recipe != null) {
        if (orientation == Plane.HORIZONTAL) {
          return true;
        } else {
          return recipe.getInputFluidStacks().get(index).getFluid() == resource.getFluid();
        }
      }
      return false;
    }
    
  }
  
  @Nonnull
  @Store
  final SmartTank tankU, tankD, tankL, tankR;

  public TileBasin() {
    super(null, CapacitorKey.BASIN_POWER_INTAKE, CapacitorKey.BASIN_POWER_BUFFER, CapacitorKey.BASIN_POWER_USE);
    getInventory().add(Type.OUTPUT, "OUTPUT", new InventorySlot());
    tankU = new BasinTank(Plane.VERTICAL, 0);
    tankD = new BasinTank(Plane.VERTICAL, 1);
    tankL = new BasinTank(Plane.HORIZONTAL, 0);
    tankR = new BasinTank(Plane.HORIZONTAL, 1);
  }
  
  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facingIn) {
    return capability == FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
  }
  
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == FLUID_HANDLER_CAPABILITY) {
      SmartTankFluidHandler ret;
      if (facingIn == EnumFacing.UP) {
        ret = new SmartTankFluidMachineHandler(this, tankU);
      } else if (facingIn == EnumFacing.DOWN) {
        ret = new SmartTankFluidMachineHandler(this, tankD);
      } else {
        ret = new SmartTankFluidMachineHandler(this, tankL, tankR);
      }
      return FLUID_HANDLER_CAPABILITY.cast(ret.get(facingIn));
    }
    return super.getCapability(capability, facingIn);
  }
  
  @Override
  protected boolean hasInputStacks() {
    return true;
  }
  
  @Override
  @Nonnull
  protected NNList<MachineRecipeInput> getRecipeInputs() {
    if (tankU.isEmpty() && tankD.isEmpty()) {
      return new NNList<>(new MachineRecipeInput(0, tankL.getFluid()), new MachineRecipeInput(1, tankR.getFluid()));
    } else {
      return new NNList<>(new MachineRecipeInput(0, tankU.getFluid()), new MachineRecipeInput(1, tankD.getFluid()));
    }
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return "basin";
  }

  @Override
  @Nullable
  public FluidTank getInputTank(FluidStack forFluidType) {
    BasinRecipe recipe = BasinRecipeManager.getInstance().getRecipeForInput(forFluidType);
    if (recipe != null) {
      if (recipe.getOrientation() == Plane.HORIZONTAL) {
        return tankU.isEmpty() || (!tankU.isFull() && tankU.getFluidNN().getFluid() == forFluidType.getFluid()) ? tankU : tankD;
      } else {
        return tankL.isEmpty() || (!tankL.isFull() && tankL.getFluidNN().getFluid() == forFluidType.getFluid()) ? tankL : tankR;
      }
    }
    return null;
  }

  @Override
  @Nonnull
  public FluidTank[] getOutputTanks() {
    return new FluidTank[0];
  }

  @Override
  public void setTanksDirty() {
    markDirty();
  }
}
