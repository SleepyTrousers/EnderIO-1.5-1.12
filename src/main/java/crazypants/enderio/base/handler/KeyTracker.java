package crazypants.enderio.base.handler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.handler.darksteel.PacketUpgradeState;
import crazypants.enderio.base.handler.darksteel.PacketUpgradeState.Type;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.base.item.conduitprobe.PacketConduitProbeMode;
import crazypants.enderio.base.item.darksteel.upgrade.jump.JumpUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetector;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetectorUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.speed.SpeedUpgrade;
import crazypants.enderio.base.item.magnet.ItemMagnet;
import crazypants.enderio.base.item.magnet.MagnetController;
import crazypants.enderio.base.item.magnet.PacketMagnetState;
import crazypants.enderio.base.item.magnet.MagnetController.ActiveMagnet;
import crazypants.enderio.base.item.magnet.PacketMagnetState.SlotType;
import crazypants.enderio.base.item.yetawrench.YetaWrenchPacketProcessor;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;

public class KeyTracker {

  public static final KeyTracker instance = new KeyTracker();

  private interface Action {
    void execute();
  }

  private final @Nonnull List<Pair<KeyBinding, Action>> keyActions = new ArrayList<Pair<KeyBinding, Action>>();

  private final @Nonnull KeyBinding fovPlusKeyFast, fovMinusKeyFast, fovPlusKey, fovMinusKey, yetaWrenchMode;

  public KeyTracker() {
    create("enderio.keybind.glidertoggle      ", Keyboard.KEY_G, "   enderio.category.darksteelarmor", new GlideAction());
    create("enderio.keybind.soundlocator      ", Keyboard.KEY_L, "   enderio.category.darksteelarmor", new SoundDetectorAction());
    create("enderio.keybind.nightvision       ", Keyboard.KEY_P, "   enderio.category.darksteelarmor", new NightVisionAction());
    create("enderio.keybind.gogglesofrevealing", Keyboard.KEY_NONE, "enderio.category.darksteelarmor", new GogglesAction());
    create("enderio.keybind.stepassist        ", Keyboard.KEY_NONE, "enderio.category.darksteelarmor", new StepAssistAction());
    create("enderio.keybind.speed             ", Keyboard.KEY_NONE, "enderio.category.darksteelarmor", new SpeedAction());
    create("enderio.keybind.jump              ", Keyboard.KEY_NONE, "enderio.category.darksteelarmor", new JumpAction());
    create("enderio.keybind.top               ", Keyboard.KEY_NONE, "enderio.category.darksteelarmor", new TopAction());
    yetaWrenchMode = //
    create("enderio.keybind.yetawrenchmode    ", Keyboard.KEY_Y, "   enderio.category.tools         ", new YetaWrenchAction());
    create("enderio.keybind.magnet            ", Keyboard.KEY_NONE, "enderio.category.tools         ", new MagnetAction());
    create("enderio.keybind.fovreset          ", Keyboard.KEY_NONE, "key.categories.misc            ", new FovAction());
    fovPlusKey = create("     enderio.keybind.fovplus     ", Keyboard.KEY_NONE, "key.categories.misc");
    fovMinusKey = create("    enderio.keybind.fovminus    ", Keyboard.KEY_NONE, "key.categories.misc");
    fovPlusKeyFast = create(" enderio.keybind.fovplusfast ", Keyboard.KEY_NONE, "key.categories.misc");
    fovMinusKeyFast = create("enderio.keybind.fovminusfast", Keyboard.KEY_NONE, "key.categories.misc");
  }

  private @Nonnull KeyBinding create(@Nonnull String description, int keyCode, @Nonnull String category, @Nonnull Action action) {
    final KeyBinding keyBinding = create(description, keyCode, category);
    keyActions.add(Pair.of(keyBinding, action));
    return keyBinding;
  }

  private @Nonnull KeyBinding create(@Nonnull String description, int keyCode, @Nonnull String category) {
    final KeyBinding keyBinding = new KeyBinding(description.trim(), keyCode, category.trim());
    ClientRegistry.registerKeyBinding(keyBinding);
    return keyBinding;
  }

  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {
    for (Pair<KeyBinding, Action> keyAction : keyActions) {
      if (keyAction.getKey().isPressed()) {
        keyAction.getValue().execute();
      }
    }

    if (!isSoundDetectorUpgradeEquipped(Minecraft.getMinecraft().player)) {
      SoundDetector.instance.setEnabled(false);
    }
  }

  private static void sendEnabledChatMessage(String messageBase, boolean isActive) {
    String message = messageBase.concat(isActive ? ".enabled" : ".disabled");
    ChatUtil.sendNoSpamClientUnloc(EnderIO.lang, message);
  }

  private static void toggleDarkSteelController(Type type, String messageBase) {
    boolean isActive = !DarkSteelController.instance.isActive(Minecraft.getMinecraft().player, type);
    sendEnabledChatMessage(messageBase, isActive);
    DarkSteelController.instance.setActive(Minecraft.getMinecraft().player, type, isActive);
    PacketHandler.INSTANCE.sendToServer(new PacketUpgradeState(type, isActive));
  }

  public static boolean isSoundDetectorUpgradeEquipped(EntityPlayerSP player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    SoundDetectorUpgrade upgrade = SoundDetectorUpgrade.loadFromItem(helmet);
    if (upgrade == null) {
      return false;
    }
    return true;
  }

  private static class MagnetAction implements Action {
    @Override
    public void execute() {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      ActiveMagnet magnet = MagnetController.getMagnet(player, false);
      if (magnet != null) {
        boolean isActive = !ItemMagnet.isActive(magnet.getItem());
        PacketHandler.INSTANCE.sendToServer(new PacketMagnetState(SlotType.INVENTORY, magnet.getSlot(), isActive));
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

  private static class JumpAction implements Action {
    @Override
    public void execute() {
      if (JumpUpgrade.isEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(Type.JUMP, "darksteel.upgrade.jump");
      }
    }
  }

  private static class SpeedAction implements Action {
    @Override
    public void execute() {
      if (SpeedUpgrade.isEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(Type.SPEED, "darksteel.upgrade.speed");
      }
    }
  }

  private static class StepAssistAction implements Action {
    @Override
    public void execute() {
      if (JumpUpgrade.isEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(Type.STEP_ASSIST, "darksteel.upgrade.stepAssist");
      }
    }
  }

  private static class GogglesAction implements Action {
    @Override
    public void execute() {
      // TODO: Mod Thaumcraft
      // EntityPlayer player = Minecraft.getMinecraft().player;
      // if(!GogglesOfRevealingUpgrade.isUpgradeEquipped(player)){
      // boolean isActive = !ModObject.itemDarkSteelHelmet.isGogglesUgradeActive();
      // sendEnabledChatMessage("darksteel.upgrade.goggles", isActive); // TODO lang key is wrong
      // ModObject.itemDarkSteelHelmet.setGogglesUgradeActive(isActive);
      // }
    }
  }

  private static class YetaWrenchAction implements Action {
    @Override
    public void execute() {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      ItemStack equipped = player.getHeldItemMainhand();
      if (Prep.isInvalid(equipped)) {
        return;
      }
      if (equipped.getItem() instanceof IConduitControl) {
        ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
        ConduitDisplayMode newMode = player.isSneaking() ? curMode.previous() : curMode.next();
        ConduitDisplayMode.setDisplayMode(equipped, newMode);
        PacketHandler.INSTANCE.sendToServer(new YetaWrenchPacketProcessor(player.inventory.currentItem, newMode));
      } else if (equipped.getItem() == itemConduitProbe.getItem()) {
        int newMeta = equipped.getItemDamage() == 0 ? 1 : 0;
        equipped.setItemDamage(newMeta);
        PacketHandler.INSTANCE.sendToServer(new PacketConduitProbeMode());
        player.swingArm(EnumHand.MAIN_HAND);

      }
    }
  }

  private static class SoundDetectorAction implements Action {
    @Override
    public void execute() {
      if (isSoundDetectorUpgradeEquipped(Minecraft.getMinecraft().player)) {
        boolean isActive = !SoundDetector.instance.isEnabled();
        sendEnabledChatMessage("darksteel.upgrade.sound", isActive);
        SoundDetector.instance.setEnabled(isActive);
      }
    }
  }

  private static class GlideAction implements Action {
    @Override
    public void execute() {
      if (DarkSteelController.instance.isGliderUpgradeEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(Type.GLIDE, "darksteel.upgrade.glider");
      } else if (DarkSteelController.instance.isElytraUpgradeEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(Type.ELYTRA, "darksteel.upgrade.elytra");
      }
    }
  }
  
  private static class NightVisionAction implements Action {
    @Override
    public void execute() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (DarkSteelController.instance.isNightVisionUpgradeEquipped(player)) {
        boolean isActive = !DarkSteelController.instance.isNightVisionActive();
        if (isActive) {
          SoundHelper.playSound(player.world, player, SoundRegistry.NIGHTVISION_ON, 0.1f, player.world.rand.nextFloat() * 0.4f - 0.2f + 1.0f);
        } else {
          SoundHelper.playSound(player.world, player, SoundRegistry.NIGHTVISION_OFF, 0.1f, 1.0f);
        }
        DarkSteelController.instance.setNightVisionActive(isActive);
      }
    }
  }

  private static class TopAction implements Action {
    @Override
    public void execute() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (DarkSteelController.instance.isTopUpgradeEquipped(player)) {
        boolean isActive = !DarkSteelController.instance.isTopActive(player);
        DarkSteelController.instance.setTopActive(player, isActive);
      }
    }
  }

  private class FovAction implements Action {
    @Override
    public void execute() {
      fovLevelLast = fovLevelNext = 1;
    }
  }

  private double fovLevelLast = 1;
  private double fovLevelNext = 1;
  private long lastWorldTime = 0;

  @SubscribeEvent
  public void onFov(FOVModifier event) {
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

  public KeyBinding getYetaWrenchMode() {
    return yetaWrenchMode;
  }

}
