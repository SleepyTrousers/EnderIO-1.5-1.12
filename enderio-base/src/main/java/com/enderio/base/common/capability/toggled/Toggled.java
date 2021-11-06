package com.enderio.base.common.capability.toggled;

import com.enderio.base.common.capability.EIOCapabilities;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class Toggled implements IToggled {

    public static boolean isEnabled(ItemStack itemStack) {
        return itemStack.getCapability(EIOCapabilities.TOGGLED).map(IToggled::isEnabled).orElse(false);
    }

    public static void toggleEnabled(ItemStack itemStack) {
        itemStack.getCapability(EIOCapabilities.TOGGLED).ifPresent(IToggled::toggle);
    }

    public static void setEnabled(ItemStack itemStack, boolean enabled) {
        itemStack.getCapability(EIOCapabilities.TOGGLED).ifPresent( iToggled -> iToggled.setEnabled(enabled));
    }

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
