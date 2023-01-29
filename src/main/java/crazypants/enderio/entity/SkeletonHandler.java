package crazypants.enderio.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import crazypants.enderio.EnderIO;

public class SkeletonHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void skeletonJoinWorld(LivingSpawnEvent event) {
        if (!event.world.isRemote && isWitherSkele(event.entity)) {
            event.entity.setDead();
            event.world.spawnEntityInWorld(new EntityWitherSkeleton((EntitySkeleton) event.entity));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void skeletonJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && isWitherSkele(event.entity) && !event.entity.isDead) {
            event.setCanceled(true);
            event.world.spawnEntityInWorld(new EntityWitherSkeleton((EntitySkeleton) event.entity));
        }
    }

    private boolean isWitherSkele(Entity entity) {
        return entity.getClass() == EntitySkeleton.class && ((EntitySkeleton) entity).getSkeletonType() == 1;
    }

    public static void registerSkeleton(EnderIO mod) {
        int entityID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry
                .registerGlobalEntityID(EntityWitherSkeleton.class, "witherSkeleton", entityID, 0x00003D, 0x751947);
        EntityRegistry.registerModEntity(EntityWitherSkeleton.class, "witherSkeleton", entityID, mod, 64, 3, true);
        MinecraftForge.EVENT_BUS.register(new SkeletonHandler());
    }
}
