package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.capacitor.Scaler;
import crazypants.enderio.base.EnderIO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class CapacitorKeyRegistry {

  private static IForgeRegistry<ICapacitorKey> REGISTRY = null;

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<ICapacitorKey>().setName(new ResourceLocation(EnderIO.DOMAIN, "capacitor")).setType(ICapacitorKey.class)
        .setIDRange(0, Integer.MAX_VALUE - 1).create();
  }

  public static void setScaler(@Nonnull ResourceLocation name, @Nonnull Scaler scaler) {
    ICapacitorKey key = REGISTRY.getValue(name);
    if (key == null) {
      throw new RuntimeException("Trying to configure non-exsistant key " + name);
    }
    key.setScaler(scaler);
  }

  public static void setBaseValue(@Nonnull ResourceLocation name, int baseValue) {
    ICapacitorKey key = REGISTRY.getValue(name);
    if (key == null) {
      throw new RuntimeException("Trying to configure non-exsistant key " + name);
    }
    key.setBaseValue(baseValue);
  }

  public static void validate() {
    for (ICapacitorKey key : REGISTRY.getValuesCollection()) {
      key.validate();
    }
  }

  public static boolean contains(@Nonnull ResourceLocation name) {
    return REGISTRY.containsKey(name);
  }

}
