package crazypants.enderio.base.handler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.handler.darksteel.gui.PacketOpenDSU;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.base.integration.thaumcraft.GogglesOfRevealingUpgrade;
import crazypants.enderio.base.item.conduitprobe.PacketConduitProbeMode;
import crazypants.enderio.base.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.glider.GliderUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.jump.JumpUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.nightvision.NightVisionUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetector;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetectorUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.speed.SpeedUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.stepassist.StepAssistUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.storage.PacketOpenInventory;
import crazypants.enderio.base.item.magnet.ItemMagnet;
import crazypants.enderio.base.item.magnet.MagnetController;
import crazypants.enderio.base.item.magnet.MagnetController.ActiveMagnet;
import crazypants.enderio.base.item.magnet.PacketMagnetState;
import crazypants.enderio.base.item.magnet.PacketMagnetState.SlotType;
import crazypants.enderio.base.item.yetawrench.PacketYetaWrenchDisplayMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import crazypants.enderio.util.Prep;
import crazypants.enderio.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class KeyTracker {

  public interface Action {
    void execute();
  }

  private static final @Nonnull List<Pair<KeyBinding, Action>> keyActions = new ArrayList<Pair<KeyBinding, Action>>();

  private static final @Nonnull KeyBinding fovPlusKeyFast, fovMinusKeyFast, fovPlusKey, fovMinusKey, yetaWrenchMode;

  public static final @Nonnull KeyBinding inventory, dsu;

  static {
    inventory = create("enderio.keybind.inventory", Keyboard.KEY_I, "key.category.darksteelarmor    ", new InventoryAction());
    dsu = create("enderio.keybind.dsu         ", Keyboard.KEY_NONE, "key.category.darksteelarmor    ", new DSUAction());
    create("enderio.keybind.glidertoggle      ", Keyboard.KEY_G, "   key.category.darksteelarmor    ", new GlideAction());
    create("enderio.keybind.soundlocator      ", Keyboard.KEY_NONE, "key.category.darksteelarmor    ", new SoundDetectorAction());
    create("enderio.keybind.nightvision       ", Keyboard.KEY_P, "   key.category.darksteelarmor    ", new NightVisionAction());
    create("enderio.keybind.gogglesofrevealing", Keyboard.KEY_NONE, "key.category.darksteelarmor    ", new GogglesAction());
    create("enderio.keybind.stepassist        ", Keyboard.KEY_NONE, "key.category.darksteelarmor    ", new StepAssistAction());
    create("enderio.keybind.speed             ", Keyboard.KEY_NONE, "key.category.darksteelarmor    ", new SpeedAction());
    create("enderio.keybind.jump              ", Keyboard.KEY_NONE, "key.category.darksteelarmor    ", new JumpAction());
    create("enderio.keybind.top               ", Keyboard.KEY_NONE, "key.category.darksteelarmor    ", new TopAction());
    yetaWrenchMode = //
        create("enderio.keybind.yetawrenchmode", Keyboard.KEY_Y, "   key.category.tools             ", new YetaWrenchAction());
    create("enderio.keybind.magnet            ", Keyboard.KEY_NONE, "key.category.tools             ", new MagnetAction());
    create("enderio.keybind.fovreset          ", Keyboard.KEY_NONE, "key.categories.misc            ", new FovAction());
    fovPlusKey = create("     enderio.keybind.fovplus     ", Keyboard.KEY_NONE, "key.categories.misc");
    fovMinusKey = create("    enderio.keybind.fovminus    ", Keyboard.KEY_NONE, "key.categories.misc");
    fovPlusKeyFast = create(" enderio.keybind.fovplusfast ", Keyboard.KEY_NONE, "key.categories.misc");
    fovMinusKeyFast = create("enderio.keybind.fovminusfast", Keyboard.KEY_NONE, "key.categories.misc");
  }

  public static @Nonnull KeyBinding create(@Nonnull String description, int keyCode, @Nonnull String category, @Nonnull Action action) {
    final KeyBinding keyBinding = create(description, keyCode, category);
    keyActions.add(Pair.of(keyBinding, action));
    return keyBinding;
  }

  public static @Nonnull KeyBinding create(@Nonnull String description, int keyCode, @Nonnull String category) {
    final KeyBinding keyBinding = new KeyBinding(description.trim(), keyCode, category.trim());
    ClientRegistry.registerKeyBinding(keyBinding);
    return keyBinding;
  }

  @SubscribeEvent
  public static void onKeyInput(KeyInputEvent event) {
    for (Pair<KeyBinding, Action> keyAction : keyActions) {
      if (keyAction.getKey().isPressed()) {
        keyAction.getValue().execute();
      }
    }

    if (!isSoundDetectorUpgradeEquipped(Minecraft.getMinecraft().player)) {
      SoundDetector.setEnabled(false);
    }
  }

  public static void sendEnabledChatMessage(@Nonnull String messageBase, boolean isActive) {
    StringUtil.sendEnabledChatMessage(Minecraft.getMinecraft().player, EnderIO.lang.addPrefix(messageBase), isActive);
  }

  public static void toggleDarkSteelController(@Nonnull IDarkSteelUpgrade type, @Nonnull String messageBase) {
    boolean isActive = !DarkSteelController.isActive(Minecraft.getMinecraft().player, type);
    sendEnabledChatMessage(messageBase, isActive);
    DarkSteelController.setActive(Minecraft.getMinecraft().player, type, isActive);
  }

  public static boolean isSoundDetectorUpgradeEquipped(EntityPlayerSP player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    return SoundDetectorUpgrade.INSTANCE.hasUpgrade(helmet);
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
      if (baubles != null) {
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

  private static class InventoryAction implements Action {
    @Override
    public void execute() {
      PacketHandler.INSTANCE.sendToServer(new PacketOpenInventory());
    }
  }

  private static class DSUAction implements Action {
    @Override
    public void execute() {
      PacketHandler.INSTANCE.sendToServer(new PacketOpenDSU());
    }
  }

  private static class JumpAction implements Action {
    @Override
    public void execute() {
      if (JumpUpgrade.isEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(JumpUpgrade.JUMP_ONE, "darksteel.upgrade.jump");
      }
    }
  }

  private static class SpeedAction implements Action {
    @Override
    public void execute() {
      if (SpeedUpgrade.isEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(SpeedUpgrade.SPEED_ONE, "darksteel.upgrade.speed");
      }
    }
  }

  private static class StepAssistAction implements Action {
    @Override
    public void execute() {
      if (JumpUpgrade.isEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(StepAssistUpgrade.INSTANCE, "darksteel.upgrade.stepAssist");
      }
    }
  }

  private static class GogglesAction implements Action {
    @Override
    public void execute() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (GogglesOfRevealingUpgrade.isUpgradeEquipped(player)) {
        toggleDarkSteelController(GogglesOfRevealingUpgrade.INSTANCE, "darksteel.upgrades.goggles");
      }
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
        PacketHandler.INSTANCE.sendToServer(new PacketYetaWrenchDisplayMode(player.inventory.currentItem, newMode));
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
        boolean isActive = !SoundDetector.isEnabled();
        sendEnabledChatMessage("darksteel.upgrade.sound", isActive);
        SoundDetector.setEnabled(isActive);
      }
    }
  }

  private static class GlideAction implements Action {
    @Override
    public void execute() {
      if (DarkSteelController.isGliderUpgradeEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(GliderUpgrade.INSTANCE, "darksteel.upgrade.glider");
      } else if (DarkSteelController.isElytraUpgradeEquipped(Minecraft.getMinecraft().player)) {
        toggleDarkSteelController(ElytraUpgrade.INSTANCE, "darksteel.upgrade.elytra");
      }
    }
  }

  private static class NightVisionAction implements Action {
    @Override
    public void execute() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (DarkSteelController.isNightVisionUpgradeEquipped(player)) {
        boolean isActive = !DarkSteelController.isNightVisionActive(player);
        if (isActive) {
          SoundHelper.playSound(player.world, player, SoundRegistry.NIGHTVISION_ON, 0.1f, player.world.rand.nextFloat() * 0.4f - 0.2f + 1.0f);
        } else {
          SoundHelper.playSound(player.world, player, SoundRegistry.NIGHTVISION_OFF, 0.1f, 1.0f);
        }
        DarkSteelController.setActive(player, NightVisionUpgrade.INSTANCE, isActive);
      }
    }
  }

  private static class TopAction implements Action {
    @Override
    public void execute() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (DarkSteelController.isTopUpgradeEquipped(player)) {
        boolean isActive = !DarkSteelController.isTopActive(player);
        DarkSteelController.setTopActive(player, isActive);
      }
    }
  }

  private static class FovAction implements Action {
    @Override
    public void execute() {
      fovLevelLast = fovLevelNext = 1;
    }
  }

  private static double fovLevelLast = 1;
  private static double fovLevelNext = 1;
  private static long lastWorldTime = 0;

  @SubscribeEvent
  public static void onFov(FOVModifier event) {
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
      fovLevelNext = MathHelper.clamp(fovLevelNext, .05, 1.3);
    }
    double val = fovLevelNext * event.getRenderPartialTicks() + fovLevelLast * (1 - event.getRenderPartialTicks());
    event.setFOV((float) (event.getFOV() * val));
  }

  public static KeyBinding getYetaWrenchMode() {
    return yetaWrenchMode;
  }

}
