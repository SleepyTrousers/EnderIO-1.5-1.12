package com.enderio.machines.common.blockentity;

import com.enderio.core.common.blockentity.sync.FluidStackDataSlot;
import com.enderio.core.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.blockentity.data.sidecontrol.fluid.FluidTankMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.enderio.machines.common.menu.MachineMenus;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class FluidTankBlockEntity extends AbstractMachineBlockEntity {

    @Getter
    private FluidTankMaster fluidTank = new FluidTankMaster(16 * FluidAttributes.BUCKET_VOLUME, getConfig());
    @Getter
    private ItemHandlerMaster itemHandlerMaster = new ItemHandlerMaster(getConfig(), 4, List.of(0,2), List.of(1,3));


    public FluidTankBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        addDataSlot(new FluidStackDataSlot(() -> fluidTank.getFluidInTank(0), fluidTank::setFluid, SyncMode.RENDER));
        itemHandlerMaster.addPredicate(0, itemStack ->
            (itemStack.getItem() instanceof BucketItem bucketItem && bucketItem.getFluid() != Fluids.EMPTY && !(bucketItem instanceof MobBucketItem))
                || (!(itemStack.getItem() instanceof BucketItem) && itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()));
        itemHandlerMaster.addPredicate(2, itemStack ->
            itemStack.getItem() == Items.BUCKET
            || (!(itemStack.getItem() instanceof BucketItem) && itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()));
    }

    @Override
    public CompoundTag save(CompoundTag pTag) {
        pTag.put("Items", itemHandlerMaster.serializeNBT());
        pTag.put("Fluids", fluidTank.writeToNBT(new CompoundTag()));
        return super.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandlerMaster.deserializeNBT(pTag.getCompound("Items"));
        fluidTank.readFromNBT(pTag.getCompound("Fluids"));
    }

    @Override
    public void tick() {
        if (isAction()) {
            fillInternal();
            drainInternal();
        }
        super.tick();
    }

    private void fillInternal() {
        ItemStack inputItem = itemHandlerMaster.getStackInSlot(0);
        ItemStack outputItem = itemHandlerMaster.getStackInSlot(1);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET && outputItem.getCount() < outputItem.getMaxStackSize())) {
                    int filled = fluidTank.fill(new FluidStack(filledBucket.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                    if (filled == FluidAttributes.BUCKET_VOLUME) {
                        fluidTank.fill(new FluidStack(filledBucket.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        itemHandlerMaster.forceInsertItem(1, Items.BUCKET.getDefaultInstance());
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();

                    int filled = moveFluids(itemFluid, fluidTank, fluidTank.getCapacity());
                    if (filled > 0) {
                        itemHandlerMaster.setStackInSlot(1, itemFluid.getContainer());
                        itemHandlerMaster.setStackInSlot(0, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private void drainInternal() {
        ItemStack inputItem = itemHandlerMaster.getStackInSlot(2);
        ItemStack outputItem = itemHandlerMaster.getStackInSlot(3);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                if (!fluidTank.isEmpty()) {
                    FluidStack stack = fluidTank.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
                    if (stack.getAmount() == FluidAttributes.BUCKET_VOLUME && (outputItem.isEmpty() || (outputItem.getItem() == stack.getFluid().getBucket() && outputItem.getCount() < outputItem.getMaxStackSize()))) {
                        fluidTank.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        if (outputItem.isEmpty()) {
                            itemHandlerMaster.setStackInSlot(3, stack.getFluid().getBucket().getDefaultInstance());
                        } else {
                            outputItem.grow(1);
                        }
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();
                    int filled = moveFluids(fluidTank, itemFluid, fluidTank.getFluidAmount());
                    if (filled > 0) {
                        itemHandlerMaster.setStackInSlot(3, itemFluid.getContainer());
                        itemHandlerMaster.setStackInSlot(2, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (direction != null) {
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return LazyOptional.of(() -> fluidTank.getAccess(direction)).cast();
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return LazyOptional.of(() -> itemHandlerMaster.getAccess(direction)).cast();
        }
        return super.getCapability(cap, direction);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> fluidTank).cast();
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> itemHandlerMaster).cast();
        return super.getCapability(cap);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FluidTankMenu(this, pInventory, pContainerId);
    }
}
