package crazypants.enderio.base.farming.fertilizer;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class Fertilizer {

  private static IForgeRegistry<IFertilizer> REGISTRY = null;

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<IFertilizer>().setName(new ResourceLocation(EnderIO.DOMAIN, "fertilizer")).setType(IFertilizer.class)
        .setIDRange(0, Integer.MAX_VALUE - 1).create();
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    event.getRegistry().register(new Bonemeal(new ItemStack(Items.DYE, 1, 15)));
  }

  /**
   * Returns the singleton instance for the fertilizer that was given as parameter. If the given item is no fertilizer, it will return an instance of
   * {@link NoFertilizer#NONE}.
   * 
   */
  public static @Nonnull IFertilizer getInstance(@Nonnull ItemStack stack) {
    for (IFertilizer fertilizer : REGISTRY.getValues()) {
      if (fertilizer.matches(stack)) {
        return fertilizer;
      }
    }
    return NoFertilizer.getNone();
  }

  /**
   * Returns true if the given item can be used as fertilizer.
   */
  public static boolean isFertilizer(@Nonnull ItemStack stack) {
    return getInstance(stack) != NoFertilizer.getNone();
  }

}
