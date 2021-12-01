package com.enderio.base.common.capability.entity;

import com.enderio.core.common.capability.INamedNBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A capability used for storing an entity inside of an item/block.
 */
public interface IEntityStorage extends INamedNBTSerializable<Tag> {
    @Override
    default String getSerializedName() {
        return "EntityStorage";
    }

    /**
     * Get the stored entity type.
     */
    @Nonnull
    Optional<ResourceLocation> getEntityType();

    /**
     * Get the entity NBT tag.
     * Generally used for creating the entity.
     */
    @Nonnull
    Optional<CompoundTag> getEntityNBT();

    float getEntityMaxHealth();

    /**
     * Set the stored entity type.
     */
    void setEntityType(ResourceLocation entityType);

    /**
     * Set the entity NBT.
     */
    void setEntityNBT(CompoundTag nbt);

    void setEntityMaxHealth(float maxHealth);
}
