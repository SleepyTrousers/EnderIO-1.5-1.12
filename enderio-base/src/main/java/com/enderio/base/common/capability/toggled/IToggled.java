package com.enderio.base.common.capability.toggled;

import com.enderio.core.common.capability.INamedNBTSerializable;
import net.minecraft.nbt.Tag;

/**
 * Defines something that can be toggled, like an item.
 */
public interface IToggled extends INamedNBTSerializable<Tag> {
    @Override
    default String getSerializedName() {
        return "ToggleState";
    }

    /**
     * Get whether the toggleable is enabled.
     */
    boolean isEnabled();

    /**
     * Toggle whether this is enabled.
     */
    void toggle();

    /**
     * Set whether this is enabled.
     */
    void setEnabled(boolean isEnabled);

    @Override
    Tag serializeNBT();

    @Override
    void deserializeNBT(Tag nbt);
}
