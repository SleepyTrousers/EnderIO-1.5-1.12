package com.enderio.machines.common.blockentity.data.sidecontrol.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class SidedFluidHandlerAccess implements IFluidHandler {

    private final FluidTankMaster master;
    private final Direction direction;

    public SidedFluidHandlerAccess(FluidTankMaster master, Direction direction) {
        this.master = master;
        this.direction = direction;
    }

    @Override
    public int getTanks() {
        return master.getTanks();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return master.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return master.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return master.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (master.getConfig().getIO(direction).canInput())
            return master.fill(resource, action);
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (master.getConfig().getIO(direction).canOutput())
            return master.drain(resource, action);
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (master.getConfig().getIO(direction).canOutput())
            return master.drain(maxDrain, action);
        return FluidStack.EMPTY;
    }
}
