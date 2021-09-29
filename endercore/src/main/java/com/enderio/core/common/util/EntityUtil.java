package com.enderio.core.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

/**
 * Helper utilities for dealing with entities.
 */
public class EntityUtil {
    /**
     * Get the description ID from an entity type in the registry.
     * @param entityType The entity type to get a description ID for.
     * @return The description ID.
     */
    public static String getEntityDescriptionId(ResourceLocation entityType) {
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(entityType);
        if (type == null)
            return "error"; // TODO: Proper key
        return type.getDescriptionId();
    }

    /**
     * Determine whether the entity is a boss.
     * @param entity The entity being checked.
     * @return Whether the entity is a boss.
     */
    // This is added to make it clearer; because canChangeDimensions will look odd in the code
    public static boolean isBoss(Entity entity) {
        // The only mobs that cannot change dimensions are infact the bosses, so this works :)
        return entity.canChangeDimensions();
    }

    /**
     * Lookup an entity's type in the entity registry and get its resource location.
     * @param entity The entity to lookup.
     * @return The resource location of the entity type.
     */
    public static Optional<ResourceLocation> getEntityType(Entity entity) {
        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(entity.getType());
        if (id != null)
            return Optional.of(id);
        return Optional.empty();
    }
}
