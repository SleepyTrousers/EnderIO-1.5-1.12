package crazypants.enderio;

import java.util.Locale;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.registry.GameRegistry.Type;

public class MigrationMapper {

  public static void create() {
    MinecraftForge.EVENT_BUS.register(MigrationMapper.class);
  }

  public static void handleMappings(FMLMissingMappingsEvent event) {
    for (MissingMapping mapping : event.get()) {
      final String resourcePath = mapping.resourceLocation.getResourcePath();
      if ("blockEnderIo".equals(resourcePath)) {
        mapping.ignore();
      } else if (mapping.type == Type.BLOCK && "blockConduitFacade".equals(resourcePath)) {
        mapping.ignore();
      } else {
        try {
          ModObject modObject = ModObject.valueOf(resourcePath.toLowerCase(Locale.ENGLISH));
          if (modObject != null) {
            if (mapping.type == Type.BLOCK && modObject.getBlock() != null) {
              mapping.remap(modObject.getBlock());
            } else if (mapping.type == Type.ITEM && modObject.getItem() != null) {
              mapping.remap(modObject.getItem());
            }
          }
        } catch (Exception e) {
        }
      }
    }
  }

}