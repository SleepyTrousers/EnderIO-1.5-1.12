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

import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.WeatherConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

  // TODO: these should be recipes, not fixed tasks
  public enum WeatherTask {
    CLEAR(Color.YELLOW) {
      @Override
      void complete(World world) {
        rain(world, false);
        thunder(world, false);
      }

      @Override
      public int getRequiredFluidAmount() {
        return WeatherConfig.weatherObeliskClearFluidAmount.get();
      }

      @Override
      @Nonnull
      public Fluid getRequiredFluidAmountType() {
        return WeatherConfig.weatherObeliskClearFluidType.get();
      }
    },
    RAIN(new Color(120, 120, 255)) {
      @Override
      void complete(World world) {
        rain(world, true);
        thunder(world, false);
      }

      @Override
      public int getRequiredFluidAmount() {
        return WeatherConfig.weatherObeliskRainFluidAmount.get();
      }

      @Override
      @Nonnull
      public Fluid getRequiredFluidAmountType() {
        return WeatherConfig.weatherObeliskRainFluidType.get();
      }
    },
    STORM(Color.DARK_GRAY) {
      @Override
      void complete(World world) {
        rain(world, true);
        thunder(world, true);
      }

      @Override
      public int getRequiredFluidAmount() {
        return WeatherConfig.weatherObeliskThunderFluidAmount.get();
      }

      @Override
      @Nonnull
      public Fluid getRequiredFluidAmountType() {
        return WeatherConfig.weatherObeliskThunderFluidType.get();
      }
    };

    final Color color;

    WeatherTask(Color color) {
      this.color = color;
    }

    abstract void complete(World world);

    public abstract int getRequiredFluidAmount();

    public abstract @Nonnull Fluid getRequiredFluidAmountType();

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

    public static @Nullable WeatherTask fromFluid(Fluid f) {
      for (WeatherTask task : values()) {
        if (f == task.getRequiredFluidAmountType()) {
          return task;
        }
      }
      return null;
    }
  }

  @Store
  private int fluidUsed = 0;
  private WeatherTask activeTask = null;

  @Store
  private boolean redstoneActivePrev;

  private boolean canBeActive = true;
  private boolean tanksDirty;

  @Store
  private final @Nonnull SmartTank inputTank = new SmartTank(WeatherConfig.tankSize.get()) {

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
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facingIn -> getSmartTankFluidHandler().get(facingIn));
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
    return isActive() ? world.isRemote ? progress : (float) fluidUsed / getActiveTask().getRequiredFluidAmount() : 0;
  }

  @Override
  public void setProgress(float progress) {
    this.progress = progress;
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
      double yi = pos1.getY() + bs.getBoundingBox(world, pos).maxY - 0.1;
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
  protected void processTasks(boolean redstoneCheck) {
    if (!redstoneCheck) {
      if (canBeActive) {
        canBeActive = false;
        updateClients = true;
      }
      return;
    } else {
      canBeActive = true;

      if (!world.isRemote && fluidUsed > 0 && activeTask == null && !inputTank.isEmpty()) {
        // we were saved while a task was active. Luckily those are easy to recreate
        activeTask = WeatherTask.fromFluid(inputTank.getFluidNN().getFluid());
        PacketHandler.INSTANCE.sendToAllAround(new PacketActivateWeather(this, activeTask != null), this);
      }

      if (isActive()) {
        if (getEnergyStored() > getPowerUsePerTick() && !inputTank.isEmpty()) {
          setEnergyStored(getEnergyStored() - getPowerUsePerTick());
          fluidUsed += inputTank
              .removeFluidAmount(Math.min(CapacitorKey.WEATHER_POWER_FLUID_USE.get(getCapacitorData()), getActiveTask().getRequiredFluidAmount() - fluidUsed));
        }

        if (fluidUsed >= getActiveTask().getRequiredFluidAmount()) {
          EntityWeatherRocket e = new EntityWeatherRocket(world, activeTask);
          e.setPosition(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
          world.spawnEntity(e);
          stopTask();
          updateClients = true;
        }
        // we have a very smooth block animation, so all clients need very detailed progress data
        // TODO: check if this is really needed for the WeatherObelisk
        PacketHandler.INSTANCE.sendToAllAround(getProgressPacket(), this);
      }
    }

    if (tanksDirty && shouldDoWorkThisTick(5)) {
      PacketHandler.sendToAllAround(new PacketWeatherTank(this), this);
      tanksDirty = false;
    }
  }

  /**
   * If the task can be started based on the current inventory. Does not take into account the world's weather state.
   * 
   * @param task
   *          The task to check
   * @return True if the task can be started with the item in the inventory.
   */
  public boolean canStartTask(WeatherTask task) {
    return task != null && getActiveTask() == null && !WeatherTask.worldIsState(task, world) && inputTank.getFluidAmount() >= task.getRequiredFluidAmount()
        && task == WeatherTask.fromFluid(inputTank.getFluidNN().getFluid()) && (world.isRemote || Prep.isValid(getStackInSlot(0)));
  }

  /**
   * @return If the operation was successful.
   */
  public boolean startTask() {
    if (getActiveTask() == null && inputTank.getFluidAmount() > 0) {
      fluidUsed = 0;
      WeatherTask task = WeatherTask.fromFluid(inputTank.getFluidNN().getFluid());
      if (canStartTask(task)) {
        getStackInSlot(0).shrink(1);
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
        PacketHandler.INSTANCE.sendToAllAround(new PacketActivateWeather(this, false), this);
      } else {
        playedFuse = false;
      }
    }
  }

  private boolean isValidFluid(Fluid f) {
    return WeatherTask.fromFluid(f) != null;
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

  public void updateRedstoneState() {
    boolean redstone = isPoweredRedstone();
    if (!redstoneActivePrev && redstone) {
      startTask();
      PacketHandler.INSTANCE.sendToAllAround(new PacketActivateWeather(this, activeTask != null), this);
    }
    redstoneActivePrev = redstone;
  }

}
