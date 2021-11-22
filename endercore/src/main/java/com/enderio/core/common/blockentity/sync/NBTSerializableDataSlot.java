package com.enderio.core.common.blockentity.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Supplier;

public class NBTSerializableDataSlot<T extends INBTSerializable<CompoundTag>> extends EnderDataSlot<T> {

    /**
     * You can add a callback here, for a ModelData Reload for example, because a setter will never be called
     */
    private final Callback setterCallback;

    public NBTSerializableDataSlot(Supplier<T> getter, SyncMode syncMode) {
        this(getter, syncMode, () -> {});
    }

    public NBTSerializableDataSlot(Supplier<T> getter, SyncMode syncMode, Callback setterCallback) {
        //I can put null here, because I override the only usage of the setter
        super(getter, null, syncMode);
        this.setterCallback = setterCallback;
    }

    @Override
    public CompoundTag toFullNBT() {
        return getter().get().serializeNBT();
    }

    @Override
    protected T fromNBT(CompoundTag nbt) {
        //I can return null here, because I override the only usage of this method
        return null;
    }

    @Override
    public void handleNBT(CompoundTag tag) {
        getter().get().deserializeNBT(tag);
        setterCallback.call();
    }

    interface Callback {
        void call();
    }
}
