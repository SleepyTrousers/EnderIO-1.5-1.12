package crazypants.enderio.machines.machine.generator.combustion;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.machines.config.config.CombustionGenConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import static crazypants.enderio.machines.capacitor.CapacitorKey.COMBUSTION_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.COMBUSTION_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.COMBUSTION_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.COMBUSTION_POWER_SEND;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_COMBUSTION_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_COMBUSTION_POWER_EFFICIENCY;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_COMBUSTION_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_COMBUSTION_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_COMBUSTION_POWER_SEND;

@Storable
public class TileCombustionGenerator extends AbstractGeneratorEntity implements ITankAccess.IExtendedTankAccess, IPaintable.IPaintableTileEntity {

  public static class Enhanced extends TileCombustionGenerator {

    public Enhanced() {
      super(new SlotDefinition(0, 0, 1), ENHANCED_COMBUSTION_POWER_SEND, ENHANCED_COMBUSTION_POWER_BUFFER, ENHANCED_COMBUSTION_POWER_GEN);
      setEnergyLoss(ENHANCED_COMBUSTION_POWER_LOSS);
      setEfficiencyMultiplier(ENHANCED_COMBUSTION_POWER_EFFICIENCY);
    }

    @Nonnull
    @Override
    public ICapacitorData getCapacitorData() {
      return CapacitorHelper.increaseCapacitorLevel(super.getCapacitorData(), 1f);
    }

    @Override
    public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
      return (faceHit != EnumFacing.UP || mode == IoMode.NONE) && super.supportsMode(faceHit, mode);
    }

  }

  @Store
  private final @Nonnull SmartTank coolantTank;
  @Store
  private final @Nonnull SmartTank fuelTank;
  private boolean tanksDirty;

  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  private int ticksRemaingFuel;
  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  private int ticksRemaingCoolant;
  @Store(NBTAction.CLIENT)
  private boolean active;

  private PowerDistributor powerDis;

  @Store(NBTAction.CLIENT)
  private int generated;

  private boolean inPause = false;

  private boolean generatedDirty = false;

  private static int IO_MB_TICK = 250;

  private IFluidFuel curFuel;
  private IFluidCoolant curCoolant;

  public TileCombustionGenerator() {
    this(new SlotDefinition(0, 0, 1), COMBUSTION_POWER_SEND, COMBUSTION_POWER_BUFFER, COMBUSTION_POWER_GEN);
    setEnergyLoss(COMBUSTION_POWER_LOSS);
  }

  protected TileCombustionGenerator(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergySent, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergySent, maxEnergyStored, maxEnergyUsed);
    coolantTank = new CoolantTank(Math.round(CombustionGenConfig.combGenTankSize.get() * getEfficiencyMultiplier()));
    fuelTank = new FuelTank(Math.round(CombustionGenConfig.combGenTankSize.get() * getEfficiencyMultiplier()));
    coolantTank.setTileEntity(this);
    coolantTank.setCanDrain(false);
    fuelTank.setTileEntity(this);
    fuelTank.setCanDrain(false);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facingIn -> getSmartTankFluidHandler().get(facingIn));
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (super.doPull(dir)) {
      return true;
    }
    if (dir != null && fuelTank.getFluidAmount() < fuelTank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), fuelTank, IO_MB_TICK) > 0) {
        setTanksDirty();
        return true;
      }
    }
    if (dir != null && coolantTank.getFluidAmount() < coolantTank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), coolantTank, IO_MB_TICK) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode != IoMode.PUSH && mode != IoMode.PUSH_PULL;
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_combustion_generator.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  @Override
  protected void processTasks(boolean redstoneChecksPassed) {
    if (!redstoneChecksPassed) {
      if (active) {
        active = false;
        updateClients = true;
      }
      return;
    } else {

      int lastGenerated = generated;

      boolean isActive = generateEnergy();
      if (isActive != active) {
        active = isActive;
        updateClients = true;
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
      updateClients = true;
    }
  }

  private boolean transmitEnergy() {
    if (getEnergyStored() <= 0) {
      return false;
    }
    if (powerDis == null) {
      powerDis = new PowerDistributor(getPos());
    }
    int transmitted = powerDis.transmitEnergy(world, getMaxEnergySent());
    setEnergyStored(getEnergyStored() - transmitted);
    return transmitted > 0;
  }

  private boolean generateEnergy() {

    generated = 0;

    // Check: We have no ticks remaining and cannot refill?
    // Check: We are full?
    if ((ticksRemaingCoolant <= 0 && getCoolantTank().isEmpty()) || (ticksRemaingFuel <= 0 && getFuelTank().isEmpty())
        || getEnergyStored() >= getMaxEnergyStored()) {
      return false;
    }

    // once full, don't start again until we have drained 10 seconds worth of power to prevent
    // flickering on and off constantly when powering a machine that draws less than this produces
    CombustionMath math = getMath();
    if (inPause) {
      int powerPerCycle = math.getEnergyPerTick();
      if (getEnergyStored() >= (getMaxEnergyStored() - (powerPerCycle * 200)) && getEnergyStored() > (getMaxEnergyStored() / 8)) {
        return false;
      }
    }
    inPause = false;

    // Use old ticks
    if (ticksRemaingCoolant > 0 && ticksRemaingFuel > 0 && math.getEnergyPerTick() > 0) {
      ticksRemaingFuel--;
      ticksRemaingCoolant--;
      generated = math.getEnergyPerTick();
      setEnergyStored(getEnergyStored() + generated);
      return true;
    }
    // oops, seems we need to refill...

    // Check: Refresh from fluid if we're out of ticks
    if (ticksRemaingFuel <= 0) {
      curFuel = null;
    }
    if (ticksRemaingCoolant <= 0) {
      curCoolant = null;
    }

    // new math as one of the fluids may have changed
    math = getMath();

    // can we draw energy from what we have in our tanks?
    if (math.getEnergyPerTick() <= 0) {
      return false;
    }

    // re-fill
    if (ticksRemaingCoolant <= 0) {
      ticksRemaingCoolant += math.getTicksPerCoolant(getCoolantTank().removeFluidAmount(100));
    }
    if (ticksRemaingFuel <= 0) {
      ticksRemaingFuel += math.getTicksPerFuel(getFuelTank().removeFluidAmount(100));
    }

    // last sanity check, then generate energy
    if (ticksRemaingCoolant > 0 && ticksRemaingFuel > 0) {
      ticksRemaingFuel--;
      ticksRemaingCoolant--;
      generated = math.getEnergyPerTick();
      setEnergyStored(getEnergyStored() + generated);
      return true;
    }

    return false;
  }

  CombustionMath getMath() {
    if (curFuel == null) {
      curFuel = CombustionMath.toFuel(getFuelTank());
    }
    if (curCoolant == null) {
      curCoolant = CombustionMath.toCoolant(getCoolantTank());
    }
    return new CombustionMath(curCoolant, curFuel, maxEnergyUsed.getFloat(getCapacitorData()), getEfficiencyMultiplier());
  }

  public int getGeneratedLastTick() {
    if (!active) {
      return 0;
    }
    return generated;
  }

  @Override
  public int getPowerUsePerTick() {
    return 0;
  }

  public @Nonnull SmartTank getCoolantTank() {
    return coolantTank;
  }

  public @Nonnull SmartTank getFuelTank() {
    return fuelTank;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null) {
      final Fluid fluid = forFluidType.getFluid();
      if (fluid == null) {
        return null;
      }
      if (FluidFuelRegister.instance.getCoolant(fluid) != null) {
        return coolantTank;
      }
      if (FluidFuelRegister.instance.getFuel(fluid) != null) {
        return fuelTank;
      }
    }
    return null;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[] { /* coolantTank, fuelTank */ };
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

}
