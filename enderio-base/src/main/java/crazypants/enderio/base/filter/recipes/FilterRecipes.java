package crazypants.enderio.base.filter.recipes;

import java.util.UUID;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class FilterRecipes {

  @SubscribeEvent
  public static void register(@Nonnull RegistryEvent.Register<IRecipe> event) {
    event.getRegistry().register(new ClearFilterRecipe().setRegistryName(UUID.randomUUID().toString()));
    event.getRegistry().register(new CopyFilterRecipe().setRegistryName(UUID.randomUUID().toString()));
  }

}
