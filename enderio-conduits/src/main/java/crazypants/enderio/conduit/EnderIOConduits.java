package crazypants.enderio.conduit;

import crazypants.enderio.api.addon.IEnderIOAddon;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static crazypants.enderio.conduit.EnderIOConduits.*;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "after:" + crazypants.enderio.base.EnderIO.MODID)
public class EnderIOConduits implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderio-conduits";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Conduits";
  public static final @Nonnull String VERSION = "@VERSION@";

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return ConfigHandler.config;
  }

}
