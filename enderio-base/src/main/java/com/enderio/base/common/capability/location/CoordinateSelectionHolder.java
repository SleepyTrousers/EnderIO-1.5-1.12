package com.enderio.base.common.capability.location;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class CoordinateSelectionHolder implements ICoordinateSelectionHolder {
    private CoordinateSelection selection;

    @Override
    public CoordinateSelection getSelection() {
        return selection;
    }

    @Override
    public void setSelection(CoordinateSelection selection) {
        this.selection = selection;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (hasSelection()) {
            nbt.putString("level", selection.getLevel().toString());
            nbt.put("pos", NbtUtils.writeBlockPos(selection.getPos()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag nbt && !nbt.isEmpty()) {
            selection = CoordinateSelection.of(new ResourceLocation(nbt.getString("level")),
                NbtUtils.readBlockPos(nbt.getCompound("pos")));
        }
    }
}
