package crazypants.enderio.machines.machine.generator.zombie;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.EnderGenConfig;
import crazypants.enderio.machines.config.config.ZombieGenConfig;
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

import static crazypants.enderio.machines.capacitor.CapacitorKey.ENDER_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FRANK_N_ZOMBIE_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ZOMBIE_POWER_LOSS;

@Storable
public class TileZombieGenerator extends AbstractGeneratorEntity implements ITankAccess.IExtendedTankAccess, IHasNutrientTank {

  public static class TileFrankenZombieGenerator extends TileZombieGenerator {

    public TileFrankenZombieGenerator() {
      super(new SlotDefinition(0, 0, 1), CapacitorKey.FRANK_N_ZOMBIE_POWER_BUFFER, CapacitorKey.FRANK_N_ZOMBIE_POWER_GEN);
      setEnergyLoss(FRANK_N_ZOMBIE_POWER_LOSS);
    }
  }

  public static class TileEnderGenerator extends TileZombieGenerator {

    public TileEnderGenerator() {
      super(new SlotDefinition(0, 0, 1), CapacitorKey.ENDER_POWER_BUFFER, CapacitorKey.ENDER_POWER_GEN);
      setEnergyLoss(ENDER_POWER_LOSS);
      ticksPerBucketOfFuel = EnderGenConfig.ticksPerBucketOfFuel.get();
      minimumTankLevel = EnderGenConfig.minimumTankLevel.get();
    }

    @Nonnull
    @Override
    protected Fluid getFluidType() {
      return Fluids.ENDER_DISTILLATION.getFluid();
    }
  }

  protected int ticksPerBucketOfFuel = ZombieGenConfig.ticksPerBucketOfFuel.get();
  protected float minimumTankLevel = ZombieGenConfig.minimumTankLevel.get();

  private static int IO_MB_TICK = 250;

  @Store
  @Nonnull
  final SmartTank tank;

  private boolean tanksDirty;
  @Store(NBTAction.CLIENT)
  private boolean active = false;
  private PowerDistributor powerDis;

  @Store
  private float ticksRemaingFuel;
  private boolean inPause;

  public TileZombieGenerator() {
    this(new SlotDefinition(0, 0, 1), CapacitorKey.ZOMBIE_POWER_BUFFER, CapacitorKey.ZOMBIE_POWER_GEN);
    setEnergyLoss(ZOMBIE_POWER_LOSS);
  }

  protected TileZombieGenerator(@Nonnull SlotDefinition slotDef, @Nonnull ICapacitorKey buffer, @Nonnull ICapacitorKey gen) {
    super(slotDef, buffer, gen);
    tank = new SmartTank(getFluidType(), Fluid.BUCKET_VOLUME * 2);
    tank.setTileEntity(this);
    tank.setCanDrain(false);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facingIn -> getSmartTankFluidHandler().get(facingIn));
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode != IoMode.PUSH && mode != IoMode.PUSH_PULL;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (super.doPull(dir)) {
      return true;
    }
    if (dir != null && tank.getFluidAmount() < tank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), tank, IO_MB_TICK) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
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
  protected boolean processTasks(boolean redstoneCheck) {
    boolean res = false;

    if (!redstoneCheck) {
      if (active) {
        active = false;
        res = true;
      }
      return res;
    } else {

      boolean isActive = generateEnergy();
      if (isActive != active) {
        active = isActive;
        res = true;
      }

      if (getEnergyStored() >= getMaxEnergyStored()) {
        inPause = true;
      }

      transmitEnergy();
    }

    if (tanksDirty) {
      PacketHandler.sendToAllAround(new PacketNutrientTank(this), this);
      tanksDirty = false;
    }

    return res;
  }

  private boolean generateEnergy() {

    if (getCapacitorData() == DefaultCapacitorData.NONE) {
      return false;
    }

    // if there are still ticks left from the last mB of fuel we used, keep producing energy no matter what
    if (ticksRemaingFuel >= 1f) {
      doGenerateEnergy();
      return true;
    }

    // if the tank is too empty, we do nothing
    if (tank.getFluidAmount() < getActivationAmount()) {
      return false;
    }

    // once full, don't start again until we have drained 10 seconds worth of power to prevent
    // flickering on and off constantly when powering a machine that draws less than this produces
    if (inPause && getEnergyStored() >= (getMaxEnergyStored() - (getPowerUsePerTick() * 200)) && getEnergyStored() > (getMaxEnergyStored() / 8)) {
      return false;
    }
    inPause = false;

    // eat more fuel and add ticks
    while (ticksRemaingFuel < 1f && tank.getFluidAmount() > 0) {
      tank.removeFluidAmount(1);
      ticksRemaingFuel += ticksPerBucketOfFuel / 1000f;
    }

    // check that we didn't run out of fuel without even gathering enough for 1 tick...
    if (ticksRemaingFuel >= 1f) {
      doGenerateEnergy();
      return true;
    }

    return false;
  }

  private void doGenerateEnergy() {
    ticksRemaingFuel -= 1f;
    setEnergyStored(getEnergyStored() + getPowerUsePerTick());
  }

  int getActivationAmount() {
    return (int) (tank.getCapacity() * minimumTankLevel);
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

  public int getFluidStored(EnumFacing from) {
    return tank.getFluidAmount();
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null && forFluidType.getFluid() == getFluidType()) {
      return tank;
    }
    return null;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[0];
  }

  @Override
  public void setTanksDirty() {
    tanksDirty = true;
  }

  @Override
  public SmartTank getNutrientTank() {
    return tank;
  }

  @SuppressWarnings("null")
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

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank);
    }
    return smartTankFluidHandler;
  }

  /**
   * Used to get different fluid types for different generators of a similar style
   * 
   * @return Fluid type for the tank to use
   */
  @Nonnull
  protected Fluid getFluidType() {
    return Fluids.NUTRIENT_DISTILLATION.getFluid();
  }

  public int getTicksPerBucketOfFuel() {
    return ticksPerBucketOfFuel;
  }
}
