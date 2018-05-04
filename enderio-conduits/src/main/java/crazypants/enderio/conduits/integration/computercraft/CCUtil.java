package crazypants.enderio.conduits.integration.computercraft;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.annotation.Nonnull;

public class CCUtil {
  public static void init(@Nonnull FMLInitializationEvent event) {
    if (Loader.isModLoaded("computercraft")) register();
  }

  @Optional.Method(modid = "computercraft")
  private static void register() {
    ComputerCraftAPI.registerBundledRedstoneProvider(new ConduitBundledRedstoneProvider());
  }
}
