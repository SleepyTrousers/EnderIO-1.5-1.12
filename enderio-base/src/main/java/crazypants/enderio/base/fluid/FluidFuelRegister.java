package crazypants.enderio.base.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class FluidFuelRegister implements IFluidRegister {

  public static final FluidFuelRegister instance = new FluidFuelRegister();

  private static final @Nonnull String KEY_FLUID_NAME = "fluidName";
  private static final @Nonnull String KEY_POWER_PER_CYCLE = "powerPerCycle";
  private static final @Nonnull String KEY_TOTAL_BURN_TIME = "totalBurnTime";
  private static final @Nonnull String KEY_COOLING_PER_MB = "coolingPerMb";

  private final Map<String, IFluidCoolant> coolants = new HashMap<String, IFluidCoolant>();
  private final Map<String, IFluidFuel> fuels = new HashMap<String, IFluidFuel>();

  private final List<IFluidRegister> otherRegisters = new ArrayList<IFluidRegister>();

  public static void init(@Nonnull FMLInitializationEvent event) {
    // NOP, just load the class
  }

  private FluidFuelRegister() {
    addCoolant(FluidRegistry.WATER, 0.0023f);
  }

  public void addRegister(IFluidRegister register) {
    if (register != null) {
      otherRegisters.add(register);
    }
  }

  public void addCoolant(NBTTagCompound tag) {
    if (tag == null) {
      return;
    }
    if (!tag.hasKey(KEY_FLUID_NAME)) {
      return;
    }
    if (!tag.hasKey(KEY_COOLING_PER_MB)) {
      return;
    }
    addCoolant(tag.getString(KEY_FLUID_NAME), tag.getFloat(KEY_COOLING_PER_MB));
  }

  public void addCoolant(String fluidName, float degreesCoolingPerMB) {
    addCoolant(NullHelper.notnullF(FluidRegistry.getFluid(fluidName), "Invalid fluid " + fluidName), degreesCoolingPerMB);
  }

  @Override
  public void addCoolant(@Nonnull Fluid fluid, float degreesCoolingPerMB) {
    coolants.put(fluid.getName(), new CoolantImpl(fluid, degreesCoolingPerMB));
    for (IFluidRegister reg : otherRegisters) {
      reg.addCoolant(fluid, degreesCoolingPerMB);
    }
  }

  @Override
  public @Nullable IFluidCoolant getCoolant(@Nonnull Fluid fluid) {
    IFluidCoolant res = coolants.get(fluid.getName());
    if (res == null && !coolants.containsKey(fluid.getName())) {
      for (IFluidRegister reg : otherRegisters) {
        res = reg.getCoolant(fluid);
        if (res != null) {
          break;
        }
      }
      coolants.put(fluid.getName(), res);
    }
    return res;
  }

  @Override
  public @Nullable IFluidCoolant getCoolant(@Nonnull FluidStack fluid) {
    final Fluid fluid2 = fluid.getFluid();
    if (fluid2 == null) {
      return null;
    }
    return getCoolant(fluid2);
  }

  public void addFuel(NBTTagCompound tag) {
    if (tag == null) {
      return;
    }
    if (!tag.hasKey(KEY_FLUID_NAME)) {
      return;
    }
    if (!tag.hasKey(KEY_POWER_PER_CYCLE)) {
      return;
    }
    if (!tag.hasKey(KEY_TOTAL_BURN_TIME)) {
      return;
    }
    addFuel(tag.getString(KEY_FLUID_NAME), tag.getInteger(KEY_POWER_PER_CYCLE), tag.getInteger(KEY_TOTAL_BURN_TIME));
  }

  public void addFuel(String fluidName, int powerPerCycleRF, int totalBurnTime) {
    addFuel(NullHelper.notnullF(FluidRegistry.getFluid(fluidName), "Invalid fluid " + fluidName), powerPerCycleRF, totalBurnTime);
  }

  @Override
  public void addFuel(@Nonnull Fluid fluid, int powerPerCycleRF, int totalBurnTime) {
    fuels.put(fluid.getName(), new FuelImpl(fluid, powerPerCycleRF, totalBurnTime));
    for (IFluidRegister reg : otherRegisters) {
      reg.addFuel(fluid, powerPerCycleRF, totalBurnTime);
    }
  }

  @Override
  public @Nullable IFluidFuel getFuel(@Nonnull Fluid fluid) {
    IFluidFuel res = fuels.get(fluid.getName());
    if (res == null && !fuels.containsKey(fluid.getName())) {
      for (IFluidRegister reg : otherRegisters) {
        res = reg.getFuel(fluid);
        if (res != null) {
          break;
        }
      }
      fuels.put(fluid.getName(), res);
    }
    return res;
  }

  @Override
  public @Nullable IFluidFuel getFuel(@Nonnull FluidStack fluid) {
    final Fluid fluid2 = fluid.getFluid();
    if (fluid2 == null) {
      return null;
    }
    return getFuel(fluid2);
  }

  public static class FuelImpl implements IFluidFuel {

    private final @Nonnull Fluid fluid;
    private final int powerPerCycle;
    private final int totalBurningTime;

    public FuelImpl(@Nonnull Fluid fluid, int powerPerCycle, int totalBurningTime) {
      this.fluid = fluid;
      this.powerPerCycle = powerPerCycle;
      this.totalBurningTime = totalBurningTime;
    }

    @Override
    public @Nonnull Fluid getFluid() {
      return fluid;
    }

    @Override
    public int getTotalBurningTime() {
      return totalBurningTime;
    }

    @Override
    public int getPowerPerCycle() {
      return powerPerCycle;
    }

  }

  public static class CoolantImpl implements IFluidCoolant {

    private final @Nonnull Fluid fluid;
    private final float degreesCoolingPerMB;

    public CoolantImpl(@Nonnull Fluid fluid, float degreesCoolingPerMB) {
      this.fluid = fluid;
      this.degreesCoolingPerMB = degreesCoolingPerMB;
    }

    @Override
    public @Nonnull Fluid getFluid() {
      return fluid;
    }

    @Override
    public float getDegreesCoolingPerMB(float heat) {
      return degreesCoolingPerMB;
    }
  }

}
