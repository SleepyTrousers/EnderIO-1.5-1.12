package crazypants.enderio.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.addon.IEnderIOAddon;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = EnderIOConduits.MODID, name = EnderIOConduits.MOD_NAME, version = EnderIOConduits.VERSION, dependencies = EnderIOConduits.DEPENDENCIES)
public class EnderIOConduits implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderioconduits";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Conduits";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return ConfigHandler.config;
  }

}
