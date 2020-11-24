package crazypants.enderio.machines.machine.niard;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.FluidUtil.FluidAndStackResult;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileNiard extends AbstractCapabilityMachineEntity implements ITankAccess.IExtendedTankAccess, IRanged {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  private static int IO_MB_TICK = 100;

  enum SLOT {
    INPUT,
    OUTPUT;
  }

  @Store
  protected final @Nonnull SmartTank inputTank = new SmartTank(2 * ONE_BLOCK_OF_LIQUID);
  protected final @Nonnull SmartTankFluidHandler smartTankFluidHandler = new SmartTankFluidMachineHandler(this, inputTank);
  protected final @Nonnull EngineNiard engine;
  protected int sleep = 20;
  protected boolean tanksDirty = false;

  public TileNiard() {
    super(CapacitorKey.NIARD_POWER_INTAKE, CapacitorKey.NIARD_POWER_BUFFER, CapacitorKey.NIARD_POWER_USE);
    engine = new EngineNiard(this);
    inputTank.setTileEntity(this);
    inputTank.setCanDrain(false);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, smartTankFluidHandler::get);
    getInventory().add(Type.INPUT, SLOT.INPUT, new InventorySlot(item -> item != null && FluidUtil.getFluidTypeFromItem(item) != null, -1));
    getInventory().add(Type.OUTPUT, SLOT.OUTPUT, new InventorySlot());
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected void processTasks(boolean redstoneCheck) {
    if (getEnergy().hasCapacitor() && canTick(redstoneCheck)) {
      doTick();
    }
    if (tanksDirty && shouldDoWorkThisTick(5)) {
      PacketHandler.sendToAllAround(new PacketNiardTank(this), this);
      tanksDirty = false;
    }
    super.processTasks(redstoneCheck);
  }

  protected boolean canTick(boolean redstoneChecksPassed) {
    if (redstoneChecksPassed) {
      if (!inputTank.isFull() && !isOutputQueued() && !getInventory().getSlot(SLOT.INPUT).isEmpty() && shouldDoWorkThisTick(20)) {
        drainFullContainer();
      }
      if (sleep > 0) {
        sleep--;
      }
      return getEnergy().useEnergy() && sleep == 0 && !inputTank.isEmpty() && getEnergy().canUseEnergy(CapacitorKey.NIARD_POWER_WORK);
    }
    return false;
  }

  protected void doTick() {
    if (shouldDoWorkThisTick(CapacitorKey.NIARD_DELAY.get(getCapacitorData()))) {
      if (inputTank.getFluidNN().getFluid() == Fluids.XP_JUICE.getFluid()) {
        doWorkXP();
      } else if (inputTank.getFluidAmount() >= ONE_BLOCK_OF_LIQUID && engine.setFluid(inputTank.getFluidNN().getFluid()).work()) {
        inputTank.setFluidAmount(inputTank.getFluidAmount() - ONE_BLOCK_OF_LIQUID);
        getEnergy().useEnergy(CapacitorKey.NIARD_POWER_WORK);
      } else {
        sleep = 200;
      }
    }

  }

  protected int getRange() {
    return CapacitorKey.NIARD_RANGE.get(getCapacitorData());
  }

  protected void doWorkXP() {
    int amount = inputTank.getFluidAmount();
    boolean looping = true;
    while (looping) {
      int remaining = engine.work(amount);
      if (remaining == amount || remaining == 0) {
        looping = false;
      }
      amount = remaining;
    }
    if (amount != inputTank.getFluidAmount()) {
      int usedFluid = inputTank.getFluidAmount() - amount;
      inputTank.setFluidAmount(amount);
      while (usedFluid > 0) {
        getEnergy().useEnergy(CapacitorKey.NIARD_POWER_WORK);
        usedFluid -= ONE_BLOCK_OF_LIQUID;
      }
    } else {
      sleep = 200;
    }
  }

  protected boolean drainFullContainer() {
    FluidAndStackResult fill = FluidUtil.tryDrainContainer(getInventory().getSlot(SLOT.INPUT).get(), this);
    if (fill.result.fluidStack == null) {
      return false;
    }

    getInputTank().setFluid(fill.remainder.fluidStack);
    getInventory().getSlot(SLOT.INPUT).set(fill.remainder.itemStack);
    if (Prep.isValid(fill.result.itemStack)) {
      addToOutputQueue(fill.result.itemStack);
    }

    setTanksDirty();
    markDirty();
    return false;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (super.doPull(dir)) {
      return true;
    }
    if (dir != null && !inputTank.isFull()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), inputTank, IO_MB_TICK) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
  }

  @Nonnull
  protected SmartTank getInputTank() {
    return inputTank;
  }

  @Override
  @Nullable
  public FluidTank getInputTank(FluidStack forFluidType) {
    return !inputTank.isFull() && isValidFluid(forFluidType) ? inputTank : null;
  }

  @Override
  @Nonnull
  public FluidTank[] getOutputTanks() {
    return new FluidTank[0];
  }

  @Override
  public void setTanksDirty() {
    tanksDirty = true;
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
        return inputTank.getFluid();
      }

      @Override
      public int getCapacity() {
        return inputTank.getCapacity();
      }
    });
  }

  protected int getFilledLevel() {
    int level = (int) Math.floor(16 * inputTank.getFilledRatio());
    if (level == 0 && inputTank.getFluidAmount() > 0) {
      level = 1;
    }
    return level;
  }

  // TODO add to block, see BlockTank
  public int getComparatorOutput() {
    return getFilledLevel();
  }

  @SuppressWarnings("static-method")
  protected boolean isValidFluid(Fluid fluid) {
    return fluid != null && (fluid.canBePlacedInWorld() || fluid == Fluids.XP_JUICE.getFluid());
  }

  protected boolean isValidFluid(FluidStack fluid) {
    return fluid != null && isValidFluid(fluid.getFluid());
  }

  // RANGE

  private boolean showingRange;

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  protected final static @Nonnull Vector4f fallbackColor = new Vector4f(145f / 255f, 82f / 255f, 21f / 255f, .4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Vector4f color4f = fallbackColor;
      final FluidStack fluid = inputTank.getFluid();
      if (fluid != null) {
        final int color = fluid.getFluid().getColor(fluid);
        if (color != 0xFFFFFFFF) {
          final float r = ((color >> 16) & 0xFF) / 255f, g = ((color >> 8) & 0xFF) / 255f, b = (color & 0xFF) / 255f, a = ((color >> 24) & 0xFF) / 255f;
          color4f = new Vector4f(r, g, b, a * .4f);
        } else {
          color4f = FluidColorUtil.getFluidColor(fluid, color4f);
        }
      }
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TileNiard>(this, color4f));
    }
  }

  @Override
  @Nonnull
  public BoundingBox getBounds() {
    final FluidStack fluid = inputTank.getFluid();
    if (fluid != null && fluid.getFluid().getDensity() <= 0) {
      return new BoundingBox(pos.south().east().up(), BlockCoord.withY(pos, 256)).expand(getRange(), 0, getRange());
    } else {
      return new BoundingBox(pos.south().east(), BlockCoord.withY(pos, 0)).expand(getRange(), 0, getRange());
    }
  }

  // RANGE END

}
