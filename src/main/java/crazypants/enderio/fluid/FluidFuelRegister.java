package crazypants.enderio.fluid;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.ICoolant;
import buildcraft.api.fuels.IFuel;


public class FluidFuelRegister {

  public static final FluidFuelRegister instance = new FluidFuelRegister();
  
  private final Map<String, IFluidCoolant> coolants = new HashMap<String, IFluidCoolant>();
  private final Map<String, IFluidFuel> fuels = new HashMap<String, IFluidFuel>();
  
  private FluidFuelRegister() {        
    addCoolant(FluidRegistry.WATER, 0.0023f);    
  }

  private void addCoolant(Fluid fluid, float degreesCoolingPerMB) {
    if(fluid == null || coolants.get(fluid.getName()) != null) {
      return;
    }
    coolants.put(fluid.getName(), new Coolant(fluid, degreesCoolingPerMB));
    if(BuildcraftFuelRegistry.coolant != null && BuildcraftFuelRegistry.coolant.getCoolant(fluid) == null) {
      BuildcraftFuelRegistry.coolant.addCoolant(fluid, degreesCoolingPerMB);
    }    
  }
  
  public IFluidCoolant getCoolant(Fluid fluid) {
    if(fluid == null) {
      return null;
    }
    IFluidCoolant res = coolants.get(fluid.getName());
    if(res == null && !coolants.containsKey(fluid.getName()) && BuildcraftFuelRegistry.coolant != null) {
      ICoolant bcCool = BuildcraftFuelRegistry.coolant.getCoolant(fluid);
      if(bcCool != null) {
        res = new CoolantBC(bcCool);
      }
      coolants.put(fluid.getName(), res);
    }    
    return res;
  }
  
  public IFluidCoolant getCoolant(FluidStack fluid) {
    if(fluid == null || fluid.getFluid() == null) {
      return null;
    }
    return getCoolant(fluid.getFluid());
  }
  
  
  public void addFuel(Fluid fluid, int powerPerCycleRF, int totalBurnTime) {
    if(fluid == null || fuels.get(fluid.getName()) != null) {
      return;
    }
    fuels.put(fluid.getName(), new Fuel(fluid, powerPerCycleRF, totalBurnTime));
    if(BuildcraftFuelRegistry.fuel != null && BuildcraftFuelRegistry.fuel.getFuel(fluid) == null) {
      BuildcraftFuelRegistry.fuel.addFuel(fluid, powerPerCycleRF, totalBurnTime);
    }      
  }

  public IFluidFuel getFuel(Fluid fluid) {
    if(fluid == null) {
      return null;
    }
    IFluidFuel res = fuels.get(fluid.getName());
    if(res == null && !fuels.containsKey(fluid.getName()) && BuildcraftFuelRegistry.fuel != null) {
      IFuel bcFuel = BuildcraftFuelRegistry.fuel.getFuel(fluid);
      if(bcFuel != null) {
        res = new FuelBC(bcFuel);
      }
      fuels.put(fluid.getName(), res);
    }    
    return res;
  }
  
  public IFluidFuel getFuel(FluidStack fluid) {
    if(fluid == null || fluid.getFluid() == null) {
      return null;
    }
    return getFuel(fluid.getFluid());
  }

  private static class Fuel implements IFluidFuel {
    
    private final Fluid fluid;
    private final int powerPerCycle;
    private final int totalBurningTime;

    Fuel(Fluid fluid, int powerPerCycle, int totalBurningTime) {
      this.fluid = fluid;
      this.powerPerCycle = powerPerCycle;
      this.totalBurningTime = totalBurningTime;
    }

    @Override
    public Fluid getFluid() {
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
  
  private static class FuelBC extends Fuel {

    FuelBC(IFuel fuel) {
      super(fuel.getFluid(), fuel.getPowerPerCycle(), fuel.getTotalBurningTime());
    }
    
  }
  
  private static class Coolant implements IFluidCoolant {

    private final Fluid fluid;
    private final float degreesCoolingPerMB;
    
    Coolant(Fluid fluid, float degreesCoolingPerMB) {      
      this.fluid = fluid;
      this.degreesCoolingPerMB = degreesCoolingPerMB;
    }

    @Override
    public Fluid getFluid() {    
      return fluid;
    }

    @Override
    public float getDegreesCoolingPerMB(float heat) {
      return degreesCoolingPerMB;
    }    
  }
  
  private static class CoolantBC extends Coolant {

    CoolantBC(ICoolant coolant) {
      //NB: in the current BC impl the temperature to getDegreesCoolingPerMB is ignored
      super(coolant.getFluid(), coolant.getDegreesCoolingPerMB(100));
    }
    
  }

}
