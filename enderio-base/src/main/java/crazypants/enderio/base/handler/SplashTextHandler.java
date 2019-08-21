package crazypants.enderio.base.handler;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class SplashTextHandler {

  private static final @Nonnull Random RANDOM = new Random();
  // just a tiny easter egg, no need for I18N
  private static final @Nonnull NNList<String> TEXTS = new NNList<>("Ender IO!", "now with mods", "laggy as ever");

  @SubscribeEvent
  public static void handle(GuiScreenEvent.InitGuiEvent.Pre event) {
    if (event.getGui() instanceof GuiMainMenu && RANDOM.nextFloat() < 0.01f) {
      ReflectionHelper.setPrivateValue(GuiMainMenu.class, (GuiMainMenu) event.getGui(), TEXTS.get(RANDOM.nextInt(TEXTS.size())), "splashText", "field_73975_c");
    }
  }

}
