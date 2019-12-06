package crazypants.enderio.base.handler.darksteel;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.integration.top.TheOneProbeUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.item.darksteel.upgrade.glider.GliderUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.jump.JumpUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.nightvision.NightVisionUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetectorUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.stepassist.StepAssistUpgrade;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DarkSteelController {

  private static final @Nonnull NNList<EntityEquipmentSlot> SLOTS = NNList.of(EntityEquipmentSlot.class);

  private static class Data {
    private boolean jumpPre;
    private boolean wasJumping;
    private int jumpCount;
    private int ticksSinceLastJump;
    private boolean wasNightvisionActive = false;
  }

  private static ThreadLocal<Data> DATA = new ThreadLocal<Data>() {
    @Override
    protected Data initialValue() {
      return new Data();
    }
  };

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase == Phase.START && !event.player.isSpectator() && !(event.player instanceof EntityOtherPlayerMP) && !(event.player instanceof FakePlayer)) {
      doPlayerTick(event.player);
    }
  }

  @SubscribeEvent
  @SideOnly(Side.SERVER)
  public static void onPlayerTickServer(TickEvent.PlayerTickEvent event) {
    if (event.phase == Phase.START && !event.player.isSpectator() && !(event.player instanceof FakePlayer)) {
      doPlayerTick(event.player);
    }
  }

  private static void doPlayerTick(@Nonnull EntityPlayer player) {
    // boots
    updateStepHeight(player);

    SLOTS.apply(slot -> {
      ItemStack stack = player.getItemStackFromSlot(slot);
      if (stack.getItem() instanceof IDarkSteelItem) {
        for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
          if (upgrade.hasUpgrade(stack)) {
            upgrade.onPlayerTick(stack, (IDarkSteelItem) stack.getItem(), player);
          }
        }
      }
    });
  }

  public static boolean isGliderUpgradeEquipped(EntityPlayer player) {
    return GliderUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
  }

  public static boolean isElytraUpgradeEquipped(EntityPlayer player) {
    return isElytraUpgradeEquipped(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
  }

  public static boolean isElytraUpgradeEquipped(@Nonnull ItemStack chestPlate) {
    return ElytraUpgrade.INSTANCE.hasUpgrade(chestPlate);
  }

  @SubscribeEvent
  public static void onFall(LivingFallEvent event) {
    float distance = event.getDistance();
    if (distance > 3) {
      ItemStack boots = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET);
      if (boots.getItem() instanceof IDarkSteelItem) {
        int energyStored = EnergyUpgradeManager.getEnergyStored(boots);
        if (energyStored > 0) {
          float toMitigate = distance - 3;
          int energyCost = (int) Math.min(energyStored, Math.ceil(toMitigate * DarkSteelConfig.fallDistanceCost.get()));
          float mitigated = energyCost / (float) DarkSteelConfig.fallDistanceCost.get();
          if (!event.getEntity().world.isRemote) {
            EnergyUpgradeManager.extractEnergy(boots, (IDarkSteelItem) boots.getItem(), energyCost, false);
          }
          if (mitigated < toMitigate) {
            // Log.debug("Mitigating fall damage partially: original=", distance, " mitigated=", mitigated, " remaining=", distance - mitigated, " power used=",
            // energyCost);
            event.setDistance(distance - mitigated);
          } else {
            // Log.debug("Canceling fall damage: original=", distance, " power used=", energyCost);
            event.setCanceled(true);
          }
        }
      }
    }
  }

  private static final float MAGIC_STEP_HEIGHT = 1.0023f;
  private static int stepHeightWarner = 0;

  private static void updateStepHeight(EntityPlayer player) {
    if (!player.isSneaking() && StepAssistUpgrade.isEquipped(player) && StateController.isActive(player, StepAssistUpgrade.INSTANCE)) {
      if (player.stepHeight < MAGIC_STEP_HEIGHT) {
        stepHeightWarner++;
        if (Loader.isModLoaded("clienttweaks") && stepHeightWarner > 20) {
          player.sendStatusMessage(Lang.GUI_STEP_ASSIST_UNAVAILABLE.toChatServer(), true);
          stepHeightWarner = -100; // 1 second after switching on but 6 seconds between repeated warnings
        }
        player.stepHeight = MAGIC_STEP_HEIGHT;
      } else if (stepHeightWarner > 0) {
        stepHeightWarner--;
      }
    } else if (player.stepHeight == MAGIC_STEP_HEIGHT) {
      player.stepHeight = 0.6F;
      stepHeightWarner = 0;
    }
  }

  public static void usePlayerEnergy(EntityPlayer player, EntityEquipmentSlot armorSlot, int cost) {
    if (cost == 0) {
      return;
    }
    int remaining = cost;
    if (DarkSteelConfig.armorDrainPowerFromInventory.get()) {
      for (ItemStack stack : player.inventory.mainInventory) {
        IEnergyStorage cap = PowerHandlerUtil.getCapability(NullHelper.first(stack, Prep.getEmpty()));
        if (cap != null && cap.canExtract()) {
          int used = cap.extractEnergy(remaining, false);
          remaining -= used;
          if (remaining <= 0) {
            return;
          }
        }
      }
    }
    if (armorSlot != null && remaining > 0) {
      ItemStack stack = player.getItemStackFromSlot(armorSlot);
      EnergyUpgradeManager.extractEnergy(stack, remaining, false);
    }
  }

  public static int getPlayerEnergy(EntityPlayer player, EntityEquipmentSlot slot) {
    int result = 0;
    if (DarkSteelConfig.armorDrainPowerFromInventory.get()) {
      for (ItemStack stack : player.inventory.mainInventory) {
        IEnergyStorage cap = PowerHandlerUtil.getCapability(NullHelper.first(stack, Prep.getEmpty()));
        if (cap != null && cap.canExtract()) {
          result += cap.extractEnergy(Integer.MAX_VALUE, true);
        }
      }
    }
    if (slot != null) {
      ItemStack stack = player.getItemStackFromSlot(slot);
      result = EnergyUpgradeManager.getEnergyStored(stack);
    }
    return result;
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    EntityPlayerSP player = Minecraft.getMinecraft().player;

    if (NullHelper.untrust(player) == null) {
      // Log.warn("(in TickEvent.ClientTickEvent) net.minecraft.client.Minecraft.player is marked @Nonnull but it is null.");
      return;
    }
    if (NullHelper.untrust(player.movementInput) == null) {
      // Log.warn("(in TickEvent.ClientTickEvent) net.minecraft.client.entity.EntityPlayerSP.movementInput is marked @Nonnull but it is null.");
      return;
    }

    if (event.phase != TickEvent.Phase.END) {
      DATA.get().jumpPre = player.movementInput.jump;
      return;
    }

    updateNightvision(player);
    if (player.capabilities.isFlying) {
      return;
    }

    MovementInput input = player.movementInput;
    boolean jumpHandled = false;
    if (input.jump && (!DATA.get().wasJumping || DATA.get().ticksSinceLastJump > 5)) {
      jumpHandled = doJump(player);
    }

    if (!jumpHandled && input.jump && !DATA.get().jumpPre && !player.onGround && player.motionY < 0.0D && !player.capabilities.isFlying
        && isElytraUpgradeEquipped(player) && !StateController.isActive(player, ElytraUpgrade.INSTANCE)) {
      StateController.setActive(player, ElytraUpgrade.INSTANCE, true);
    }

    DATA.get().wasJumping = !player.onGround;
    if (!DATA.get().wasJumping) {
      DATA.get().jumpCount = 0;
    }
    DATA.get().ticksSinceLastJump++;
  }

  @SideOnly(Side.CLIENT)
  private static boolean doJump(@Nonnull EntityPlayerSP player) {
    if (!StateController.isActive(player, JumpUpgrade.JUMP_ONE)) {
      return false;
    }

    ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
    JumpUpgrade jumpUpgrade = JumpUpgrade.loadAnyFromItem(boots);

    if (jumpUpgrade == null) {
      return false;
    }

    boolean autoJump = Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.AUTO_JUMP);
    if (autoJump && DATA.get().jumpCount <= 0) {
      DATA.get().jumpCount++;
      return false;
    }

    int autoJumpOffset = autoJump ? 1 : 0;
    int requiredPower = DarkSteelConfig.bootsJumpPowerCost.get() * (int) Math.pow(DATA.get().jumpCount + 1 - autoJumpOffset, 2.5);
    int availablePower = getPlayerEnergy(player, EntityEquipmentSlot.FEET);
    int maxJumps = jumpUpgrade.getLevel() + autoJumpOffset;
    if (availablePower > 0 && requiredPower <= availablePower && DATA.get().jumpCount < maxJumps) {
      DATA.get().jumpCount++;
      player.motionY += 0.15 * DarkSteelConfig.darkSteelBootsJumpModifier.get() * (DATA.get().jumpCount - autoJumpOffset);
      DATA.get().ticksSinceLastJump = 0;

      usePlayerEnergy(player, EntityEquipmentSlot.FEET, requiredPower);
      PacketHandler.INSTANCE.sendToServer(new PacketDarkSteelPowerUse(requiredPower, EntityEquipmentSlot.FEET));

      jumpUpgrade.doMultiplayerSFX(player);
      PacketHandler.INSTANCE.sendToServer(new PacketDarkSteelSFX(jumpUpgrade, player));

      return true;
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  private static void updateNightvision(@Nonnull EntityPlayer player) {
    if (StateController.isActive(player, NightVisionUpgrade.INSTANCE)) {
      if (isNightVisionUpgradeEquipped(player)) {
        player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 210, 0, true, true));
        DATA.get().wasNightvisionActive = true;
        return;
      } else {
        StateController.setActive(player, NightVisionUpgrade.INSTANCE, false);
      }
    }
    if (DATA.get().wasNightvisionActive) {
      player.removePotionEffect(MobEffects.NIGHT_VISION);
      DATA.get().wasNightvisionActive = false;
    }
  }

  public static boolean isNightVisionUpgradeEquipped(@Nonnull EntityPlayer player) {
    return NightVisionUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
  }

  public static boolean isTopUpgradeEquipped(@Nonnull EntityPlayer player) {
    return TheOneProbeUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
  }

  public static void setTopActive(@Nonnull EntityPlayer player, boolean active) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    if (active) {
      ItemUtil.getOrCreateNBT(helmet).setInteger(TheOneProbeUpgrade.PROBETAG, 1);
    } else {
      ItemUtil.getOrCreateNBT(helmet).removeTag(TheOneProbeUpgrade.PROBETAG);
    }
  }

  public static boolean isTopActive(@Nonnull EntityPlayer player) {
    return ItemUtil.getOrCreateNBT(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).hasKey(TheOneProbeUpgrade.PROBETAG);
  }

  public static boolean isSoundDetectorUpgradeEquipped(EntityPlayer player) {
    return SoundDetectorUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
  }

}
