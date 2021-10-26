package com.enderio.core.common.capability;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public interface INamedNBTSerializable<T extends Tag> extends INBTSerializable<T> {
    /**
     * Get the serialized name.
     * Must not change based on the state!
     */
    String getSerializedName();
}
