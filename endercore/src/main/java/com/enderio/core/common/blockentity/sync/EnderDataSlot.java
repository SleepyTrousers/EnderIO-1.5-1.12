package com.enderio.core.common.blockentity.sync;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class EnderDataSlot<T> {
    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    @Getter
    private final SyncMode syncMode;

    CompoundTag previousValue = new CompoundTag();


    public Optional<CompoundTag> toOptionalNBT() {
        CompoundTag newNBT = toFullNBT();
        if (newNBT.equals(previousValue))
            return Optional.empty();
        previousValue = newNBT;
        return Optional.of(newNBT);
    }

    public void handleNBT(CompoundTag tag) {
        setter.accept(fromNBT(tag));
    }

    public abstract CompoundTag toFullNBT();

    protected abstract T fromNBT(CompoundTag nbt);
}
