package com.enderio.core.common.blockentity.sync;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumDataSlot<T extends Enum<T>> extends EnderDataSlot<T> {

    public EnumDataSlot(Supplier<T> getter, Consumer<T> setter, SyncMode syncMode) {
        super(getter, setter, syncMode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ordinal", getter().get().ordinal());
        return tag;
    }

    @Override
    protected T fromNBT(CompoundTag nbt) {
        return getter().get().getDeclaringClass().getEnumConstants()[nbt.getInt("ordinal")];
    }
}
