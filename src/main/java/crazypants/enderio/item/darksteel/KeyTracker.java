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
  private boolean isGlideActive = false;
  
  private KeyBinding soundDetectorKey;  
  private boolean isSoundDectorActive = false;
  
  public KeyTracker() {
    glideKey = new KeyBinding("Glider Toggle", Keyboard.KEY_G, "Dark Steel Armor");
    ClientRegistry.registerKeyBinding(glideKey);
    soundDetectorKey = new KeyBinding("Sound Locator", Keyboard.KEY_L, "Dark Steel Armor");
    ClientRegistry.registerKeyBinding(soundDetectorKey);        
  }
  
  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {   
    handleGlide();
    handleSoundDetector();
  }

  private void handleSoundDetector() {
    if(!DarkSteelController.instance.isSoundDetectorUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      SoundDetector.instance.enabled = false;
      return;
    }
    if(soundDetectorKey.getIsKeyPressed()) {      
      isSoundDectorActive = !isSoundDectorActive;
      String message;
      if(isSoundDectorActive) {
        message = Lang.localize("darksteel.upgrade.sound.enabled");
      } else {
        message = Lang.localize("darksteel.upgrade.sound.disabled");
      }
      Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentTranslation(message));
      SoundDetector.instance.enabled = isSoundDectorActive;
    }
    
  }

  private void handleGlide() {
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
