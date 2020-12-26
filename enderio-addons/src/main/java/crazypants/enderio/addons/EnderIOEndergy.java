package crazypants.enderio.addons;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = EnderIOEndergy.MODID, name = EnderIOEndergy.MOD_NAME, version = EnderIOEndergy.VERSION, dependencies = EnderIOEndergy.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOEndergy implements IEnderIOAddon {

  @NetworkCheckHandler
  @SideOnly(Side.CLIENT)
  public boolean checkModLists(Map<String, String> modList, Side side) {
    return true;
  }

  public static final @Nonnull String MODID = "enderioeaddons";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Addons";
  public static final @Nonnull String VERSION = "@VERSION@";

  public static final @Nonnull String DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;

  public EnderIOEndergy() {
  }

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
  }

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return null;
  }

  private static final @Nonnull String KEEP_INVENTORY = "keepInventory";

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onPlayerDeath(PlayerDropsEvent evt) {
    if (evt.getEntityPlayer() == null) {
      Log.warn("PlayerDropsEvent: no player");
      return;
    }
    if (evt.getEntityPlayer() instanceof FakePlayer) {
      Log.warn("PlayerDropsEvent: fake player");
      return;
    }
    if (evt.isCanceled()) {
      Log.warn("PlayerDropsEvent: event already cancelled");
      return;
    }
    Log.warn("PlayerDropsEvent: gamerule keepInventory is " + evt.getEntityPlayer().world.getGameRules().getBoolean(KEEP_INVENTORY));
    for (String rule : evt.getEntityPlayer().world.getGameRules().getRules()) {
      Log.warn("PlayerDropsEvent: gamerule '" + rule + "' is " + evt.getEntityPlayer().world.getGameRules().getBoolean(rule) + "/"
          + evt.getEntityPlayer().world.getGameRules().getInt(rule) + "/" + evt.getEntityPlayer().world.getGameRules().getString(rule));
    }
  }

}
