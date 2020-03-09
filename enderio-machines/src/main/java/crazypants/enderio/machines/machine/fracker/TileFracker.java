package crazypants.enderio.machines.machine.fracker;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.machines.config.config.LavaGenConfig;
import crazypants.enderio.machines.init.MachineObject;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import static crazypants.enderio.machines.capacitor.CapacitorKey.LAVAGEN_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.LAVAGEN_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.LAVAGEN_POWER_LOSS;

@Storable
public class TileFracker extends AbstractCapabilityMachineEntity implements IPaintable.IPaintableTileEntity, ITankAccess.IExtendedTankAccess {

  // @Store
  // public long pressure = 0;
  @Store
  protected final @Nonnull SmartTank tank = new SmartTank(Fluids.VAPOR_OF_LEVITY.getFluid(), LavaGenConfig.tankSize.get()); // TODO config

  public TileFracker() {
    super(LAVAGEN_POWER_GEN, LAVAGEN_POWER_BUFFER, LAVAGEN_POWER_GEN); // TODO capkeys
    getEnergy().setEnergyLoss(LAVAGEN_POWER_LOSS); // TODO capkeys
    tank.setTileEntity(this);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this::getSmartTankFluidHandler);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_lava_generator.getUnlocalisedName(); // todo mo
  }

  @Override
  public boolean isActive() {
    return !tank.isEmpty();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    super.processTasks(redstoneCheck);
    // if (pressure > 10 && shouldDoWorkThisTick(20)) {
    // int count = Long.SIZE - Long.numberOfLeadingZeros(pressure >>> 4);
    // for (int i = 0; i <= count && pressure > 10; i++) {
    // @SuppressWarnings("null")
    // @Nonnull
    // List<WeightedOre> list = ORES.stream().filter(ore -> ore.getDimension() == Integer.MAX_VALUE || ore.getDimension() == world.provider.getDimension())
    // .filter(ore -> ore.getMinPressure() < pressure).collect(Collectors.toList());
    // Block.spawnAsEntity(world, pos.add(world.rand.nextGaussian() * count, -pos.getY(), world.rand.nextGaussian() * count),
    // WeightedRandom.getRandomItem(world.rand, list).getOre());
    // pressure -= 10;
    // }
    // }

    if (redstoneCheck && !tank.isEmpty() && getEnergy().useEnergy()) {
      final FluidStack drained = tank.drainInternal(100, true);
      if (drained != null) {
        // pressure += drained.amount;
        FrackingData.get(world).add(pos, drained.amount);
      }
    }

    return false;
  }

  // public static final @Nonnull NNList<WeightedOre> ORES = new NNList<>();
  //
  // static {
  // ORES.add(new WeightedOre(100, 0, Integer.MAX_VALUE, new Things("dustBedrock")));
  // ORES.add(new WeightedOre(10, 1000, -1, new Things("POWDER_QUARTZ")));
  // ORES.add(new WeightedOre(10, 2500, 0, new Things("minecraft:obsidian")));
  // ORES.add(new WeightedOre(10, 1000, 0, new Things("minecraft:coal:0")));
  // ORES.add(new WeightedOre(5, 5000, 0, new Things("oreIron")));
  // }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected IFluidHandler getSmartTankFluidHandler(EnumFacing facingIn) {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank);
    }
    return smartTankFluidHandler.get(facingIn);
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
