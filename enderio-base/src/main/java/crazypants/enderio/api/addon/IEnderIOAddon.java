package crazypants.enderio.api.addon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObjectRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Interface to tag mod classes (i.e. classes annotated with {@link Mod}) as addons for Ender IO. Implementing this interface is only needed if the addon wants
 * to use one of the features in it.
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
  default @Nullable Configuration getConfiguration() {
    return null;
  }

  /**
   * This allows addons to manually register additional blocks during Ender IO's block registration, i.e. using {@link EnderIO#DOMAIN} for their blocks'
   * registry name.
   * <p>
   * Don't use this for your general blocks, those go into the {@link ModObjectRegistry}. This is for things like fluid blocks that cannot be mod-objected.
   * 
   */
  default void injectBlocks(@Nonnull IForgeRegistry<Block> registry) {
  }

}
