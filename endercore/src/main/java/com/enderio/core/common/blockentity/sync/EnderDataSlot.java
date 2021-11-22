package com.enderio.core.common.blockentity.sync;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class EnderDataSlot<T> {
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private final SyncMode syncMode;

    CompoundTag previousValue = new CompoundTag();

    public EnderDataSlot(Supplier<T> getter, Consumer<T> setter, SyncMode mode) {
        this.getter = getter;
        this.setter = setter;
        this.syncMode = mode;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    protected Supplier<T> getter() {
        return getter;
    }

    public Optional<CompoundTag> toOptionalNBT() {
        CompoundTag newNBT = toFullNBT();
        if (newNBT.equals(previousValue))
            return Optional.empty();
        //copy to prevent mismatched by adding the dataSlotIndex
        previousValue = newNBT.copy();
        return Optional.of(newNBT);
    }

    /**
     * Make sure to always retain a valid state, even when this method throws an Exception and only throw an Exception if invalid data is sent, as Clients can have full control over incoming data
     * @param tag
     */
    public void handleNBT(CompoundTag tag) {
        setter.accept(fromNBT(tag));
    }

    public abstract CompoundTag toFullNBT();

    protected abstract T fromNBT(CompoundTag nbt);
}
