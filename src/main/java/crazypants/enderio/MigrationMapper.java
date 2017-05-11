package crazypants.enderio;

import java.util.Locale;

import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.registry.GameRegistry.Type;

public class MigrationMapper {

  public static void handleMappings(FMLMissingMappingsEvent event) {
    for (MissingMapping mapping : event.getAll()) {
      if (EnderIO.DOMAIN.equals(mapping.resourceLocation.getResourceDomain())) {
        final String resourcePath = mapping.resourceLocation.getResourcePath();
        if ("blockEnderIo".equals(resourcePath)) {
          mapping.ignore();
        } else if (mapping.type == Type.BLOCK && "blockConduitFacade".equals(resourcePath)) {
          mapping.ignore();
        } else if (mapping.type == Type.ITEM && "blockConduitFacade".equals(resourcePath)) {
          mapping.remap(ModObject.itemConduitFacade.getItem());
        } else {
          try {
            ModObject modObject = ModObject.valueOf(resourcePath.replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH));
            if (mapping.type == Type.BLOCK && modObject.getBlock() != null) {
              mapping.remap(modObject.getBlockNN());
            } else if (mapping.type == Type.ITEM && modObject.getItem() != null) {
              mapping.remap(modObject.getItemNN());
            }
          } catch (Exception e) {
          }
        }
      }
    }
  }

}