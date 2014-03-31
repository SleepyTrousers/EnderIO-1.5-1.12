package crazypants.enderio.machine.generator;

import cofh.api.energy.EnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.fuels.IronEngineCoolant;
import buildcraft.api.fuels.IronEngineCoolant.Coolant;
import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.fuels.IronEngineFuel.Fuel;
import buildcraft.api.power.IPowerEmitter;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.BlockCoord;

public class TileCombustionGenerator extends AbstractMachineEntity implements IPowerEmitter, IFluidHandler  {

  final FluidTank coolantTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
  final FluidTank fuelTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
  private boolean tanksDirty;
  
  private int ticksRemaingFuel;
  private int ticksRemaingCoolant;  
  private boolean active;
  
  private PowerDistributor powerDis;
  
  public TileCombustionGenerator() {
    super(new SlotDefinition(-1, -1, -1, -1, -1, -1));
    powerHandler.configure(0, 0, 0, capacitorType.capacitor.getMaxEnergyStored());
  }

  @Override
  public String getInventoryName() {
    return ModObject.blockCombustionGenerator.unlocalisedName;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCombustionGenerator.unlocalisedName;
  }

  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return true;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public float getProgress() {
    return 0;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(resource == null || resource.getFluid() == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    tanksDirty = true;
    if(IronEngineCoolant.isCoolant(resource.getFluid())) {
      return coolantTank.fill(resource, doFill);  
    } 
    if(IronEngineFuel.getFuelForFluid(resource.getFluid()) != null) {
      return fuelTank.fill(resource, doFill);
    }
    return 0;    
  }
  

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {   
    return null;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {    
    return null;
  }

  
  
  @Override
  public void onNeighborBlockChange(Block blockId) {
    super.onNeighborBlockChange(blockId);
    if(powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {    
    boolean res = tanksDirty;    
    tanksDirty = false;
    
    if(!redstoneChecksPassed) {
      return res;
    }
    
    boolean isActive = checkActive();
    if(isActive != this.active) {
      active = isActive;
      res = true;
    }    
    
    transmitEnergy();
    
    return res;
  }
  
  private boolean transmitEnergy() { 
    if(storedEnergy <= 0) {
      return false;
    }
    if(powerDis == null) {
      powerDis = new PowerDistributor(new BlockCoord(this));
    }        
    float transmitted = powerDis.transmitEnergy(worldObj, (float)storedEnergy);   
    storedEnergy -= transmitted;
    return transmitted > 0;      
  }

  private boolean checkActive() {
    if(fuelTank.getFluidAmount() <= 0 || coolantTank.getFluidAmount() <= 0 || storedEnergy >= powerHandler.getMaxEnergyStored()) {            
      return false;
    }
    Coolant coolant = IronEngineCoolant.getCoolant(coolantTank.getFluid());
    if(coolant == null) {      
      return false;
    }
    Fuel fuel = IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid());
    if(fuel == null) {
      return false;
    }
    
    boolean res = false;
    ticksRemaingFuel--;        
    if(ticksRemaingFuel <= 0) {
      fuelTank.drain(1, true);      
      ticksRemaingFuel = getNumTicksPerMbFuel(fuel);
      res = true;
    }    
    ticksRemaingCoolant--;
    if(ticksRemaingCoolant <= 0) {
      coolantTank.drain(1, true);            
      ticksRemaingCoolant = getNumTicksPerMbCoolant(coolant, fuel); 
      res = true;
    }
    
    storedEnergy += fuel.powerPerCycle;
    
    return fuelTank.getFluidAmount() > 0 && coolantTank.getFluidAmount() > 0;
  }

  public int getNumTicksPerMbFuel() {
    if(fuelTank.getFluidAmount() <=0 ) {
      return 0;
    }
    return getNumTicksPerMbFuel(IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid()));
  }
  
  public int getNumTicksPerMbCoolant() {
    if(fuelTank.getFluidAmount() <=0 ) {
      return 0;
    }
    Fuel fuel = IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid());
    Coolant coolant = IronEngineCoolant.getCoolant(coolantTank.getFluid());    
    return getNumTicksPerMbCoolant(coolant, fuel);
  }
  
  static int getNumTicksPerMbFuel(Fuel fuel) {
    if(fuel == null) {
      return 0;
    }
    return fuel.totalBurningTime/1000;
  }

  static int getNumTicksPerMbCoolant(Coolant coolant, Fuel fuel) {
    if(coolant == null || fuel == null) {
      return 0;
    }
    float power = fuel.powerPerCycle;    
    float cooling = coolant.getDegreesCoolingPerMB(100);
    double toCool = 1d/ (0.027 * power);
    int numTicks = (int)Math.round(toCool/ (cooling * 1000));
    return numTicks;
  }
  
  protected void updateStoredEnergyFromPowerHandler() {
   //no-op as we don't actually need a BC power handler for a generator
    //Need to clean this up 
  }  

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int)(storedEnergy * 10);
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {    
    return IronEngineCoolant.isCoolant(fluid) || IronEngineFuel.getFuelForFluid(fluid) != null;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {   
    return false;      
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] {coolantTank.getInfo(), fuelTank.getInfo()};
  }
  
  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);

    if(nbtRoot.hasKey("coolantTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("coolantTank");
      if(tankRoot != null) {
        coolantTank.readFromNBT(tankRoot);
      } else {
        coolantTank.setFluid(null);
      }
    } else {
      coolantTank.setFluid(null);
    }

    if(nbtRoot.hasKey("fuelTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("fuelTank");
      if(tankRoot != null) {
        fuelTank.readFromNBT(tankRoot);
      } else {
        fuelTank.setFluid(null);
      }
    } else {
      fuelTank.setFluid(null);
    }
    
    ticksRemaingFuel = nbtRoot.getInteger("ticksRemaingFuel");
    ticksRemaingCoolant = nbtRoot.getInteger("ticksRemaingCoolant");
    active = nbtRoot.getBoolean("active");    

  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    if(coolantTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      coolantTank.writeToNBT(tankRoot);
      nbtRoot.setTag("coolantTank", tankRoot);
    }
    if(fuelTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      fuelTank.writeToNBT(tankRoot);
      nbtRoot.setTag("fuelTank", tankRoot);
    }    
    nbtRoot.setInteger("ticksRemaingFuel",ticksRemaingFuel);
    nbtRoot.setInteger("ticksRemaingCoolant",ticksRemaingCoolant);
    nbtRoot.setBoolean("active",active);  
  }

  public double getCurrentOutputMj() {
    if(!active) {
      return 0;
    }
    Fuel fuel = IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid());
    if(fuel == null) {
      return 0;
    }
    return fuel.powerPerCycle;    
  }
  
  

}
