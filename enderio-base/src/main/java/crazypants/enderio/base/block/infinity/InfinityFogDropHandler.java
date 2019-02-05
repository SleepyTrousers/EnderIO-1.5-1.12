package crazypants.enderio.base.block.infinity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.InfinityConfig;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.util.NbtValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class InfinityFogDropHandler {

  @SubscribeEvent
  public static void onDrop(LivingDropsEvent event) {
    Entity entity = event.getEntity();
    if (entity != null && NbtValue.INFINITY.getBoolean(entity.getEntityData()) && entity.world.rand.nextFloat() < InfinityConfig.dropChanceFogCreatures.get()) {
      EntityItem entityitem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, Material.POWDER_INFINITY.getStack());
      entityitem.setDefaultPickupDelay();
      event.getDrops().add(entityitem);
    }
  }

}
