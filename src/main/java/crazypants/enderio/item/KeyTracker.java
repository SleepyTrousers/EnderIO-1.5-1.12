package crazypants.enderio.item;

import org.lwjgl.input.Keyboard;

import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.MagnetController.ActiveMagnet;
import crazypants.enderio.item.PacketMagnetState.SlotType;
import crazypants.enderio.item.darksteel.DarkSteelController;
import crazypants.enderio.item.darksteel.PacketUpgradeState;
import crazypants.enderio.item.darksteel.PacketUpgradeState.Type;
import crazypants.enderio.item.darksteel.SoundDetector;
import crazypants.enderio.item.darksteel.upgrade.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SoundDetectorUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpeedUpgrade;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.sound.SoundHelper;
import crazypants.enderio.sound.SoundRegistry;
import crazypants.util.BaublesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

import static crazypants.enderio.config.Config.allowFovControlsInSurvivalMode;

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

  private final KeyBinding topKey;
  
  private final KeyBinding fovPlusKey, fovMinusKey, fovPlusKeyFast, fovMinusKeyFast, fovResetKey;

  public KeyTracker() {
    glideKey = new KeyBinding("enderio.keybind.glidertoggle", Keyboard.KEY_G, "enderio.category.darksteelarmor");
    ClientRegistry.registerKeyBinding(glideKey);
    soundDetectorKey = new KeyBinding("enderio.keybind.soundlocator", Keyboard.KEY_L, "enderio.category.darksteelarmor");
    ClientRegistry.registerKeyBinding(soundDetectorKey);
    nightVisionKey = new KeyBinding("enderio.keybind.nightvision", Keyboard.KEY_P, "enderio.category.darksteelarmor");
    ClientRegistry.registerKeyBinding(nightVisionKey);
    gogglesKey = new KeyBinding("enderio.keybind.gogglesofrevealing", Keyboard.KEY_R, "enderio.category.darksteelarmor");
    // ClientRegistry.registerKeyBinding(gogglesKey);

    stepAssistKey = new KeyBinding("enderio.keybind.stepassist", Keyboard.KEY_NONE, "enderio.category.darksteelarmor");
    ClientRegistry.registerKeyBinding(stepAssistKey);

    speedKey = new KeyBinding("enderio.keybind.speed", Keyboard.KEY_NONE, "enderio.category.darksteelarmor");
    ClientRegistry.registerKeyBinding(speedKey);

    jumpKey = new KeyBinding("enderio.keybind.jump", Keyboard.KEY_NONE, "enderio.category.darksteelarmor");
    ClientRegistry.registerKeyBinding(jumpKey);

    yetaWrenchMode = new KeyBinding("enderio.keybind.yetawrenchmode", Keyboard.KEY_Y, "enderio.category.tools");
    ClientRegistry.registerKeyBinding(yetaWrenchMode);

    magnetKey = new KeyBinding("enderio.keybind.magnet", Keyboard.CHAR_NONE, "enderio.category.tools");
    ClientRegistry.registerKeyBinding(magnetKey);

    topKey = new KeyBinding("enderio.keybind.top", Keyboard.CHAR_NONE, "enderio.category.darksteelarmor");
    ClientRegistry.registerKeyBinding(topKey);

    fovPlusKey = new KeyBinding("enderio.keybind.fovplus", Keyboard.CHAR_NONE, "key.categories.misc");
    ClientRegistry.registerKeyBinding(fovPlusKey);

    fovMinusKey = new KeyBinding("enderio.keybind.fovminus", Keyboard.CHAR_NONE, "key.categories.misc");
    ClientRegistry.registerKeyBinding(fovMinusKey);

    fovPlusKeyFast = new KeyBinding("enderio.keybind.fovplusfast", Keyboard.CHAR_NONE, "key.categories.misc");
    ClientRegistry.registerKeyBinding(fovPlusKeyFast);

    fovMinusKeyFast = new KeyBinding("enderio.keybind.fovminusfast", Keyboard.CHAR_NONE, "key.categories.misc");
    ClientRegistry.registerKeyBinding(fovMinusKeyFast);

    fovResetKey = new KeyBinding("enderio.keybind.fovreset", Keyboard.CHAR_NONE, "key.categories.misc");
    ClientRegistry.registerKeyBinding(fovResetKey);
  }
  
  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {   
    handleGlide();
    handleElytra();
    handleSoundDetector();
    handleNightVision();
    handleYetaWrench();
    handleGoggles();
    handleStepAssist();
    handleSpeed();
    handleJump();
    handleMagnet();
    handleTop();
    handleFov();
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
    if(magnetKey.isPressed()) {
      EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
      ActiveMagnet magnet = MagnetController.getMagnet(player, false);
      if (magnet != null) {
        boolean isActive = !ItemMagnet.isActive(magnet.item);
        PacketHandler.INSTANCE.sendToServer(new PacketMagnetState(SlotType.INVENTORY, magnet.slot, isActive));
        return;
      }

      IInventory baubles = BaublesUtil.instance().getBaubles(player);
      if(baubles != null) {
        for (int i = 0; i < baubles.getSizeInventory(); i++) {
          ItemStack stack = baubles.getStackInSlot(i);
          if (ItemMagnet.isMagnet(stack)) {
            boolean isActive = !ItemMagnet.isActive(stack);
            PacketHandler.INSTANCE.sendToServer(new PacketMagnetState(SlotType.BAUBLES, i, isActive));
            return;
          }
        }
      }
    }
  }

  private void handleJump() {
    if(!JumpUpgrade.isEquipped(Minecraft.getMinecraft().thePlayer)) {
      return;
    }
    if(jumpKey.isPressed()) {
      toggleDarkSteelController(Type.JUMP, "darksteel.upgrade.jump");
    }
  }

  private void handleSpeed() {
    if(!SpeedUpgrade.isEquipped(Minecraft.getMinecraft().thePlayer)) {
      return;
    }
    if(speedKey.isPressed()) {
      toggleDarkSteelController(Type.SPEED, "darksteel.upgrade.speed");
    }
  }

  private void handleStepAssist() {
    if(!JumpUpgrade.isEquipped(Minecraft.getMinecraft().thePlayer)) {
      return;
    }
    if(stepAssistKey.isPressed()) {
      toggleDarkSteelController(Type.STEP_ASSIST, "darksteel.upgrade.stepAssist");
    }
  }

  private void handleGoggles() {
//    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    //TODO: 1.9 Thaumcraft
//    if(!GogglesOfRevealingUpgrade.isUpgradeEquipped(player)){
//      return;
//    }
    // if(gogglesKey.isPressed()) {
    // boolean isActive = !DarkSteelItems.itemDarkSteelHelmet.isGogglesUgradeActive();
    // sendEnabledChatMessage("darksteel.upgrade.goggles", isActive); // TODO lang key is wrong
    // DarkSteelItems.itemDarkSteelHelmet.setGogglesUgradeActive(isActive);
    // }
  }

  private void handleYetaWrench() {
    if(!yetaWrenchMode.isPressed()) {
      if (yetaWrenchMode.getKeyCode() == 0) {
        Config.checkYetaAccess();
      }
      return;
    }
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    ItemStack equipped = player.getHeldItemMainhand();
    if(equipped == null) {
      return;
    }
    if(equipped.getItem() instanceof IConduitControl) {
      ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
      if(curMode == null) {
        curMode = ConduitDisplayMode.ALL;
      }
      ConduitDisplayMode newMode = player.isSneaking() ? curMode.previous() : curMode.next();
      ConduitDisplayMode.setDisplayMode(equipped, newMode);
      PacketHandler.INSTANCE.sendToServer(new YetaWrenchPacketProcessor(player.inventory.currentItem, newMode));
    } else if(equipped.getItem() == EnderIO.itemConduitProbe) {
      
      int newMeta = equipped.getItemDamage() == 0 ? 1 : 0;
      equipped.setItemDamage(newMeta);
      PacketHandler.INSTANCE.sendToServer(new PacketConduitProbeMode());   
      player.swingArm(EnumHand.MAIN_HAND);
      
    }    
  }

  private void handleSoundDetector() {
    if(!isSoundDetectorUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      SoundDetector.instance.setEnabled(false);
      return;
    }
    if(soundDetectorKey.isPressed()) {
      boolean isActive = !SoundDetector.instance.isEnabled();
      sendEnabledChatMessage("darksteel.upgrade.sound", isActive);
      SoundDetector.instance.setEnabled(isActive);
    }
  }

  private void handleGlide() {
    if(!DarkSteelController.instance.isGliderUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      return;
    }
    if(glideKey.isPressed()) {
      toggleDarkSteelController(Type.GLIDE, "darksteel.upgrade.glider");
    }
  }
  
  private void handleElytra() {
    if (!DarkSteelController.instance.isElytraUpgradeEquipped(Minecraft.getMinecraft().thePlayer)) {
      return;
    }
    if (glideKey.isPressed()) {
      toggleDarkSteelController(Type.ELYTRA, "darksteel.upgrade.elytra");
    }
  }

  private void handleNightVision() {
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if(!DarkSteelController.instance.isNightVisionUpgradeEquipped(player)){
      return;
    }
    if(nightVisionKey.isPressed()) {
      boolean isActive = !DarkSteelController.instance.isNightVisionActive();
      if(isActive) {
        SoundHelper.playSound(player.worldObj, player, SoundRegistry.NIGHTVISION_ON, 0.1f, player.worldObj.rand.nextFloat() * 0.4f - 0.2f + 1.0f);
      } else {
        SoundHelper.playSound(player.worldObj, player, SoundRegistry.NIGHTVISION_OFF, 0.1f, 1.0f);
      }
      DarkSteelController.instance.setNightVisionActive(isActive);
    }
  }

  public boolean isSoundDetectorUpgradeEquipped(EntityPlayerSP player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    SoundDetectorUpgrade upgrade = SoundDetectorUpgrade.loadFromItem(helmet);
    if(upgrade == null) {
      return false;
    }
    return true;
  }
  
  public KeyBinding getYetaWrenchMode() {
    return yetaWrenchMode;
  }

  private void handleTop() {
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if (topKey.isPressed() && DarkSteelController.instance.isTopUpgradeEquipped(player)) {
      boolean isActive = !DarkSteelController.instance.isTopActive(player);
      DarkSteelController.instance.setTopActive(player, isActive);
    }
  }

  private double fovLevelLast = 1;
  private double fovLevelNext = 1;
  private long lastWorldTime = 0;

  @SubscribeEvent
  public void onFov(FOVModifier event) {
    final PlayerControllerMP playerController = Minecraft.getMinecraft().playerController;
    if (!allowFovControlsInSurvivalMode && (playerController == null || playerController.gameIsSurvivalOrAdventure())) {
      return;
    }
    long worldTime = EnderIO.proxy.getTickCount();
    while (worldTime > lastWorldTime) {
      if (worldTime - lastWorldTime > 10) {
        lastWorldTime = worldTime;
      } else {
        lastWorldTime++;
      }
      fovLevelLast = fovLevelNext;
      if (fovPlusKeyFast.isKeyDown()) {
        fovLevelNext *= 1.05;
      } else if (fovMinusKeyFast.isKeyDown()) {
        fovLevelNext /= 1.05;
      } else if (fovPlusKey.isKeyDown()) {
        fovLevelNext *= 1.01;
      } else if (fovMinusKey.isKeyDown()) {
        fovLevelNext /= 1.01;
      }
      if (fovLevelNext > 1.3) {
        fovLevelNext = 1.3;
      } else if (fovLevelNext < .05) {
        fovLevelNext = .05;
      }
    }
    double val = fovLevelNext * event.getRenderPartialTicks() + fovLevelLast * (1 - event.getRenderPartialTicks());
    event.setFOV((float) (event.getFOV() * val));
  }

  private void handleFov() {
    if (fovResetKey.isPressed()) {
      fovLevelLast = fovLevelNext = 1;
    }
  }

}
