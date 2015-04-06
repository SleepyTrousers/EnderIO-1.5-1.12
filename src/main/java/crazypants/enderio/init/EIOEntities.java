package crazypants.enderio.init;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.SoundEntity;
import crazypants.enderio.item.darksteel.SoundRenderer;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.machine.ranged.RangeRenerer;

public class EIOEntities {

  public static void registerEntities() {
    int entityID = EntityRegistry.findGlobalUniqueEntityId();
    EntityRegistry.registerGlobalEntityID(SoundEntity.class, "soundEntity", entityID);
    EntityRegistry.registerModEntity(SoundEntity.class, "soundEntity", entityID, EnderIO.instance, 0, 0, false);
  
    entityID = EntityRegistry.findGlobalUniqueEntityId();
    EntityRegistry.registerGlobalEntityID(RangeEntity.class, "rangeEntity", entityID);
    EntityRegistry.registerModEntity(RangeEntity.class, "rangeEntity", entityID, EnderIO.instance, 0, 0, false);
  }

  @SideOnly(Side.CLIENT)
  public static void registerEntityRenderers() {
    RenderingRegistry.registerEntityRenderingHandler(SoundEntity.class, new SoundRenderer());
    RenderingRegistry.registerEntityRenderingHandler(RangeEntity.class, new RangeRenerer());
  }

}
