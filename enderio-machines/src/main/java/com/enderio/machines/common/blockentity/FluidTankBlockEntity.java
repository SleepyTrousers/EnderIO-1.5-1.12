package com.enderio.machines.common.blockentity;

import com.enderio.core.common.blockentity.sync.FluidStackDataSlot;
import com.enderio.core.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.sidecontrol.fluid.FluidTankMaster;
import com.enderio.machines.common.sidecontrol.item.ItemHandlerMaster;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    private FluidTankMaster fluidTank = new FluidTankMaster(16 * FluidAttributes.BUCKET_VOLUME, getConfig());
    private ItemHandlerMaster itemHandlerMaster = new ItemHandlerMaster(getConfig(), 4, List.of(0,2), List.of(1,3));


    public FluidTankBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        addDataSlot(new FluidStackDataSlot(() -> fluidTank.getFluidInTank(0), SyncMode.RENDER));
        itemHandlerMaster.addPredicate(0, itemStack ->
            (itemStack.getItem() instanceof BucketItem bucketItem && bucketItem.getFluid() != Fluids.EMPTY && !(bucketItem instanceof MobBucketItem))
                || itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent());
        itemHandlerMaster.addPredicate(2, itemStack ->
            itemStack.getItem() == Items.BUCKET
            || itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent());
    }

    @Override
    public void tick() {
        if (isAction()) {
            ItemStack fullFluidContainer = itemHandlerMaster.getStackInSlot(0);
            ItemStack fullFluidContainerOutput = itemHandlerMaster.getStackInSlot(1);
            if (!fullFluidContainer.isEmpty()) {
                if (fullFluidContainer.getItem() instanceof BucketItem filledBucket) {
                    if (fullFluidContainerOutput.isEmpty()
                        || (fullFluidContainerOutput.getItem() == Items.BUCKET && fullFluidContainerOutput.getCount() < fullFluidContainerOutput.getMaxStackSize())) {
                        int filled = fluidTank.fill(new FluidStack(filledBucket.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                        if (filled == FluidAttributes.BUCKET_VOLUME) {
                            fullFluidContainer.shrink(1);
                            itemHandlerMaster.insertItem(1, Items.BUCKET.getDefaultInstance(), false);
                        }
                    }
                } else {
                    Optional<IFluidHandlerItem> fluidHandlerCap = fullFluidContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
                    if (fluidHandlerCap.isPresent()) {
                        IFluidHandlerItem fluidHandler = fluidHandlerCap.get();
                        if (fluidTank.getFluid().isEmpty()) {
                            FluidStack stack = fluidHandler.drain(fluidTank.getCapacity(), IFluidHandler.FluidAction.SIMULATE);
                            int filled = fluidTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                            stack.setAmount(filled);
                            fluidHandler.drain(stack, IFluidHandler.FluidAction.EXECUTE);
                        } else {
                            FluidStack toDrain = fluidTank.getFluid().copy();
                            toDrain.setAmount(fluidTank.getSpace());
                            FluidStack drained = fluidHandler.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);
                            int filled = fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                            drained.setAmount(filled);
                            fluidHandler.drain(drained, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
                //TODO: Fluid Emptying
            }
        }
        super.tick();
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
}
