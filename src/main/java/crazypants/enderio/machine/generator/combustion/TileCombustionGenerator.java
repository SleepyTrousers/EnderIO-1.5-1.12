package crazypants.enderio.machine.generator.combustion;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.ModObject;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidCoolant;
import crazypants.enderio.fluid.IFluidFuel;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.AbstractGeneratorEntity;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerDistributor;

public class TileCombustionGenerator extends AbstractGeneratorEntity implements IFluidHandler, ITankAccess {

    private final FluidTank coolantTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
    private final FluidTank fuelTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
    private boolean tanksDirty;

    private int ticksRemaingFuel;
    private int ticksRemaingCoolant;
    private boolean active;

    private PowerDistributor powerDis;

    private int generated;

    private boolean inPause = false;

    private boolean generatedDirty = false;

    private int maxOutputTick = 1280;

    private static int IO_MB_TICK = 250;

    private IFluidFuel curFuel;
    private IFluidCoolant curCoolant;

    public TileCombustionGenerator() {
        super(new SlotDefinition(-1, -1, -1, -1, -1, -1));
    }

    @Override
    protected boolean doPull(ForgeDirection dir) {
        boolean res = super.doPull(dir);
        BlockCoord loc = getLocation().getLocation(dir);
        IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
        if (target != null) {
            FluidTankInfo[] infos = target.getTankInfo(dir.getOpposite());
            if (infos != null) {
                for (FluidTankInfo info : infos) {
                    if (info.fluid != null && info.fluid.amount > 0) {
                        if (canFill(dir, info.fluid.getFluid())) {
                            FluidStack canPull = info.fluid.copy();
                            canPull.amount = Math.min(IO_MB_TICK, canPull.amount);
                            FluidStack drained = target.drain(dir.getOpposite(), canPull, false);
                            if (drained != null && drained.amount > 0) {
                                int filled = fill(dir, drained, false);
                                if (filled > 0) {
                                    drained = target.drain(dir.getOpposite(), filled, true);
                                    fill(dir, drained, true);
                                    return res;
                                }
                            }
                        }
                    }
                }
            }
        }

        return res;
    }

    @Override
    public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
        return mode != IoMode.PUSH && mode != IoMode.PUSH_PULL;
    }

    @Override
    public String getMachineName() {
        return ModObject.blockCombustionGenerator.unlocalisedName;
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
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null || resource.getFluid() == null || !canFill(from, resource.getFluid())) {
            return 0;
        }
        int res = 0;

        IFluidCoolant cool = FluidFuelRegister.instance.getCoolant(resource.getFluid());
        if (cool != null) {
            res = getCoolantTank().fill(resource, doFill);
        } else {
            IFluidFuel f = FluidFuelRegister.instance.getFuel(resource.getFluid());
            if (f != null) {
                res = getFuelTank().fill(resource, doFill);
            }
        }
        if (res > 0) {
            tanksDirty = true;
        }
        return res;
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
            powerDis = new PowerDistributor(new BlockCoord(this));
        }
        int transmitted = powerDis.transmitEnergy(worldObj, Math.min(maxOutputTick, getEnergyStored()));
        setEnergyStored(getEnergyStored() - transmitted);
        return transmitted > 0;
    }

    private boolean generateEnergy() {

        generated = 0;

        if ((ticksRemaingCoolant <= 0 && getCoolantTank().getFluidAmount() <= 0)
                || (ticksRemaingFuel <= 0 && getFuelTank().getFluidAmount() <= 0)
                || getEnergyStored() >= getMaxEnergyStored()) {
            return false;
        }

        // once full, don't start again until we have drained 10 seconds worth of power to prevent
        // flickering on and off constantly when powering a machine that draws less than this produces
        if (inPause) {
            int powerPerCycle = getPowerPerCycle();
            if (getEnergyStored() >= (getMaxEnergyStored() - (powerPerCycle * 200))
                    && getEnergyStored() > (getMaxEnergyStored() / 8)) {
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
            FluidStack drained = getFuelTank().drain(100, true);
            if (drained == null) {
                return false;
            }
            ticksRemaingFuel = getNumTicksPerMbFuel(curFuel) * drained.amount;

            tanksDirty = true;
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
            FluidStack drained = getCoolantTank().drain(100, true);
            if (drained == null) {
                return false;
            }
            ticksRemaingCoolant = getNumTicksPerMbCoolant(curCoolant, curFuel) * drained.amount;
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
        if (worldObj.isRemote) {
            curFuel = FluidFuelRegister.instance.getFuel(getFuelTank().getFluid());
            updateCoolantFromTank();
        }
        return getNumTicksPerMbCoolant(curCoolant, curFuel);
    }

    static int getNumTicksPerMbFuel(IFluidFuel fuel) {
        if (fuel == null) {
            return 0;
        }
        return fuel.getTotalBurningTime() / 1000;
    }

    public static float HEAT_PER_RF = 0.00023F;

    static int getNumTicksPerMbCoolant(IFluidCoolant coolant, IFluidFuel fuel) {
        if (coolant == null || fuel == null) {
            return 0;
        }
        float power = fuel.getPowerPerCycle();
        float cooling = coolant.getDegreesCoolingPerMB(100);
        double toCool = 1d / (HEAT_PER_RF * power);
        int numTicks = (int) Math.round(toCool / (cooling * 1000));
        return numTicks;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (isSideDisabled(from.ordinal()) || fluid == null) {
            return false;
        }
        return FluidFuelRegister.instance.getCoolant(fluid) != null
                || FluidFuelRegister.instance.getFuel(fluid) != null;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (isSideDisabled(from.ordinal())) {
            return new FluidTankInfo[0];
        }
        return new FluidTankInfo[] { getCoolantTank().getInfo(), getFuelTank().getInfo() };
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtRoot) {
        super.readCustomNBT(nbtRoot);
        active = nbtRoot.getBoolean("active");
        generated = nbtRoot.getInteger("generatedRF");
    }

    @Override
    public void readCommon(NBTTagCompound nbtRoot) {
        super.readCommon(nbtRoot);
        if (nbtRoot.hasKey("coolantTank")) {
            NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("coolantTank");
            if (tankRoot != null) {
                getCoolantTank().readFromNBT(tankRoot);
            } else {
                getCoolantTank().setFluid(null);
            }
        } else {
            getCoolantTank().setFluid(null);
        }

        if (nbtRoot.hasKey("fuelTank")) {
            NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("fuelTank");
            if (tankRoot != null) {
                getFuelTank().readFromNBT(tankRoot);
            } else {
                getFuelTank().setFluid(null);
            }
        } else {
            getFuelTank().setFluid(null);
        }

        ticksRemaingFuel = nbtRoot.getInteger("ticksRemaingFuel");
        ticksRemaingCoolant = nbtRoot.getInteger("ticksRemaingCoolant");
    }

    @Override
    public void writeCommon(NBTTagCompound nbtRoot) {
        super.writeCommon(nbtRoot);
        if (getCoolantTank().getFluidAmount() > 0) {
            NBTTagCompound tankRoot = new NBTTagCompound();
            getCoolantTank().writeToNBT(tankRoot);
            nbtRoot.setTag("coolantTank", tankRoot);
        }
        if (getFuelTank().getFluidAmount() > 0) {
            NBTTagCompound tankRoot = new NBTTagCompound();
            getFuelTank().writeToNBT(tankRoot);
            nbtRoot.setTag("fuelTank", tankRoot);
        }
        nbtRoot.setInteger("ticksRemaingFuel", ticksRemaingFuel);
        nbtRoot.setInteger("ticksRemaingCoolant", ticksRemaingCoolant);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
        super.writeCustomNBT(nbtRoot);
        nbtRoot.setBoolean("active", active);
        nbtRoot.setInteger("generatedRF", generated);
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

    public FluidTank getCoolantTank() {
        return coolantTank;
    }

    public FluidTank getFuelTank() {
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
        return new FluidTank[] {
                /* coolantTank, fuelTank */
        };
    }

    @Override
    public void setTanksDirty() {
        tanksDirty = true;
    }
}
