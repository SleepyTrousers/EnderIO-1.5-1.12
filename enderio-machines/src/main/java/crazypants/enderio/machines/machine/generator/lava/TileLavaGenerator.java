package crazypants.enderio.machines.machine.generator.lava;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityGeneratorEntity;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.machines.init.MachineObject;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_GEN;

@Storable
public class TileLavaGenerator extends AbstractCapabilityGeneratorEntity implements IPaintable.IPaintableTileEntity, ITankAccess.IExtendedTankAccess {

  @Store
  public int burnTime = 0;
  @Store
  public int heat = 0;
  @Store
  protected final @Nonnull SmartTank tank = new SmartTank(FluidRegistry.LAVA, 4000); // TODO config 4000

  private PowerDistributor powerDis;

  public TileLavaGenerator() {
    super(STIRLING_POWER_BUFFER, STIRLING_POWER_GEN); // TODO
    // setEnergyLoss(STIRLING_POWER_LOSS); // TODO
    tank.setTileEntity(this);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_lava_generator.getUnlocalisedName();
  }

  @Override
  public boolean isActive() {
    return burnTime > 0;
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
  protected boolean processTasks(boolean redstoneCheck) {
    if (heat > 0) {
      heat--;
    }

    if (redstoneCheck && !getEnergy().isFull()) {
      if (burnTime > 0) {
        getEnergy().setEnergyStored(getEnergy().getEnergyStored() + getPowerGenPerTick());
        burnTime--;
        heat = Math.min(heat + 4, getMaxHeat()); // TODO config 4
      }
      if (burnTime <= 0 && !tank.isEmpty()) {
        tank.drain(1, true);
        burnTime = getLavaBurntime() / Fluid.BUCKET_VOLUME;
      }
    }

    // TODO: heat > 75% => make fire around machine

    transmitEnergy();

    return false;
  }

  private int getLavaBurntime() {
    return 20000; // hardcode vanilla value? or clamp it into some range?
    // return Math.max(20000, TileEntityFurnace.getItemBurnTime(new ItemStack(Items.LAVA_BUCKET)));
  }

  protected int getPowerGenPerTick() {
    return (int) Math.max(1, getEnergy().getMaxUsage() * getHeatFactor());
  }

  protected float getHeatFactor() {
    return Math.max(.05f, 1f - heat / (float) getMaxHeat()); // TODO config 5%
  }

  protected int getMaxHeat() {
    return getLavaBurntime() * 8; // TODO config 8
  }

  private boolean transmitEnergy() {
    if (powerDis == null) {
      powerDis = new PowerDistributor(getPos());
    }
    int canTransmit = Math.min(getEnergy().getEnergyStored(), getEnergy().getMaxUsage() * 2);
    if (canTransmit <= 0) {
      return false;
    }
    int transmitted = powerDis.transmitEnergy(world, canTransmit);
    getEnergy().setEnergyStored(getEnergy().getEnergyStored() - transmitted);
    return transmitted > 0;
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank);
    }
    return smartTankFluidHandler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

  @Override
  @Nullable
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (tank.canFill(forFluidType)) {
      return tank;
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

  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    return Collections.<ITankData> singletonList(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.INPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return tank.getFluid();
      }

      @Override
      public int getCapacity() {
        return tank.getCapacity();
      }
    });
  }

}
