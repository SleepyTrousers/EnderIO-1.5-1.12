package crazypants.enderio.api.addon;

import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Interface to tag mod classes (i.e. classes annotated with {@link Mod}) as addons for Ender IO. Implementing this interface is only needed if the addon wants
 * to use one of teature in it.
 *
 */
public interface IEnderIOAddon {

  /**
   * Addons that return their {@link Configuration} object here will be included in the in-game configuration menu of the base mod.
   * <p>
   * Note: This means they will also have to check for {@link EnderIO#MODID} on the {@link OnConfigChangedEvent} instead of only their own ID!
   * 
   * @return A {@link Configuration} object.
   */
  @Nullable
  Configuration getConfiguration();

}
