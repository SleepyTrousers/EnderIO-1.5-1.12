package crazypants.enderio.machines.machine.basin;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredTaskEntity;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.basin.BasinMachineRecipe;
import crazypants.enderio.base.recipe.basin.BasinRecipe;
import crazypants.enderio.base.recipe.basin.BasinRecipeManager;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TileBasin extends AbstractCapabilityPoweredTaskEntity implements ITankAccess {
  
  private class BasinTank extends SmartTank {
    
    private final Plane orientation;
    private final int index;
    
    BasinTank(Plane orientation, int index) {
      super(1000);
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
      NNList<BasinRecipe> recipes = BasinRecipeManager.getInstance().getRecipesForInput(resource, orientation);
      return recipes.stream()
          .anyMatch(recipe -> recipe.getInputFluidStacks().get(index).getFluid() == resource.getFluid());
    }
    
  }
  
  public enum Slots {
    OUTPUT,
    TOOL
  }
  
  @Nonnull
  @Store
  final SmartTank tankU, tankD, tankL, tankR;
  
  // Clientside rendering information
  Plane orientation;
  FluidStack inputA, inputB;

  public TileBasin() {
    super(null, CapacitorKey.BASIN_POWER_INTAKE, CapacitorKey.BASIN_POWER_BUFFER, CapacitorKey.BASIN_POWER_USE);
    getInventory().add(Type.OUTPUT, Slots.OUTPUT, new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE));
    getInventory().add(Type.INPUT, Slots.TOOL, new InventorySlot());
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
    return new NNList<>(
        new MachineRecipeInput(0, tankU.getFluid()), new MachineRecipeInput(1, tankD.getFluid()),
        new MachineRecipeInput(2, tankL.getFluid()), new MachineRecipeInput(3, tankR.getFluid()));
  }
  
  @Override
  protected void drainInputFluid(@Nonnull MachineRecipeInput fluid) {
    super.drainInputFluid(fluid);
    switch (fluid.slotNumber) {
    case 0:
      drainInputFluid(fluid.fluid, tankU);
      break;
    case 1:
      drainInputFluid(fluid.fluid, tankD);
      break;
    case 2:
      drainInputFluid(fluid.fluid, tankL);
      break;
    case 3:
      drainInputFluid(fluid.fluid, tankR);
      break;
    default: break;
    }
  }
  
  private void drainInputFluid(@Nullable FluidStack stack, @Nonnull SmartTank tank) {
    if (stack != null) {
      tank.removeFluidAmount(stack.amount);
    }
  }
  
  @Override
  protected void taskComplete() {
    super.taskComplete();
    
    getInventory().getSlot(Slots.TOOL).get().damageItem(1, FakePlayerFactory.getMinecraft((WorldServer) getWorld()));
  }
  
  @Override
  public @Nonnull IMessage getProgressPacket() {
    return new PacketBasinProgress(this);
  }
  
  @Override
  @Nullable
  protected IPoweredTask createTask(@Nonnull IMachineRecipe nextRecipe, long nextSeed) {
    IPoweredTask task = super.createTask(nextRecipe, nextSeed);
    if (task != null) {
      // PoweredTask filters out inputs that have no item or fluid, we need to keep those for our recipe checking semantics
      task.getInputs().clear();
      task.getInputs().addAll(getRecipeInputs());
    }
    return task;
  }
  
  void setClientTask(IPoweredTask currentTask) {
    this.currentTask = currentTask;
  }
  
  @Override
  @Nullable
  protected IMachineRecipe canStartNextTask(long nextSeed) {
    IMachineRecipe ret = super.canStartNextTask(nextSeed);
    if (ret != null) {
      IRecipe recipe = ((BasinMachineRecipe)ret).getRecipeForInputs(getRecipeInputs());
      if (recipe != null) {
        if (((BasinRecipe)recipe).getOrientation() == Plane.VERTICAL) {
          return tankU.isFull() && tankD.isFull() ? ret : null;
        } else {
          return tankL.isFull() && tankR.isFull() ? ret : null;
        }
      }
    }
    return ret;
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return "basin";
  }

  @Override
  @Nullable
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType == null) {
      return null;
    }
    NNList<BasinRecipe> recipes;
    Plane orientation = null;
    if (!tankU.isEmpty() || !tankD.isEmpty()) {
      orientation = Plane.VERTICAL;
    } else if (!tankL.isEmpty() || !tankR.isEmpty()) {
      orientation = Plane.HORIZONTAL;
    }
    if (orientation == null) {
      recipes = BasinRecipeManager.getInstance().getRecipesForInput(forFluidType);
    } else {
      recipes = BasinRecipeManager.getInstance().getRecipesForInput(forFluidType, orientation);
    }
    if (!recipes.isEmpty()) {
      SmartTank tank;
      if (recipes.get(0).getOrientation() == Plane.VERTICAL) {
        tank = tankU.isEmpty() || (!tankU.isFull() && tankU.getFluidNN().getFluid() == forFluidType.getFluid()) ? tankU : tankD;
      } else {
        tank = tankL.isEmpty() || (!tankL.isFull() && tankL.getFluidNN().getFluid() == forFluidType.getFluid()) ? tankL : tankR;
      }
      if (tank.canFillFluidType(forFluidType)) {
        return tank;
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
