package com.enderio.base.common.capability.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Optional;

public class EntityStorage implements IEntityStorage {
    private CompoundTag tag = new CompoundTag();

    @Nonnull
    @Override
    public Optional<ResourceLocation> getEntityType() {
        if (tag != null && tag.contains("id")) {
            return Optional.of(new ResourceLocation(tag.getString("id")));
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<CompoundTag> getEntityNBT() {
        if (tag != null)
            return Optional.of(tag);
        return Optional.empty();
    }

    @Override
    public void setEntityType(ResourceLocation entityType) {
        tag = new CompoundTag();
        tag.putString("id", entityType.toString());
    }

    @Override
    public void setEntityNBT(CompoundTag nbt) {
        tag = nbt;
    }

    public void empty() {
        tag = new CompoundTag();
    }

    @Override
    public Tag serializeNBT() {
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag)
            tag = compoundTag;
    }
}
