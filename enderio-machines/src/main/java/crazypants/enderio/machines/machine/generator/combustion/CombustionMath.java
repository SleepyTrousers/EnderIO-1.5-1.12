package crazypants.enderio.machines.machine.generator.combustion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.fluid.SmartTank;

import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CombustionMath {

  public static final double HEAT_PER_RF = 0.00023F / 2f;

  private final int ticksPerCoolant;
  private final int ticksPerFuel;
  private final int energyPerTick;

  public CombustionMath(@Nullable IFluidCoolant coolant, @Nullable IFluidFuel fuel, float capQuality, float machineQuality) {
    if (coolant == null || fuel == null || capQuality == 0 || machineQuality == 0) {
      ticksPerCoolant = ticksPerFuel = energyPerTick = 0;
    } else {
      energyPerTick = Math.round(fuel.getPowerPerCycle() * capQuality * machineQuality);

      double cooling = coolant.getDegreesCoolingPerMB(); // heat absorbed per mB
      double toCool = HEAT_PER_RF * energyPerTick * machineQuality; // heat per tick
      ticksPerCoolant = Math.max((int) Math.round(cooling / toCool), 1);

      ticksPerFuel = Math.max((int) (fuel.getTotalBurningTime() / capQuality / 1000), 1);
    }
  }

  public CombustionMath(@Nonnull SmartTank coolant, @Nonnull SmartTank fuel, float capQuality, float machineQuality) {
    this(coolant.getFluid(), fuel.getFluid(), capQuality, machineQuality);
  }

  public CombustionMath(@Nullable FluidStack coolantFluid, @Nullable FluidStack fuelFluid, float capQuality, float machineQuality) {
    this(toCoolant(coolantFluid), toFuel(fuelFluid), capQuality, machineQuality);
  }

  public static IFluidFuel toFuel(@Nonnull SmartTank fuelTank) {
    return toFuel(fuelTank.getFluid());
  }

  public static IFluidFuel toFuel(@Nullable FluidStack fuelFluid) {
    return fuelFluid != null ? FluidFuelRegister.instance.getFuel(fuelFluid) : null;
  }

  public static IFluidFuel toFuel(@Nullable Fluid fuelFluid) {
    return fuelFluid != null ? FluidFuelRegister.instance.getFuel(fuelFluid) : null;
  }

  public static IFluidCoolant toCoolant(@Nonnull SmartTank coolantTank) {
    return toCoolant(coolantTank.getFluid());
  }

  public static IFluidCoolant toCoolant(@Nullable FluidStack coolantFluid) {
    return coolantFluid != null ? FluidFuelRegister.instance.getCoolant(coolantFluid) : null;
  }

  public static IFluidCoolant toCoolant(@Nullable Fluid coolantFluid) {
    return coolantFluid != null ? FluidFuelRegister.instance.getCoolant(coolantFluid) : null;
  }

  public int getTicksPerCoolant() {
    return ticksPerCoolant;
  }

  public int getTicksPerCoolant(int amount) {
    return ticksPerCoolant * amount;
  }

  public int getTicksPerFuel() {
    return ticksPerFuel;
  }

  public int getTicksPerFuel(int amount) {
    return ticksPerFuel * amount;
  }

  public int getEnergyPerTick() {
    return energyPerTick;
  }

}
