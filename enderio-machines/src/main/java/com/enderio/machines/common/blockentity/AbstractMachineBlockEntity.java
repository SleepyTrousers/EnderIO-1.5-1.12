package com.enderio.machines.common.blockentity;

import com.enderio.core.common.blockentity.SyncedBlockEntity;
import com.enderio.core.common.blockentity.sync.EnumDataSlot;
import com.enderio.core.common.blockentity.sync.NBTSerializableDataSlot;
import com.enderio.core.common.blockentity.sync.SyncMode;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.machines.common.blockentity.data.sidecontrol.IOConfig;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.EnumMap;
import java.util.Optional;

public abstract class AbstractMachineBlockEntity extends SyncedBlockEntity implements MenuProvider {

    private final IOConfig config = new IOConfig();

    @Getter
    @Setter
    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    private final EnumMap<Direction, LazyOptional<IItemHandler>> itemHandlerCache = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> fluidHandlerCache = new EnumMap<>(Direction.class);
    private boolean isCacheDirty = false;

    public AbstractMachineBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        add2WayDataSlot(new EnumDataSlot<>(this::getRedstoneControl, this::setRedstoneControl, SyncMode.GUI));
        add2WayDataSlot(new NBTSerializableDataSlot<>(() -> config, SyncMode.RENDER));
    }

    public final IOConfig getConfig() {
        return this.config;
    }

    public abstract ItemHandlerMaster getItemHandlerMaster();

    public void updateCache() {
        itemHandlerCache.clear();
        fluidHandlerCache.clear();
        for (Direction direction: Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor != null) {
                itemHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite())));
                fluidHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite())));
            } else {
                itemHandlerCache.put(direction, LazyOptional.empty());
                fluidHandlerCache.put(direction, LazyOptional.empty());
            }
        }
    }

    /**
     * needs to be called to prevent an instant call of the listener if the capability is not present
     * @param capability
     * @param <T>
     * @return
     */
    private <T> LazyOptional<T> addInvalidationListener(LazyOptional<T> capability) {
        if (capability.isPresent())
            capability.addListener(this::markCacheDirty);
        return capability;
    }
    private <T> void markCacheDirty(LazyOptional<T> capability) {
        isCacheDirty = true;
    }


    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AbstractMachineBlockEntity pBlockEntity) {
        pBlockEntity.tick();
    }
    @Override
    public void tick() {
        if (isCacheDirty) {
            updateCache();
        }
        if (isAction()) {
            for (Direction direction : Direction.values()) {
                if (config.getIO(direction).canForce()) {
                    moveItems(direction);
                    moveFluids(direction);
                }
            }
        }
        super.tick();
    }

    public boolean isAction() {
        return !level.isClientSide && level.getGameTime() % 5 == 0
            && redstoneControl.isActive(level.hasNeighborSignal(worldPosition));
    }

    private void moveFluids(Direction direction) {
        getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).resolve().ifPresent(fluidHandler -> {
            Optional<IFluidHandler> otherFluid = fluidHandlerCache.get(direction).resolve();
            if (otherFluid.isPresent()) {
                FluidStack stack = fluidHandler.drain(100, FluidAction.SIMULATE);
                if (stack.isEmpty()) {
                    moveFluids(otherFluid.get(), fluidHandler, 100);
                } else {
                    moveFluids(fluidHandler, otherFluid.get(), 100);
                }
            }
        });
    }
    public int moveFluids(IFluidHandler from, IFluidHandler to, int maxDrain) {
        FluidStack stack = from.drain(maxDrain, FluidAction.SIMULATE);
        int filled = to.fill(stack, FluidAction.EXECUTE);
        stack.setAmount(filled);
        from.drain(stack, FluidAction.EXECUTE);
        return filled;
    }

    private void moveItems(Direction direction) {
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().ifPresent(itemHandler -> {
            Optional<IItemHandler> otherItem = itemHandlerCache.get(direction).resolve();
            if (otherItem.isPresent()) {
                moveItems(itemHandler, otherItem.get());
                moveItems(otherItem.get(), itemHandler);
            }
        });
    }

    private void moveItems(IItemHandler from, IItemHandler to) {
        boolean shouldStop = false;
        for (int i = 0; i < from.getSlots(); i++) {
            if (shouldStop)
                break;
            ItemStack extracted = from.extractItem(i, 1, true);
            if (!extracted.isEmpty()) {
                for (int j = 0; j < to.getSlots(); j++) {
                    ItemStack inserted = to.insertItem(j, extracted, false);
                    if (inserted.isEmpty()) {
                        from.extractItem(i, 1, false);
                        shouldStop = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag pTag) {
        pTag.put("io_config", config.serializeNBT());
        pTag.putInt("redstone", redstoneControl.ordinal());
        return super.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        config.deserializeNBT(pTag.getCompound("io_config"));
        redstoneControl = RedstoneControl.values()[pTag.getInt("redstone")];
        super.load(pTag);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }
}
