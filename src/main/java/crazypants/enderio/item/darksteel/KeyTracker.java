package crazypants.enderio.item.darksteel;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentTranslation;

public class KeyTracker {

  public static KeyTracker instance = new KeyTracker();
  
  static {
    FMLCommonHandler.instance().bus().register(instance);
  }
  
  private KeyBinding glideKey;
  private boolean wasGlideKeyPressed = false;
  
  private boolean isGlideActive = false;
  
  public KeyTracker() {
    glideKey = new KeyBinding("Glider Toggle", Keyboard.KEY_G, "Dark Steel Armor");
    ClientRegistry.registerKeyBinding(glideKey);    
  }
  
  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {   
    if(!DarkSteelController.instance.isGliderUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      return;
    }
    if(glideKey.getIsKeyPressed()) {      
      isGlideActive = !isGlideActive;
      String message;
      if(isGlideActive) {
        message = Lang.localize("darksteel.upgrade.glider.enabled");
      } else {
        message = Lang.localize("darksteel.upgrade.glider.disabled");
      }
      Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentTranslation(message));
      DarkSteelController.instance.setGlideActive(Minecraft.getMinecraft().thePlayer, isGlideActive);
      PacketHandler.INSTANCE.sendToServer(new PacketGlideState(isGlideActive));
    }
  }

  public boolean isGlideActive() {
    return isGlideActive;
  }   
  
}
