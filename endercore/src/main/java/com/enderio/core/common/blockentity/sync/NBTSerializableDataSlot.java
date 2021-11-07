package com.enderio.core.common.blockentity.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Supplier;

public class NBTSerializableDataSlot<T extends INBTSerializable<CompoundTag>> extends EnderDataSlot<T> {

    public NBTSerializableDataSlot(Supplier<T> getter, SyncMode syncMode) {
        //I can put null here, because I override the single usage of the setter
        super(getter, null, syncMode);
    }

    @Override
    public CompoundTag toFullNBT() {
        return getter().get().serializeNBT();
    }

    @Override
    protected T fromNBT(CompoundTag nbt) {
        //I can return null here, because I override the single usage of this method
        return null;
    }

    @Override
    public void handleNBT(CompoundTag tag) {
        getter().get().deserializeNBT(tag);
    }
}
