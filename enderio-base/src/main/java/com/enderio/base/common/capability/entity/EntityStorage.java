package com.enderio.base.common.capability.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Optional;

public class EntityStorage implements IEntityStorage {
    private CompoundTag entityTag = new CompoundTag();
    private float maxHealth;

    @Nonnull
    @Override
    public Optional<ResourceLocation> getEntityType() {
        if (entityTag != null && entityTag.contains("id")) {
            return Optional.of(new ResourceLocation(entityTag.getString("id")));
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<CompoundTag> getEntityNBT() {
        if (entityTag != null)
            return Optional.of(entityTag);
        return Optional.empty();
    }

    @Override
    public float getEntityMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setEntityType(ResourceLocation entityType) {
        entityTag = new CompoundTag();
        entityTag.putString("id", entityType.toString());
    }

    @Override
    public void setEntityNBT(CompoundTag nbt) {
        entityTag = nbt;
    }

    public void empty() {
        entityTag = new CompoundTag();
    }

    @Override
    public Tag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("Entity", entityTag);
        compound.putFloat("MaxHealth", maxHealth);
        return compound;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            entityTag = compoundTag.getCompound("Entity");
            maxHealth = compoundTag.getFloat("MaxHealth");
        }
    }

    @Override
    public void setEntityMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }
}
