package crazypants.enderio.item.darksteel;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.enderio.core.client.ClientUtil;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4d;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.config.Config;
import crazypants.enderio.integration.top.TheOneProbeUpgrade;
import crazypants.enderio.item.darksteel.PacketUpgradeState.Type;
import crazypants.enderio.item.darksteel.upgrade.ElytraUpgrade;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.GliderUpgrade;
import crazypants.enderio.item.darksteel.upgrade.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.NightVisionUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SolarUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpeedController;
import crazypants.enderio.item.darksteel.upgrade.SwimUpgrade;
import crazypants.enderio.machine.solar.TileEntitySolarPanel;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.sound.SoundHelper;
import crazypants.enderio.sound.SoundRegistry;
import crazypants.util.NullHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DarkSteelController {

  private static final EnumSet<Type> DEFAULT_ACTIVE = EnumSet.of(Type.SPEED, Type.STEP_ASSIST, Type.JUMP);
  
  public static final DarkSteelController instance = new DarkSteelController();
 
  private boolean wasJumping;
  private int jumpCount;
  private int ticksSinceLastJump;

  private final SpeedController speedController;

  private final Map<UUID, EnumSet<Type>> allActive = new HashMap<UUID, EnumSet<Type>>();

  private boolean nightVisionActive = false;
  private boolean removeNightvision = false;

  private DarkSteelController() {
    PacketHandler.INSTANCE.registerMessage(PacketDarkSteelPowerPacket.class, PacketDarkSteelPowerPacket.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpgradeState.class, PacketUpgradeState.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpgradeState.class, PacketUpgradeState.class, PacketHandler.nextID(), Side.CLIENT);
    speedController = new SpeedController();
  }
  
  public void register() {
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(speedController);
  }

  private EnumSet<Type> getActiveSet(EntityPlayer player) {
    EnumSet<Type> active;
    GameProfile gameProfile = player.getGameProfile();
    UUID id = gameProfile == null ? null : gameProfile.getId();
    active = id == null ? null : allActive.get(id);
    if (active == null) {
      active = DEFAULT_ACTIVE.clone();
      if (id != null) {
        allActive.put(id, active);
      }
    }
    return active;
  }

  public boolean isActive(EntityPlayer player, Type type) {
    return getActiveSet(player).contains(type);
  }

  public void setActive(EntityPlayer player, Type type, boolean isActive) {
    EnumSet<Type> set = getActiveSet(player);
    if (isActive) {
      set.add(type);
    } else {
      set.remove(type);
    }
  }

  public boolean isGlideActive(EntityPlayer player) {
    return isActive(player, Type.GLIDE);
  }

  public boolean isSpeedActive(EntityPlayer player) {
    return isActive(player, Type.SPEED);
  }

  public boolean isStepAssistActive(EntityPlayer player) {
    return isActive(player, Type.STEP_ASSIST);
  }

  public boolean isJumpActive(EntityPlayer player) {
    return isActive(player, Type.JUMP);
  }

  public boolean isElytraActive(EntityPlayer player) {
    return isActive(player, Type.ELYTRA);
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    EntityPlayer player = event.player;

    if (event.phase == Phase.START) {
      // boots
      updateStepHeightAndFallDistance(player);

      // leggings
      speedController.updateSpeed(player);

      updateGlide(player);

      updateSwim(player);

      updateSolar(player);

    }

  }

  private void updateSolar(EntityPlayer player) {
    // no processing on client
    if (player.world.isRemote) {
      return;
    }

    ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    SolarUpgrade upgrade = SolarUpgrade.loadFromItem(helm);
    if (upgrade == null
        || !player.world.canBlockSeeSky(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY + player.eyeHeight + .25),
            MathHelper.floor(player.posZ)))) {
      return;
    }

    int RFperSecond = Math.round(upgrade.getRFPerSec() * TileEntitySolarPanel.calculateLightRatio(player.world));

    int leftover = RFperSecond % 20;
    boolean addExtraRF = player.world.getTotalWorldTime() % 20 < leftover;

    int toAdd = (RFperSecond / 20) + (addExtraRF ? 1 : 0);

    if (toAdd != 0) {

      int nextIndex = player.getEntityData().getInteger("dsarmor:solar") % 4;

      for (int i = 0; i < 4 && toAdd > 0; i++) {
        ItemStack stack = player.inventory.armorInventory[nextIndex];
        IEnergyStorage cap = PowerHandlerUtil.getCapability(stack, null);
        if (cap != null
            && (EnergyUpgrade.loadFromItem(stack) != null || Config.darkSteelSolarChargeOthers)) {
          toAdd -= cap.receiveEnergy(toAdd, false);
        }
        nextIndex = (nextIndex + 1) % 4;
      }

      player.getEntityData().setInteger("dsarmor:solar", nextIndex);
    }
  }

  private void updateSwim(EntityPlayer player) {
    ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
    SwimUpgrade upgrade = SwimUpgrade.loadFromItem(boots);
    if (upgrade == null) {
      return;
    }
    if (player.isInWater() && !player.capabilities.isFlying) {
      player.motionX *= 1.1;
      player.motionZ *= 1.1;
    }
  }

  private void updateGlide(EntityPlayer player) {
    if (!isGlideActive(player) || !isGliderUpgradeEquipped(player)) {
      return;
    }

    if (!player.onGround && player.motionY < 0 && !player.isSneaking() && !player.isInWater()) {

      double horizontalSpeed = Config.darkSteelGliderHorizontalSpeed;
      double verticalSpeed = Config.darkSteelGliderVerticalSpeed;
      if (player.isSprinting()) {
        verticalSpeed = Config.darkSteelGliderVerticalSpeedSprinting;
      }

      Vector3d look = Util.getLookVecEio(player);
      Vector3d side = new Vector3d();
      side.cross(new Vector3d(0, 1, 0), look);
      Vector3d playerPos = new Vector3d(player.prevPosX, player.prevPosY, player.prevPosZ);
      Vector3d b = new Vector3d(playerPos);
      b.y += 1;
      Vector3d c = new Vector3d(playerPos);
      c.add(side);
      Vector4d plane = new Vector4d();
      VecmathUtil.computePlaneEquation(playerPos, b, c, plane);
      double dist = Math.abs(VecmathUtil.distanceFromPointToPlane(plane, new Vector3d(player.posX, player.posY, player.posZ)));
      double minDist = 0.15;
      if (dist < minDist) {
        double dropRate = (minDist * 10) - (dist * 10);
        verticalSpeed = verticalSpeed + (verticalSpeed * dropRate * 8);
        horizontalSpeed -= (0.02 * dropRate);
      }

      double x = Math.cos(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
      double z = Math.sin(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;

      player.motionX += x;
      player.motionZ += z;

      player.motionY = verticalSpeed;
      player.fallDistance = 0f;

    }

  }

  public boolean isGliderUpgradeEquipped(EntityPlayer player) {
    ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    GliderUpgrade glideUpgrade = GliderUpgrade.loadFromItem(chestPlate);
    if (glideUpgrade == null) {
      return false;
    }
    return true;
  }

  public boolean isElytraUpgradeEquipped(EntityPlayer player) {
    ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    return isElytraUpgradeEquipped(chestPlate);
  }

  public boolean isElytraUpgradeEquipped(ItemStack chestPlate) {
    ElytraUpgrade glideUpgrade = ElytraUpgrade.loadFromItem(chestPlate);
    if (glideUpgrade == null) {
      return false;
    }
    return true;
  }

  private void updateStepHeightAndFallDistance(EntityPlayer player) {
    ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);

    if (boots != null && boots.getItem() == DarkSteelItems.itemDarkSteelBoots && !player.capabilities.allowFlying) {
      int costedDistance = (int) player.fallDistance;
      if (costedDistance > 1) { // Elytra flight will limit fall distance to 1.0F in normal flight
        int energyCost = costedDistance * Config.darkSteelFallDistanceCost;
        int totalEnergy = getPlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots);
        if (totalEnergy > 0 && totalEnergy >= energyCost) {
          usePlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots, energyCost);
          player.fallDistance -= costedDistance;
        }
      }
    }

    JumpUpgrade jumpUpgrade = JumpUpgrade.loadFromItem(boots);
    if (jumpUpgrade != null && boots != null && boots.getItem() == DarkSteelItems.itemDarkSteelBoots && isStepAssistActive(player)) {
      player.stepHeight = 1.0023F;
    } else if (player.stepHeight == 1.0023F) {
      player.stepHeight = 0.6F;
    }
  }

  public void usePlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor, int cost) {
    if (cost == 0) {
      return;
    }
    int remaining = cost;
    if (Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        IEnergyStorage cap = PowerHandlerUtil.getCapability(stack);
        if (cap != null && cap.canExtract()) {
          int used = cap.extractEnergy(remaining, false);
          remaining -= used;
          if (remaining <= 0) {
            return;
          }
        }
      }
    }
    if (armor != null && remaining > 0) {
      ItemStack stack = player.getItemStackFromSlot(armor.armorType);
      if (stack != null) {
        EnergyUpgrade.extractEnergy(stack, remaining, false);
      }
    }
  }

  public int getPlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor) {
    int res = 0;

    if (Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        IEnergyStorage cap = PowerHandlerUtil.getCapability(stack);
        if (cap != null && cap.canExtract()) {
          res += cap.extractEnergy(Integer.MAX_VALUE, true);
        }
      }
    }
    if (armor != null) {
      ItemStack stack = player.getItemStackFromSlot(armor.armorType);
      res = EnergyUpgrade.getEnergyStored(stack);
    }
    return res;
  }

  @SubscribeEvent
  public void onStartTracking(PlayerEvent.StartTracking event) {
    if (event.getTarget() instanceof EntityPlayerMP) {
      for (PacketUpgradeState.Type type : PacketUpgradeState.Type.values()) {
        PacketHandler.sendTo(new PacketUpgradeState(type, isActive((EntityPlayer) event.getTarget(), type), event.getTarget().getEntityId()),
            (EntityPlayerMP) event.getEntityPlayer());
      }
    }
  }

  private boolean jumpPre;

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    EntityPlayerSP player = Minecraft.getMinecraft().player;
    if (player == null) {
      return;
    }

    if (event.phase != TickEvent.Phase.END) {
      jumpPre = player.movementInput == null ? false : player.movementInput.jump;
      return;
    }

    updateNightvision(player);
    if (player.capabilities.isFlying) {
      return;
    }

    MovementInput input = player.movementInput;
    boolean jumpHandled = false;
    if (input != null && input.jump && (!wasJumping || ticksSinceLastJump > 5)) {
      jumpHandled = doJump(player);
    }

    if (!jumpHandled && input != null && input.jump && !jumpPre && !player.onGround && player.motionY < 0.0D && !player.capabilities.isFlying
        && isElytraUpgradeEquipped(player) && !isElytraActive(player)) {
      DarkSteelController.instance.setActive(player, Type.ELYTRA, true);
      PacketHandler.INSTANCE.sendToServer(new PacketUpgradeState(Type.ELYTRA, true));
    }

    wasJumping = !player.onGround;
    if (!wasJumping) {
      jumpCount = 0;
    }
    ticksSinceLastJump++;

  }

  @SideOnly(Side.CLIENT)
  private boolean doJump(EntityPlayerSP player) {
    if (!isJumpActive(player)) {
      return false;
    }

    ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
    JumpUpgrade jumpUpgrade = JumpUpgrade.loadFromItem(boots);

    if (jumpUpgrade == null || boots == null || boots.getItem() != DarkSteelItems.itemDarkSteelBoots) {
      return false;
    }

    boolean autoJump = Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.AUTO_JUMP);
    if (autoJump && jumpCount <= 0) {
      jumpCount++;
      return false;
    }

    int autoJumpOffset = autoJump ? 1 : 0;
    int requiredPower = Config.darkSteelBootsJumpPowerCost * (int) Math.pow(jumpCount + 1 - autoJumpOffset, 2.5);
    int availablePower = getPlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots);
    int maxJumps = jumpUpgrade.getLevel() + autoJumpOffset;
    if (availablePower > 0 && requiredPower <= availablePower && jumpCount < maxJumps) {
      jumpCount++;
      player.motionY += 0.15 * Config.darkSteelBootsJumpModifier * (jumpCount - autoJumpOffset);
      ticksSinceLastJump = 0;
      usePlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots, requiredPower);
      SoundHelper.playSound(player.world, player, SoundRegistry.JUMP, 1.0f, player.world.rand.nextFloat() * 0.5f + 0.75f);

      Random rand = player.world.rand;
      for (int i = rand.nextInt(10) + 5; i >= 0; i--) {
        Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.REDSTONE.getParticleID(),
            player.posX + (rand.nextDouble() * 0.5 - 0.25), player.posY - player.getYOffset(), player.posZ + (rand.nextDouble() * 0.5 - 0.25), 1, 1, 1);
        ClientUtil.setParticleVelocity(fx, player.motionX + (rand.nextDouble() * 0.5 - 0.25), (player.motionY / 2) + (rand.nextDouble() * -0.05),
            player.motionZ + (rand.nextDouble() * 0.5 - 0.25));
        Minecraft.getMinecraft().effectRenderer.addEffect(NullHelper.notnullM(fx, "spawnEffectParticle() failed unexptedly"));
      }
      PacketHandler.INSTANCE.sendToServer(new PacketDarkSteelPowerPacket(requiredPower, DarkSteelItems.itemDarkSteelBoots.armorType));
      return true;
    }
    return false;
  }

  private void updateNightvision(EntityPlayer player) {
    if (isNightVisionUpgradeEquipped(player) && nightVisionActive) {
      player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 210, 0, true, true));
    }
    if (!isNightVisionUpgradeEquipped(player) && nightVisionActive) {
      nightVisionActive = false;
      removeNightvision = true;
    }
    if (removeNightvision) {
      player.removePotionEffect(MobEffects.NIGHT_VISION);
      removeNightvision = false;
    }
  }

  public boolean isNightVisionUpgradeEquipped(EntityPlayer player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    return NightVisionUpgrade.loadFromItem(helmet) != null;
  }

  public void setNightVisionActive(boolean isNightVisionActive) {
    if (nightVisionActive && !isNightVisionActive) {
      removeNightvision = true;
    }
    this.nightVisionActive = isNightVisionActive;
  }

  public boolean isNightVisionActive() {
    return nightVisionActive;
  }

  public boolean isTopUpgradeEquipped(EntityPlayer player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    return TheOneProbeUpgrade.loadFromItem(helmet) != null;
  }

  public void setTopActive(EntityPlayer player, boolean active) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    if (active) {
      ItemUtil.getOrCreateNBT(helmet).setInteger(TheOneProbeUpgrade.PROBETAG, 1);
    } else {
      ItemUtil.getOrCreateNBT(helmet).removeTag(TheOneProbeUpgrade.PROBETAG);
    }
  }

  public boolean isTopActive(EntityPlayer player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    return ItemUtil.getOrCreateNBT(helmet).hasKey(TheOneProbeUpgrade.PROBETAG);
  }
}
