package crazypants.enderio.machine;

import static crazypants.enderio.machine.EnderIOInvPanel.MODID;
import static crazypants.enderio.machine.EnderIOInvPanel.MOD_NAME;
import static crazypants.enderio.machine.EnderIOInvPanel.VERSION;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.invpanel.config.ConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION)
public class EnderIOInvPanel {

  public static final @Nonnull String MODID = "enderioinvpanel";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Inventory Panel";
  public static final @Nonnull String VERSION = "@VERSION@";
  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    ConfigHandler.init(event);
  }
}
