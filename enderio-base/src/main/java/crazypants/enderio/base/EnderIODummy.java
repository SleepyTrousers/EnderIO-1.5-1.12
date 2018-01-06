package crazypants.enderio.base;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = EnderIODummy.MODID, name = EnderIODummy.MOD_NAME, version = EnderIO.VERSION, dependencies = "after:" + EnderIO.MODID)
public class EnderIODummy {

  public static final @Nonnull String MODID = "enderiobase";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Base";

}
