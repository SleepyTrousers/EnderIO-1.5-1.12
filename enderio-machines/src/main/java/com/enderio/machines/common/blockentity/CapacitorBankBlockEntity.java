package com.enderio.machines.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapacitorBankBlockEntity extends BlockEntity {
    private EnergyStorage energyStorage;

    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    public CapacitorBankBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, int storage, boolean creative) {
        super(pType, pWorldPosition, pBlockState);
        energyStorage = new EnergyStorage(storage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energy.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag pTag) {
        energyStorage.deserializeNBT(pTag);
        super.load(pTag);
    }

    @Override
    public CompoundTag save(CompoundTag pTag) {
        pTag.put("Energy", energyStorage.serializeNBT());
        return super.save(pTag);
    }
}
