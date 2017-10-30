package crazypants.enderio.machine.generator.combustion;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.NBTAction;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidCoolant;
import crazypants.enderio.fluid.IFluidFuel;
import crazypants.enderio.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machine.generator.AbstractGeneratorEntity;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.PowerDistributor;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public class TileCombustionGenerator extends AbstractGeneratorEntity
    implements ITankAccess.IExtendedTankAccess, IPaintable.IPaintableTileEntity {

  @Store
  private final SmartTank coolantTank = new SmartTank(Fluid.BUCKET_VOLUME * 5) {

    @Override
    public boolean canFillFluidType(FluidStack resource) {
      return super.canFillFluidType(resource) && FluidFuelRegister.instance.getCoolant(resource.getFluid()) != null;
    }

  };
  @Store
  private final SmartTank fuelTank = new SmartTank(Fluid.BUCKET_VOLUME * 5) {

    @Override
    public boolean canFillFluidType(FluidStack resource) {
      return super.canFillFluidType(resource) && FluidFuelRegister.instance.getFuel(resource.getFluid()) != null;
    }

  };
  private boolean tanksDirty;

  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  private int ticksRemaingFuel;
  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  private int ticksRemaingCoolant;
  @Store(NBTAction.UPDATE)
  private boolean active;

  private PowerDistributor powerDis;

  @Store(NBTAction.UPDATE)
  private int generated;

  private boolean inPause = false;

  private boolean generatedDirty = false;

  private int maxOutputTick = 1280;

  private static int IO_MB_TICK = 250;

  private IFluidFuel curFuel;
  private IFluidCoolant curCoolant;

  public TileCombustionGenerator() {
    super(new SlotDefinition(-1, -1, -1, -1, -1, -1), MachineObject.blockCombustionGenerator);
    coolantTank.setTileEntity(this);
    coolantTank.setCanDrain(false);
    fuelTank.setTileEntity(this);
    fuelTank.setCanDrain(false);
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    boolean res = super.doPull(dir);
    if (dir != null && fuelTank.getFluidAmount() < fuelTank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), fuelTank, IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
    if (dir != null && coolantTank.getFluidAmount() < coolantTank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), coolantTank, IO_MB_TICK) > 0) {
        setTanksDirty();
        }
    }
    return res;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode != IoMode.PUSH && mode != IoMode.PUSH_PULL;
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.blockCombustionGenerator.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void onNeighborBlockChange(IBlockState state, World worldIn, BlockPos posIn, Block blockIn, BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = false;

    if (!redstoneChecksPassed) {
      if (active) {
        active = false;
        res = true;
      }
      return res;
    } else {

      int lastGenerated = generated;

      boolean isActive = generateEnergy();
      if (isActive != active) {
        active = isActive;
        res = true;
      }
      if (lastGenerated != generated) {
        generatedDirty = true;
      }

      if (getEnergyStored() >= getMaxEnergyStored()) {
        inPause = true;
      }

      transmitEnergy();
    }

    if (tanksDirty && shouldDoWorkThisTick(10)) {
      PacketHandler.sendToAllAround(new PacketCombustionTank(this), this);
      tanksDirty = false;
    }

    if (generatedDirty && shouldDoWorkThisTick(10)) {
      generatedDirty = false;
      res = true;
    }

    return res;
  }

  private boolean transmitEnergy() {
    if (getEnergyStored() <= 0) {
      return false;
    }
    if (powerDis == null) {
      powerDis = new PowerDistributor(getPos());
    }
    int transmitted = powerDis.transmitEnergy(world, Math.min(maxOutputTick, getEnergyStored()));
    setEnergyStored(getEnergyStored() - transmitted);
    return transmitted > 0;
  }

  private boolean generateEnergy() {

    generated = 0;

    if ((ticksRemaingCoolant <= 0 && getCoolantTank().getFluidAmount() <= 0) || (ticksRemaingFuel <= 0 && getFuelTank().getFluidAmount() <= 0)
        || getEnergyStored() >= getMaxEnergyStored()) {
      return false;
    }

    // once full, don't start again until we have drained 10 seconds worth of power to prevent
    // flickering on and off constantly when powering a machine that draws less than this produces
    if (inPause) {
      int powerPerCycle = getPowerPerCycle();
      if (getEnergyStored() >= (getMaxEnergyStored() - (powerPerCycle * 200)) && getEnergyStored() > (getMaxEnergyStored() / 8)) {
        return false;
      }
    }
    inPause = false;

    ticksRemaingFuel--;
    if (ticksRemaingFuel <= 0) {
      curFuel = FluidFuelRegister.instance.getFuel(getFuelTank().getFluid());
      if (curFuel == null) {
        return false;
      }
      int drained = getFuelTank().removeFluidAmount(100);
      if (drained == 0) {
        return false;
      }
      ticksRemaingFuel = getNumTicksPerMbFuel(curFuel) * drained;
    } else if (curFuel == null) {
      curFuel = FluidFuelRegister.instance.getFuel(getFuelTank().getFluid());
      if (curFuel == null) {
        return false;
      }
    }

    ticksRemaingCoolant--;
    if (ticksRemaingCoolant <= 0) {
      updateCoolantFromTank();
      if (curCoolant == null) {
        return false;
      }
      int drained = getCoolantTank().removeFluidAmount(100);
      if (drained == 0) {
        return false;
      }
      ticksRemaingCoolant = getNumTicksPerMbCoolant(curCoolant, curFuel) * drained;
    } else if (curCoolant == null) {
      updateCoolantFromTank();
      if (curCoolant == null) {
        return false;
      }
    }

    generated = getPowerPerCycle();
    setEnergyStored(getEnergyStored() + generated);

    return getFuelTank().getFluidAmount() > 0 && getCoolantTank().getFluidAmount() > 0;
  }

  protected void updateCoolantFromTank() {
    curCoolant = FluidFuelRegister.instance.getCoolant(getCoolantTank().getFluid());
  }

  private int getPowerPerCycle() {
    return curFuel == null ? 0 : curFuel.getPowerPerCycle();
  }

  public int getNumTicksPerMbFuel() {
    if (getFuelTank().getFluidAmount() <= 0) {
      return 0;
    }
    return getNumTicksPerMbFuel(FluidFuelRegister.instance.getFuel(getFuelTank().getFluid().getFluid()));
  }

  public int getNumTicksPerMbCoolant() {
    if (getFuelTank().getFluidAmount() <= 0) {
      return 0;
    }
    if (world.isRemote) {
      curFuel = FluidFuelRegister.instance.getFuel(getFuelTank().getFluid());
      updateCoolantFromTank();
    }
    return getNumTicksPerMbCoolant(curCoolant, curFuel);
  }

  public static int getNumTicksPerMbFuel(IFluidFuel fuel) {
    if (fuel == null) {
      return 0;
    }
    return fuel.getTotalBurningTime() / 1000;
  }

  public static float HEAT_PER_RF = 0.00023F;

  public static int getNumTicksPerMbCoolant(IFluidCoolant coolant, IFluidFuel fuel) {
    if (coolant == null || fuel == null) {
      return 0;
    }
    float power = fuel.getPowerPerCycle();
    float cooling = coolant.getDegreesCoolingPerMB(100);
    double toCool = 1d / (HEAT_PER_RF * power);
    int numTicks = (int) Math.round(toCool / (cooling * 1000));
    return numTicks;
  }

  public int getGeneratedLastTick() {
    if (!active) {
      return 0;
    }
    return generated;
  }

  @Override
  public int getPowerUsePerTick() {
    if (getFuelTank().getFluidAmount() <= 0) {
      return 0;
    }
    IFluidFuel fuel = FluidFuelRegister.instance.getFuel(getFuelTank().getFluid());
    if (fuel == null) {
      return 0;
    }
    return fuel.getPowerPerCycle();
  }

  public SmartTank getCoolantTank() {
    return coolantTank;
  }

  public SmartTank getFuelTank() {
    return fuelTank;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null) {
      if (FluidFuelRegister.instance.getCoolant(forFluidType.getFluid()) != null) {
        return coolantTank;
      }
      if (FluidFuelRegister.instance.getFuel(forFluidType.getFluid()) != null) {
        return fuelTank;
      }
    }
    return null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { /* coolantTank, fuelTank */};
  }

  @Override
  public void setTanksDirty() {
    tanksDirty = true;
  }

  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    List<ITankData> result = new ArrayList<ITankData>();
    result.add(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.INPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return fuelTank.getFluid();
      }

      @Override
      public int getCapacity() {
        return fuelTank.getCapacity();
      }
    });
    result.add(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.INPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return coolantTank.getFluid();
      }

      @Override
      public int getCapacity() {
        return coolantTank.getCapacity();
      }
    });
    return result;
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, coolantTank, fuelTank);
    }
    return smartTankFluidHandler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
