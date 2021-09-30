package com.enderio.base.common.blockentity;

import java.util.Collection;

import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.owner.IOwner;
import com.enderio.base.common.capability.owner.Owner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class GraveBlockEntity extends BlockEntity{    
    private Owner owner = new Owner();
    private LazyOptional<IOwner> ownerLazy = LazyOptional.of(() -> owner);
    private GraveItemStackHandler itemHandler = new GraveItemStackHandler();
    private LazyOptional<IItemHandler> itemLazy = LazyOptional.of(() -> itemHandler);

    public GraveBlockEntity(BlockEntityType<?> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState);
    }
    
    public void addDrops(Collection<ItemEntity> drops) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        drops.forEach(entity -> stacks.add(entity.getItem()));
        this.itemHandler.setItems(stacks);
    }
    
    public Collection<ItemStack> getItems() {
        return this.itemHandler.getItems();
    }

    
    @Override
    public void setRemoved() {
        ownerLazy.invalidate();
        itemLazy.invalidate();
        super.setRemoved();
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == EIOCapabilities.OWNER) {
            return this.ownerLazy.cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.itemLazy.cast();
        }
        return super.getCapability(cap, side);
    }
    
   @Override
   public void load(CompoundTag pTag) {
       owner.deserializeNBT(pTag.getCompound("owner"));
       itemHandler.deserializeNBT(pTag.getCompound("inv"));
       super.load(pTag);
   }
   
   @Override
   public CompoundTag save(CompoundTag pTag) {
       pTag.put("owner", owner.serializeNBT());
       pTag.put("inv", itemHandler.serializeNBT());
       return super.save(pTag);
   }
   
   private class GraveItemStackHandler extends ItemStackHandler  {
       
       public void setItems(NonNullList<ItemStack> items) {
           this.stacks = items;
       }
       
       public NonNullList<ItemStack> getItems() {
           return stacks;
       }
       
       @Override
       public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
           return stack;
       }
       
       @Override
       public ItemStack extractItem(int slot, int amount, boolean simulate) {
           return ItemStack.EMPTY;
       }    
   }
}
