package crazypants.enderio.base.farming.registry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class Registry {

  static final ResourceLocation PRIOLIST = new ResourceLocation(EnderIO.DOMAIN, "priolist");

  static IForgeRegistry<IFarmerJoe> REGISTRY = null;

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<IFarmerJoe>().setName(new ResourceLocation(EnderIO.DOMAIN, "farmers")).setType(IFarmerJoe.class)
        .setIDRange(0, Integer.MAX_VALUE - 1).addCallback(new RegistryCallbacks()).create();
  }

  public interface Callback<T> {
    T run(@Nonnull IFarmerJoe joe);
  }

  public static <T extends Object> T foreach(@Nonnull Callback<T> callback) {
    for (ResourceLocation farmer : (NNList<ResourceLocation>) Registry.REGISTRY.getSlaveMap(Registry.PRIOLIST, NNList.class)) {
      final IFarmerJoe joe = Registry.REGISTRY.getValue(farmer);
      if (joe != null) {
        T result = callback.run(joe);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

}
