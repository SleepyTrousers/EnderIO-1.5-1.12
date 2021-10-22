package com.enderio.base.common.util;

import com.enderio.core.common.util.EntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EntityCaptureUtils {
    // The id of the ender dragon for manual filtering.
    private static final ResourceLocation DRAGON = new ResourceLocation("minecraft", "ender_dragon");

    public static List<ResourceLocation> getCapturableEntities() {
        List<ResourceLocation> entities = new ArrayList<>();
        for (EntityType<?> type : ForgeRegistries.ENTITIES.getValues()) {
            if (type.getCategory() != MobCategory.MISC) {
                ResourceLocation key = ForgeRegistries.ENTITIES.getKey(type);
                if (key != null && !key.equals(DRAGON)) {
                    entities.add(key);
                }
            }
        }
        return entities;
    }

    public static boolean canCapture(Entity entity) {
        // TODO: Config for capture blacklist.
        return !isBlacklistedBoss(entity);
    }

    public static boolean isBlacklistedBoss(Entity entity) {
        return EntityUtil.getEntityType(entity).map(entityType -> EntityUtil.isBoss(entity) && !"minecraft".equals(entityType.getNamespace())).orElse(false);
    }
}
