package com.enderio.base.common.capability.toggled;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;

public class Toggled implements IToggled {
    private boolean enabled = false;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        enabled = isEnabled;
    }

    @Override
    public Tag serializeNBT() {
        return ByteTag.valueOf(enabled);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (!(nbt instanceof ByteTag byteTag))
            throw new IllegalArgumentException("Incorrect NBT data read!");
        enabled = byteTag.getAsByte() != 0;
    }
}
