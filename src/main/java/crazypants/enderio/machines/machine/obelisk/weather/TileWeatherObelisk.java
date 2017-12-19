package crazypants.enderio.machines.machine.obelisk.weather;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.capacitor.CapacitorKey.WEATHER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.WEATHER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.WEATHER_POWER_USE;

@Storable
public class TileWeatherObelisk extends AbstractPowerConsumerEntity implements IProgressTile, ITankAccess.IExtendedTankAccess {

  public enum WeatherTask {
    CLEAR(Color.YELLOW) {
      @Override
      void complete(World world) {
        rain(world, false);
        thunder(world, false);
      }
    },
    RAIN(new Color(120, 120, 255)) {
      @Override
      void complete(World world) {
        rain(world, true);
        thunder(world, false);
      }
    },
    STORM(Color.DARK_GRAY) {
      @Override
      void complete(World world) {
        rain(world, true);
        thunder(world, true);
      }
    };

    final Color color;

    WeatherTask(Color color) {
      this.color = color;
    }

    abstract void complete(World world);

    protected void rain(World world, boolean state) {
      world.getWorldInfo().setRaining(state);
    }

    protected void thunder(World world, boolean state) {
      world.getWorldInfo().setThundering(state);
    }

    public static boolean worldIsState(WeatherTask task, World world) {
      if (world.isRaining()) {
        return world.isThundering() ? task == STORM : task == RAIN;
      }
      return task == CLEAR;
    }

    public static WeatherTask fromFluid(Fluid f) {
      if (f == Fluids.LIQUID_SUNSHINE.getFluid()) {
        return CLEAR;
      } else if (f == Fluids.CLOUD_SEED.getFluid()) {
        return RAIN;
      } else if (f == Fluids.CLOUD_SEED_CONCENTRATED.getFluid()) {
        return STORM;
      }
      return null;
    }
  }

  private int fluidUsed = 0;
  private WeatherTask activeTask = null;

  private boolean canBeActive = true;
  private boolean tanksDirty;

  @Store
  private final @Nonnull SmartTank inputTank = new SmartTank(8000) {

    @Override
    public boolean canFillFluidType(@Nullable FluidStack resource) {
      return super.canFillFluidType(resource) && resource != null && isValidFluid(resource.getFluid());
    }

  };

  /* client fields */
  private float progress = 0; // client only
  private boolean playedFuse = false;

  public TileWeatherObelisk() {
    super(new SlotDefinition(1, 0, 0), WEATHER_POWER_INTAKE, WEATHER_POWER_BUFFER, WEATHER_POWER_USE);
    inputTank.setTileEntity(this);
    inputTank.setCanDrain(false);
  }

  @Override
  public void init() {
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_weather_obelisk.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return i == 0 && itemstack.getItem() == Items.FIREWORKS;
  }

  @Override
  public boolean isActive() {
    return canBeActive && getActiveTask() != null;
  }

  @Override
  public float getProgress() {
    return isActive() ? world.isRemote ? progress : (float) fluidUsed / 1000 : 0;
  }

  @Override
  public void setProgress(float progress) {
    this.progress = progress;
  }

  @Override
  protected int getProgressUpdateFreq() {
    return 3;
  }

  @Override
  public @Nonnull TileEntity getTileEntity() {
    return this;
  }

  public WeatherTask getActiveTask() {
    return activeTask;
  }

  @Override
  public void doUpdate() {
    super.doUpdate();
    if (world.isRemote && isActive() && world.getTotalWorldTime() % 2 == 0) {
      doLoadingParticles();
    }
  }

  @SideOnly(Side.CLIENT)
  private void doLoadingParticles() {
    if (progress < 0.9f) {
      Color c = getActiveTask().color;
      double correction = 0.1;
      BlockPos pos1 = getPos();
      double xf = pos1.getX() + 0.5 + correction;
      double yf = pos1.getY() + 0.8;
      double zf = pos1.getZ() + 0.5 + correction;

      IBlockState bs = world.getBlockState(pos);
      // Block b = getBlockType();
      // double yi = pos1.getY() + b.getBlockBoundsMaxY() - 0.1;
      double yi = bs.getBoundingBox(world, pos).maxY - 01.;
      double offset = 0.3;
      Minecraft.getMinecraft().effectRenderer
          .addEffect(new EntityFluidLoadingFX(world, pos1.getX() + offset + correction, yi, pos1.getZ() + offset + correction, xf, yf, zf, c));
      Minecraft.getMinecraft().effectRenderer
          .addEffect(new EntityFluidLoadingFX(world, pos1.getX() + (1 - offset) + correction, yi, pos1.getZ() + offset + correction, xf, yf, zf, c));
      Minecraft.getMinecraft().effectRenderer
          .addEffect(new EntityFluidLoadingFX(world, pos1.getX() + (1 - offset) + correction, yi, pos1.getZ() + (1 - offset) + correction, xf, yf, zf, c));
      Minecraft.getMinecraft().effectRenderer
          .addEffect(new EntityFluidLoadingFX(world, pos1.getX() + offset + correction, yi, pos1.getZ() + (1 - offset) + correction, xf, yf, zf, c));
    } else if (!playedFuse) {
      world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1, 1, true);
      playedFuse = true;
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    boolean res = false;

    if (!redstoneCheck) {
      if (canBeActive) {
        canBeActive = false;
        res = true;
      }
      return res;
    } else {
      canBeActive = true;

      if (isActive()) {
        if (getEnergyStored() > getPowerUsePerTick() && inputTank.getFluidAmount() > 3) {
          setEnergyStored(getEnergyStored() - getPowerUsePerTick());

          int toUse = 4;
          inputTank.removeFluidAmount(toUse);
          fluidUsed += toUse;
        }

        if (fluidUsed >= 1000) {
          EntityWeatherRocket e = new EntityWeatherRocket(world, activeTask);
          e.setPosition(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
          world.spawnEntity(e);
          stopTask();
          res = true;
        }
      }
    }

    if (tanksDirty && shouldDoWorkThisTick(5)) {
      PacketHandler.sendToAllAround(new PacketWeatherTank(this), this);
      tanksDirty = false;
    }

    return res;
  }

  /**
   * If the task can be started based on the current inventory. Does not take into account the world's weather state.
   * 
   * @param task
   *          The task to check
   * @return True if the task can be started with the item in the inventory.
   */
  public boolean canStartTask(WeatherTask task) {
    return task != null && getActiveTask() == null && !WeatherTask.worldIsState(task, world) && Prep.isValid(getStackInSlot(0))
        && inputTank.getFluidAmount() >= 1000 && task == WeatherTask.fromFluid(inputTank.getFluid().getFluid());
  }

  /**
   * @return If the operation was successful.
   */
  public boolean startTask() {
    if (getActiveTask() == null && inputTank.getFluidAmount() > 0) {
      fluidUsed = 0;
      WeatherTask task = WeatherTask.fromFluid(inputTank.getFluid().getFluid());
      if (canStartTask(task)) {
        decrStackSize(0, 1);
        activeTask = task;
        return true;
      }
    }
    return false;
  }

  public void stopTask() {
    if (getActiveTask() != null) {
      activeTask = null;
      fluidUsed = 0;
      if (!world.isRemote) {
        PacketHandler.INSTANCE.sendToDimension(new PacketActivateWeather(this), world.provider.getDimension());
      } else {
        playedFuse = false;
      }
    }
  }

  private boolean isValidFluid(Fluid f) {
    return f == Fluids.LIQUID_SUNSHINE.getFluid() || f == Fluids.CLOUD_SEED.getFluid() || f == Fluids.CLOUD_SEED_CONCENTRATED.getFluid();
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return forFluidType != null && forFluidType.getFluid() != null && isValidFluid(forFluidType.getFluid()) ? inputTank : null;
  }

  @Nonnull
  FluidTank getInputTank() {
    return inputTank;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
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

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, inputTank);
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

}
