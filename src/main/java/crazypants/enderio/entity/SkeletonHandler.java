package crazypants.enderio.entity;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import crazypants.enderio.EnderIO;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class SkeletonHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void skeletonJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.entity.getClass() == EntitySkeleton.class && ((EntitySkeleton)event.entity).getSkeletonType()==1)
        {
            event.setCanceled(true);
            event.world.spawnEntityInWorld(new EntityWitherSkeleton((EntitySkeleton)event.entity));
        }
    }

    public static void registerSkeleton(EnderIO mod)
    {
        int entityID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(EntityWitherSkeleton.class,"witherSkeleton", entityID, 0x00003D, 0x751947);
        EntityRegistry.registerModEntity(EntityWitherSkeleton.class,"witherSkeleton",entityID,mod,64,3,true);
        MinecraftForge.EVENT_BUS.register(new SkeletonHandler());
    }
}
