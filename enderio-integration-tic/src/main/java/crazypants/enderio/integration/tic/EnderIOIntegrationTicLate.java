package crazypants.enderio.integration.tic;

import javax.annotation.Nonnull;

import crazypants.enderio.api.addon.IEnderIOAddon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = EnderIOIntegrationTicLate.MODID, name = EnderIOIntegrationTicLate.MOD_NAME, version = EnderIOIntegrationTicLate.VERSION, dependencies = EnderIOIntegrationTicLate.DEFAULT_DEPENDENCIES)
@EventBusSubscriber
public class EnderIOIntegrationTicLate implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderiointegrationticlate";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Integration with Tinkers' Construct";
  public static final @Nonnull String VERSION = "@VERSION@";

  public static final @Nonnull String DEFAULT_DEPENDENCIES = "after:tconstruct;after:enderiointegrationtic;before:enderio";

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (EnderIOIntegrationTic.isLoaded()) {
      TicControl.initPostTic(event);
    }
  }

}
