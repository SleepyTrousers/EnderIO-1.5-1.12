package crazypants.enderio.base.handler;

import crazypants.enderio.base.EnderIO;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DevVersionWarningHandler {

  @SubscribeEvent
  public static void onPlayerLoggon(PlayerLoggedInEvent evt) {
    if (EnderIO.VERSION.contains("-") || EnderIO.VERSION.contains("@")) { // e.g. 1.2.3-nightly
      evt.player.sendMessage(new TextComponentString(
          TextFormatting.DARK_RED + "This is an " + TextFormatting.BLACK + "Ender IO " + TextFormatting.DARK_RED + "development build!"));
      evt.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "It may trash your world at any time!"));
      evt.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Do not use it for anything but testing!"));
      evt.player.sendMessage(new TextComponentString("You have been warned..."));
    }
  }

}
