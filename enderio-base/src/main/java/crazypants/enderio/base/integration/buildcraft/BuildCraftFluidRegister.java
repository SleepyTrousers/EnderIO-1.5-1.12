package crazypants.enderio.base.integration.buildcraft;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.ICoolant;
import buildcraft.api.fuels.IFuel;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import crazypants.enderio.base.fluid.IFluidRegister;
import crazypants.enderio.base.fluid.FluidFuelRegister.CoolantImpl;
import crazypants.enderio.base.fluid.FluidFuelRegister.FuelImpl;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/*
public class BuildCraftFluidRegister implements IFluidRegister {

  public BuildCraftFluidRegister() throws Exception {
    // Make it go splat in object construction if an older version of
    // build craft is installed
    Class.forName("buildcraft.api.fuels.BuildcraftFuelRegistry");
  }

  @Override
  public void addCoolant(@Nonnull Fluid fluid, float degreesCoolingPerMB) {
    if (BuildcraftFuelRegistry.coolant != null && BuildcraftFuelRegistry.coolant.getCoolant(fluid) == null) {
      BuildcraftFuelRegistry.coolant.addCoolant(fluid, degreesCoolingPerMB);
    }
  }

  @Override
  public IFluidCoolant getCoolant(@Nonnull Fluid fluid) {
    if (BuildcraftFuelRegistry.coolant != null) {
      ICoolant bcCool = BuildcraftFuelRegistry.coolant.getCoolant(fluid);
      if (bcCool != null) {
        return new CoolantBC(bcCool);
      }
    }
    return null;
  }

  @Override
  public IFluidCoolant getCoolant(@Nonnull FluidStack fluid) {
    final Fluid fluid2 = fluid.getFluid();
    if (fluid2 == null) {
      return null;
    }
    return getCoolant(fluid2);
  }

  @Override
  public void addFuel(@Nonnull Fluid fluid, int powerPerCycleRF, int totalBurnTime) {
    if (BuildcraftFuelRegistry.fuel != null && BuildcraftFuelRegistry.fuel.getFuel(fluid) == null) {
      BuildcraftFuelRegistry.fuel.addFuel(fluid, powerPerCycleRF, totalBurnTime);
    }
  }

  @Override
  public IFluidFuel getFuel(@Nonnull Fluid fluid) {
    if (BuildcraftFuelRegistry.fuel != null) {
      IFuel bcFuel = BuildcraftFuelRegistry.fuel.getFuel(fluid);
      if (bcFuel != null) {
        return new FuelBC(bcFuel);
      }
    }
    return null;
  }

  @Override
  public IFluidFuel getFuel(@Nonnull FluidStack fluid) {
    final Fluid fluid2 = fluid.getFluid();
    if (fluid2 == null) {
      return null;
    }
    return getFuel(fluid2);
  }

  private static class FuelBC extends FuelImpl {

    FuelBC(IFuel fuel) {
      super(NullHelper.notnull(fuel.getFluid(), "invalid fuel " + fuel), fuel.getPowerPerCycle(), fuel.getTotalBurningTime());
    }

  }

  private static class CoolantBC extends CoolantImpl {

    CoolantBC(ICoolant coolant) {
      // NB: in the current BC impl the temperature to getDegreesCoolingPerMB is ignored
      super(NullHelper.notnull(coolant.getFluid(), "invalid coolant " + coolant), coolant.getDegreesCoolingPerMB(100));
    }

  }
}
*/