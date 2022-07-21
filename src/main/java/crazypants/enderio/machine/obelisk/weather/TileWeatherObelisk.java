package crazypants.enderio.machine.obelisk.weather;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.api.common.util.ITankAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileWeatherObelisk extends AbstractPowerConsumerEntity
        implements IProgressTile, IFluidHandler, ITankAccess {

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
            if (f == EnderIO.fluidLiquidSunshine) {
                return CLEAR;
            } else if (f == EnderIO.fluidCloudSeed) {
                return RAIN;
            } else if (f == EnderIO.fluidCloudSeedConcentrated) {
                return STORM;
            }
            return null;
        }
    }

    private int fluidUsed = 0;
    private WeatherTask activeTask = null;

    private boolean canBeActive = true;
    private boolean tanksDirty;

    private static final ICapacitor cap = Capacitors.BASIC_CAPACITOR.capacitor;

    private FluidTank inputTank = new FluidTank(8000);

    /* client fields */
    private float progress = 0; // client only
    private boolean playedFuse = false;

    public TileWeatherObelisk() {
        super(new SlotDefinition(1, 0, 0));
    }

    @Override
    public void init() {
        setCapacitor(Capacitors.ACTIVATED_CAPACITOR);
    }

    @Override
    public String getMachineName() {
        return ModObject.blockWeatherObelisk.unlocalisedName;
    }

    @Override
    protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
        return i == 0 && itemstack != null && itemstack.getItem() == Items.fireworks;
    }

    @Override
    public boolean isActive() {
        return canBeActive && getActiveTask() != null;
    }

    @Override
    public float getProgress() {
        return isActive() ? worldObj.isRemote ? progress : (float) fluidUsed / 1000 : 0;
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
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public ICapacitor getCapacitor() {
        return cap;
    }

    public WeatherTask getActiveTask() {
        return activeTask;
    }

    @Override
    public void doUpdate() {
        super.doUpdate();
        if (worldObj.isRemote && isActive() && worldObj.getTotalWorldTime() % 2 == 0) {
            doLoadingParticles();
        }
    }

    @SideOnly(Side.CLIENT)
    private void doLoadingParticles() {
        if (progress < 0.9f) {
            Color c = getActiveTask().color;
            double correction = 0.1;
            double xf = xCoord + 0.5 + correction;
            double yf = yCoord + 0.8;
            double zf = zCoord + 0.5 + correction;
            Block b = getBlockType();
            double yi = yCoord + b.getBlockBoundsMaxY() - 0.1;
            double offset = 0.3;
            Minecraft.getMinecraft()
                    .effectRenderer
                    .addEffect(new EntityFluidLoadingFX(
                            worldObj, xCoord + offset + correction, yi, zCoord + offset + correction, xf, yf, zf, c));
            Minecraft.getMinecraft()
                    .effectRenderer
                    .addEffect(new EntityFluidLoadingFX(
                            worldObj,
                            xCoord + (1 - offset) + correction,
                            yi,
                            zCoord + offset + correction,
                            xf,
                            yf,
                            zf,
                            c));
            Minecraft.getMinecraft()
                    .effectRenderer
                    .addEffect(new EntityFluidLoadingFX(
                            worldObj,
                            xCoord + (1 - offset) + correction,
                            yi,
                            zCoord + (1 - offset) + correction,
                            xf,
                            yf,
                            zf,
                            c));
            Minecraft.getMinecraft()
                    .effectRenderer
                    .addEffect(new EntityFluidLoadingFX(
                            worldObj,
                            xCoord + offset + correction,
                            yi,
                            zCoord + (1 - offset) + correction,
                            xf,
                            yf,
                            zf,
                            c));
        } else if (!playedFuse) {
            worldObj.playSound(xCoord, yCoord, zCoord, "game.tnt.primed", 1, 1, true);
            playedFuse = true;
        }
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        boolean res = false;

        if (!redstoneCheckPassed) {
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
                    inputTank.drain(toUse, true);
                    fluidUsed += toUse;
                    tanksDirty = true;
                    res = true;
                }

                if (fluidUsed >= 1000) {
                    EntityWeatherRocket e = new EntityWeatherRocket(worldObj, activeTask);
                    e.setPosition(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
                    worldObj.spawnEntityInWorld(e);
                    stopTask();
                    res = true;
                }
            }
        }

        if (tanksDirty) {
            PacketHandler.sendToAllAround(new PacketWeatherTank(this), this);
            tanksDirty = false;
        }

        return res;
    }

    /**
     * If the task can be started based on the current inventory. Does not take
     * into account the world's weather state.
     *
     * @param task
     *          The task to check
     * @return True if the task can be started with the item in the inventory.
     */
    public boolean canStartTask(WeatherTask task) {
        return getActiveTask() == null
                && !WeatherTask.worldIsState(task, worldObj)
                && getStackInSlot(0) != null
                && inputTank.getFluidAmount() >= 1000;
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
            if (!worldObj.isRemote) {
                PacketHandler.INSTANCE.sendToDimension(new PacketActivateWeather(this), worldObj.provider.dimensionId);
            } else {
                playedFuse = false;
            }
        }
    }

    @Override
    public void writeCommon(NBTTagCompound nbtRoot) {
        super.writeCommon(nbtRoot);
        nbtRoot.setTag("tank", inputTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readCommon(NBTTagCompound nbtRoot) {
        super.readCommon(nbtRoot);
        inputTank.readFromNBT(nbtRoot.getCompoundTag("tank"));
    }

    private boolean isValidFluid(Fluid f) {
        return f == EnderIO.fluidLiquidSunshine
                || f == EnderIO.fluidCloudSeed
                || f == EnderIO.fluidCloudSeedConcentrated;
    }

    @Override
    public FluidTank getInputTank(FluidStack forFluidType) {
        return forFluidType != null && forFluidType.getFluid() != null && isValidFluid(forFluidType.getFluid())
                ? inputTank
                : null;
    }

    FluidTank getInputTank() {
        return inputTank;
    }

    @Override
    public FluidTank[] getOutputTanks() {
        return new FluidTank[0];
    }

    @Override
    public void setTanksDirty() {
        tanksDirty = true;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource != null && canFill(from, resource.getFluid())) {
            int res = inputTank.fill(resource, doFill);
            if (res > 0 && doFill) {
                tanksDirty = true;
            }
            return res;
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
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return isValidFluid(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] {inputTank.getInfo()};
    }
}
