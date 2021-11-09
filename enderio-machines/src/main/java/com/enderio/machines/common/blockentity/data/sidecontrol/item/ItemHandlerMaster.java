package com.enderio.machines.common.blockentity.data.sidecontrol.item;

import com.enderio.machines.common.blockentity.data.sidecontrol.IOConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

public class ItemHandlerMaster extends ItemStackHandler {

    private final EnumMap<Direction, SidedItemHandlerAccess> access = new EnumMap(Direction.class);
    private final IOConfig config;

    private List<Integer> onlyInputs;
    private List<Integer> onlyOutputs;

    private Map<Integer, Predicate<ItemStack>> inputPredicates = new HashMap<>();
    private boolean isForceMode = false;

    public ItemHandlerMaster(IOConfig config, int size) {
        this(config, size, new ArrayList<>(), new ArrayList<>());
    }

    public ItemHandlerMaster(IOConfig config, int size, List<Integer> onlyInputs, List<Integer> onlyOutputs) {
        super(size);
        this.onlyInputs = onlyInputs;
        this.onlyOutputs = onlyOutputs;
        this.config = config;
    }

    public void addPredicate(int slot, Predicate<ItemStack> predicate) {
        if (onlyOutputs.contains(slot))
            throw new IllegalArgumentException("Tried to add an insert predicate to the output slot:" + slot);
        if (slot >= getSlots())
            throw new IllegalArgumentException("Tried to add an insert predicate to an invalid slot:" + slot);
        inputPredicates.put(slot, predicate);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (onlyOutputs.contains(slot))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack forceInsertItem(int slot, @Nonnull ItemStack stack) {
        isForceMode = true;
        ItemStack returnValue = super.insertItem(slot, stack, false);
        isForceMode = false;
        return returnValue;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return isForceMode || inputPredicates.getOrDefault(slot, itemStack -> true).test(stack);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (onlyInputs.contains(slot))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    public SidedItemHandlerAccess getAccess(Direction direction) {
        return access.computeIfAbsent(direction,
            dir -> new SidedItemHandlerAccess(this, dir));
    }

    public IOConfig getConfig() {
        return config;
    }
}
