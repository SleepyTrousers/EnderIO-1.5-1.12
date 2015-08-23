package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.enderio.core.common.util.ChatUtil;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.item.PacketMagnetState.SlotType;
import crazypants.enderio.item.darksteel.DarkSteelController;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.PacketUpgradeState;
import crazypants.enderio.item.darksteel.PacketUpgradeState.Type;
import crazypants.enderio.item.darksteel.SoundDetector;
import crazypants.enderio.item.darksteel.upgrade.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SoundDetectorUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpeedUpgrade;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.thaumcraft.GogglesOfRevealingUpgrade;
import crazypants.util.BaublesUtil;

import static crazypants.enderio.item.darksteel.DarkSteelItems.itemMagnet;

public class KeyTracker {

  public static final KeyTracker instance = new KeyTracker();
  
  private final KeyBinding glideKey;
  
  private final KeyBinding soundDetectorKey;
  
  private final KeyBinding nightVisionKey;
  
  private final KeyBinding stepAssistKey;
  
  private final KeyBinding speedKey;
  
  private final KeyBinding jumpKey;
  
  private final KeyBinding gogglesKey;
  
  private final KeyBinding yetaWrenchMode;
  
  private final KeyBinding magnetKey;
  
  public KeyTracker() {
    glideKey = new KeyBinding(EnderIO.lang.localize("keybind.glidertoggle"), Keyboard.KEY_G, EnderIO.lang.localize("category.darksteelarmor"));
    ClientRegistry.registerKeyBinding(glideKey);
    soundDetectorKey = new KeyBinding(EnderIO.lang.localize("keybind.soundlocator"), Keyboard.KEY_L, EnderIO.lang.localize("category.darksteelarmor"));
    ClientRegistry.registerKeyBinding(soundDetectorKey);        
    nightVisionKey = new KeyBinding(EnderIO.lang.localize("keybind.nightvision"), Keyboard.KEY_P, EnderIO.lang.localize("category.darksteelarmor"));
    ClientRegistry.registerKeyBinding(nightVisionKey);
    gogglesKey = new KeyBinding(EnderIO.lang.localize("keybind.gogglesofrevealing"), Keyboard.KEY_R, EnderIO.lang.localize("category.darksteelarmor"));
    ClientRegistry.registerKeyBinding(gogglesKey);
    
    stepAssistKey = new KeyBinding(EnderIO.lang.localize("keybind.stepassist"), Keyboard.KEY_NONE, EnderIO.lang.localize("category.darksteelarmor"));
    ClientRegistry.registerKeyBinding(stepAssistKey);
    
    speedKey = new KeyBinding(EnderIO.lang.localize("keybind.speed"), Keyboard.KEY_NONE, EnderIO.lang.localize("category.darksteelarmor"));
    ClientRegistry.registerKeyBinding(speedKey);
    
    jumpKey = new KeyBinding(EnderIO.lang.localize("keybind.jump"), Keyboard.KEY_NONE, EnderIO.lang.localize("category.darksteelarmor"));
    ClientRegistry.registerKeyBinding(jumpKey);
    
    yetaWrenchMode = new KeyBinding(EnderIO.lang.localize("keybind.yetawrenchmode"), Keyboard.KEY_Y, EnderIO.lang.localize("category.tools"));
    ClientRegistry.registerKeyBinding(yetaWrenchMode);

    magnetKey = new KeyBinding(EnderIO.lang.localize("keybind.magnet"), Keyboard.CHAR_NONE, EnderIO.lang.localize("category.tools"));
    ClientRegistry.registerKeyBinding(magnetKey);
  }
  
  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {   
    handleGlide();
    handleSoundDetector();
    handleNightVision();
    handleYetaWrench();
    handleGoggles();
    handleStepAssist();
    handleSpeed();
    handleJump();
    handleMagnet();
  }

  private static int __KEYS = 0;
  private static final int KEY_GLIDE = __KEYS++;
  private static final int KEY_MAGNET = __KEYS++;
  private static final int KEY_JUMP = __KEYS++;
  private static final int KEY_SPEED = __KEYS++;
  private static final int KEY_STEP = __KEYS++;
  private static final int KEY_GOOGLES = __KEYS++;
  private static final int KEY_YETA = __KEYS++;
  private static final int KEY_SOUND = __KEYS++;
  private static final int KEY_NIGHT = __KEYS++;
  private static final long[] lastseen = new long[__KEYS];

  private static boolean debounce(int key) {
    long now = EnderIO.proxy.getTickCount();
    if (lastseen[key] < now) {
      lastseen[key] = now + 3;
      return false;
    }
    return true;
  }

  private void sendEnabledChatMessage(String messageBase, boolean isActive) {
    String message = messageBase.concat(isActive ? ".enabled" : ".disabled");
    ChatUtil.sendNoSpamClientUnloc(EnderIO.lang, message);
  }

  private void toggleDarkSteelController(Type type, String messageBase) {
    boolean isActive = !DarkSteelController.instance.isActive(Minecraft.getMinecraft().thePlayer, type);
    sendEnabledChatMessage(messageBase, isActive);
    DarkSteelController.instance.setActive(Minecraft.getMinecraft().thePlayer, type, isActive);
    PacketHandler.INSTANCE.sendToServer(new PacketUpgradeState(type, isActive));
  }

  private void handleMagnet() {
    if (magnetKey.isPressed() && !debounce(KEY_MAGNET)) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      ItemStack[] inv = player.inventory.mainInventory;
      for (int i = 0; i < 9; i++) {
        if(inv[i] != null && inv[i].getItem() != null && inv[i].getItem() == itemMagnet) {
          boolean isActive = !ItemMagnet.isActive(inv[i]);
          PacketHandler.INSTANCE.sendToServer(new PacketMagnetState(SlotType.INVENTORY, i, isActive));
          return;
        }
      }

      IInventory baubles = BaublesUtil.instance().getBaubles(player);
      if(baubles != null) {
        for (int i = 0; i < baubles.getSizeInventory(); i++) {
          ItemStack stack = baubles.getStackInSlot(i);
          if(stack != null && stack.getItem() != null && stack.getItem() == itemMagnet) {
            boolean isActive = !ItemMagnet.isActive(inv[i]);
            PacketHandler.INSTANCE.sendToServer(new PacketMagnetState(SlotType.BAUBLES, i, isActive));
            return;
          }
        }
      }
    }
  }

  private void handleJump() {
    if (jumpKey.isPressed() && !debounce(KEY_JUMP) && JumpUpgrade.isEquipped(Minecraft.getMinecraft().thePlayer)) {
      toggleDarkSteelController(Type.JUMP, "darksteel.upgrade.jump");
    }
  }

  private void handleSpeed() {
    if (speedKey.isPressed() && !debounce(KEY_SPEED) && SpeedUpgrade.isEquipped(Minecraft.getMinecraft().thePlayer)) {
      toggleDarkSteelController(Type.SPEED, "darksteel.upgrade.speed");
    }
  }

  private void handleStepAssist() {
    if (stepAssistKey.isPressed() && !debounce(KEY_STEP) && JumpUpgrade.isEquipped(Minecraft.getMinecraft().thePlayer)) {
      toggleDarkSteelController(Type.STEP_ASSIST, "darksteel.upgrade.stepAssist");
    }
  }

  private void handleGoggles() {
    if (gogglesKey.isPressed() && !debounce(KEY_GOOGLES)
        && GogglesOfRevealingUpgrade.isUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      boolean isActive = !DarkSteelItems.itemDarkSteelHelmet.isGogglesUgradeActive();
      sendEnabledChatMessage("darksteel.upgrade.goggles", isActive);
      DarkSteelItems.itemDarkSteelHelmet.setGogglesUgradeActive(isActive);
    }
  }

  private void handleYetaWrench() {
    if (!yetaWrenchMode.isPressed() || debounce(KEY_YETA)) {
      return;
    }
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return;
    }
    if(equipped.getItem() instanceof IConduitControl) {
      ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
      if(curMode == null) {
        curMode = ConduitDisplayMode.ALL;
      }
      ConduitDisplayMode newMode = curMode.next();
      ConduitDisplayMode.setDisplayMode(equipped, newMode);
      PacketHandler.INSTANCE.sendToServer(new YetaWrenchPacketProcessor(player.inventory.currentItem, newMode));
    } else if(equipped.getItem() == EnderIO.itemConduitProbe) {
      
      int newMeta = equipped.getItemDamage() == 0 ? 1 : 0;
      equipped.setItemDamage(newMeta);
      PacketHandler.INSTANCE.sendToServer(new PacketConduitProbeMode());   
      player.swingItem();
      
    }    
  }

  private void handleSoundDetector() {
    if (soundDetectorKey.isPressed() && !debounce(KEY_SOUND) && isSoundDetectorUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      boolean isActive = !SoundDetector.instance.isEnabled();
      sendEnabledChatMessage("darksteel.upgrade.sound", isActive);
      SoundDetector.instance.setEnabled(isActive);
    } else {
      SoundDetector.instance.setEnabled(false);
    }
  }

  private void handleGlide() {
    if (glideKey.isPressed() && !debounce(KEY_GLIDE)
        && DarkSteelController.instance.isGliderUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      toggleDarkSteelController(Type.GLIDE, "darksteel.upgrade.glider");
    }
  }
  
  private void handleNightVision() {
    if (nightVisionKey.isPressed() && !debounce(KEY_NIGHT)) {
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      if (!DarkSteelController.instance.isNightVisionUpgradeEquipped(player)) {
        return;
      }
      boolean isActive = !DarkSteelController.instance.isNightVisionActive();
      if(isActive) {
        player.worldObj.playSound(player.posX, player.posY, player.posZ, EnderIO.MODID + ":ds.nightvision.on", 0.1f, player.worldObj.rand.nextFloat() * 0.4f - 0.2f + 1.0f, false);
      } else {
        player.worldObj.playSound(player.posX, player.posY, player.posZ, EnderIO.MODID + ":ds.nightvision.off", 0.1f, 1.0f, false);
      }
      DarkSteelController.instance.setNightVisionActive(isActive);
    }
  }

  public boolean isSoundDetectorUpgradeEquipped(EntityClientPlayerMP player) {
    ItemStack helmet = player.getEquipmentInSlot(4);
    SoundDetectorUpgrade upgrade = SoundDetectorUpgrade.loadFromItem(helmet);
    if(upgrade == null) {
      return false;
    }
    return true;
  }
  
  public KeyBinding getYetaWrenchMode() {
    return yetaWrenchMode;
  }
}
