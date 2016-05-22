package crazypants.enderio.item.darksteel;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4d;
import com.mojang.authlib.GameProfile;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.PacketUpgradeState.Type;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.GliderUpgrade;
import crazypants.enderio.item.darksteel.upgrade.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.NightVisionUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SolarUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpeedUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SwimUpgrade;
import crazypants.enderio.machine.solar.TileEntitySolarPanel;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.sound.SoundHelper;
import crazypants.enderio.sound.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DarkSteelController {

  public static final DarkSteelController instance = new DarkSteelController();

  private final AttributeModifier[] walkModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[2], 1), };

  private final AttributeModifier[] sprintModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[2], 1), };

  private final AttributeModifier swordDamageModifierPowered = new AttributeModifier(new UUID(63242325, 320981923), "Weapon modifier", 2, 0);

  private boolean wasJumping;
  private int jumpCount;
  private int ticksSinceLastJump;

  private static final EnumSet<Type> DEFAULT_ACTIVE = EnumSet.of(Type.SPEED, Type.STEP_ASSIST, Type.JUMP);

  private final Map<UUID, EnumSet<Type>> allActive = new HashMap<UUID, EnumSet<Type>>();

  private boolean nightVisionActive = false;
  private boolean removeNightvision = false;

  private DarkSteelController() {
    PacketHandler.INSTANCE.registerMessage(PacketDarkSteelPowerPacket.class, PacketDarkSteelPowerPacket.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpgradeState.class, PacketUpgradeState.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpgradeState.class, PacketUpgradeState.class, PacketHandler.nextID(), Side.CLIENT);
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

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    EntityPlayer player = event.player;

    if (event.phase == Phase.START) {
      // boots
      updateStepHeightAndFallDistance(player);

      // leggings
      updateSpeed(player);

      // sword
      updateSword(player);

      updateGlide(player);

      updateSwim(player);

      updateSolar(player);

    }

  }

  private void updateSolar(EntityPlayer player) {
    // no processing on client
    if (player.worldObj.isRemote) {
      return;
    }

    ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    SolarUpgrade upgrade = SolarUpgrade.loadFromItem(helm);
    if (upgrade == null
        || !player.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + player.eyeHeight + .25),
            MathHelper.floor_double(player.posZ)))) {
      return;
    }

    int RFperSecond = Math.round(upgrade.getRFPerSec() * TileEntitySolarPanel.calculateLightRatio(player.worldObj));

    int leftover = RFperSecond % 20;
    boolean addExtraRF = player.worldObj.getTotalWorldTime() % 20 < leftover;

    int toAdd = (RFperSecond / 20) + (addExtraRF ? 1 : 0);

    if (toAdd != 0) {

      int nextIndex = player.getEntityData().getInteger("dsarmor:solar") % 4;

      for (int i = 0; i < 4 && toAdd > 0; i++) {
        ItemStack stack = player.inventory.armorInventory[nextIndex];
        if (stack != null
            && (EnergyUpgrade.loadFromItem(stack) != null || (Config.darkSteelSolarChargeOthers && stack.getItem() instanceof IEnergyContainerItem))) {
          toAdd -= ((IEnergyContainerItem) stack.getItem()).receiveEnergy(stack, toAdd, false);
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

  private void updateSword(EntityPlayer player) {
    if (ItemDarkSteelSword.isEquipped(player, EnumHand.MAIN_HAND)) {
      IAttributeInstance attackInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
      attackInst.removeModifier(swordDamageModifierPowered);

      ItemStack sword = player.getHeldItemMainhand();
      if (Config.darkSteelSwordPowerUsePerHit <= 0 || EnergyUpgrade.getEnergyStored(sword) >= Config.darkSteelSwordPowerUsePerHit) {
        attackInst.applyModifier(swordDamageModifierPowered);
      }
    }
  }

  private void updateSpeed(EntityPlayer player) {
    if (player.worldObj.isRemote || !player.onGround) {
      return;
    }

    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
    if (moveInst.getModifier(walkModifiers[0].getID()) != null) {
      moveInst.removeModifier(walkModifiers[0]); // any will so as they all have
                                                 // the same UID
    } else if (moveInst.getModifier(sprintModifiers[0].getID()) != null) {
      moveInst.removeModifier(sprintModifiers[0]);
    }

    ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
    SpeedUpgrade speedUpgrade = SpeedUpgrade.loadFromItem(leggings);
    if (leggings != null && leggings.getItem() == DarkSteelItems.itemDarkSteelLeggings && speedUpgrade != null && isSpeedActive(player)) {

      double horzMovement = Math.abs(player.distanceWalkedModified - player.prevDistanceWalkedModified);
      double costModifier = player.isSprinting() ? Config.darkSteelSprintPowerCost : Config.darkSteelWalkPowerCost;
      costModifier = costModifier + (costModifier * speedUpgrade.getWalkMultiplier());
      int cost = (int) (horzMovement * costModifier);
      int totalEnergy = getPlayerEnergy(player, DarkSteelItems.itemDarkSteelLeggings);

      if (totalEnergy > 0) {
        usePlayerEnergy(player, DarkSteelItems.itemDarkSteelLeggings, cost);
        if (player.isSprinting()) {
          moveInst.applyModifier(sprintModifiers[speedUpgrade.getLevel() - 1]);
        } else {
          moveInst.applyModifier(walkModifiers[speedUpgrade.getLevel() - 1]);
        }
      }
    }
  }

  private void updateStepHeightAndFallDistance(EntityPlayer player) {
    ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);

    if (boots != null && boots.getItem() == DarkSteelItems.itemDarkSteelBoots && !player.capabilities.allowFlying) {
      int costedDistance = (int) player.fallDistance;
      if (costedDistance > 0) {
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
      player.stepHeight = 0.5001F;
    }
  }

  void usePlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor, int cost) {
    if (cost == 0) {
      return;
    }
    int remaining = cost;
    if (Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        if (stack != null && stack.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem cont = (IEnergyContainerItem) stack.getItem();
          int used = cont.extractEnergy(stack, remaining, false);
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
        armor.extractEnergy(stack, remaining, false);
      }
    }
  }

  private int getPlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor) {
    int res = 0;

    if (Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        if (stack != null && stack.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem cont = (IEnergyContainerItem) stack.getItem();
          res += cont.extractEnergy(stack, Integer.MAX_VALUE, true);
        }
      }
    }
    if (armor != null) {
      ItemStack stack = player.getItemStackFromSlot(armor.armorType);
      res = armor.getEnergyStored(stack);
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

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
      if (player == null) {
        return;
      }
      updateNightvision(player);
      if (player.capabilities.isFlying) {
        return;
      }
      MovementInput input = player.movementInput;
      if (input != null) {
        if (input.jump && !wasJumping) {
          doJump(player);
        } else if (input.jump && jumpCount < 3 && ticksSinceLastJump > 5) {
          doJump(player);
        }
      }

      wasJumping = !player.onGround;
      if (!wasJumping) {
        jumpCount = 0;
      }
      ticksSinceLastJump++;
    }
  }

  @SideOnly(Side.CLIENT)
  private void doJump(EntityPlayerSP player) {
    if (!isJumpActive(player)) {
      return;
    }

    ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
    JumpUpgrade jumpUpgrade = JumpUpgrade.loadFromItem(boots);

    if (jumpUpgrade == null || boots == null || boots.getItem() != DarkSteelItems.itemDarkSteelBoots) {
      return;
    }

    int requiredPower = Config.darkSteelBootsJumpPowerCost * (int) Math.pow(jumpCount + 1, 2.5);
    int availablePower = getPlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots);
    if (availablePower > 0 && requiredPower <= availablePower && jumpCount < jumpUpgrade.getLevel()) {
      jumpCount++;
      player.motionY += 0.15 * Config.darkSteelBootsJumpModifier * jumpCount;
      ticksSinceLastJump = 0;
      usePlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots, requiredPower);
      SoundHelper.playSound(player.worldObj, player, SoundRegistry.JUMP, 1.0f, player.worldObj.rand.nextFloat() * 0.5f + 0.75f);
      Random rand = player.worldObj.rand;
      for (int i = rand.nextInt(10) + 5; i >= 0; i--) {
        Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.REDSTONE.getParticleID(),
            player.posX + (rand.nextDouble() * 0.5 - 0.25), player.posY - player.getYOffset(), player.posZ + (rand.nextDouble() * 0.5 - 0.25), 1, 1, 1);
        // TODO 1.9 velocity/speed is no longer exposed. use reflection or subclassing to change
        // fx.setVelocity(player.motionX + (rand.nextDouble() * 0.5 - 0.25), (player.motionY / 2) + (rand.nextDouble() * -0.05),
        // player.motionZ + (rand.nextDouble() * 0.5 - 0.25));
        Minecraft.getMinecraft().effectRenderer.addEffect(fx);
      }
      PacketHandler.INSTANCE.sendToServer(new PacketDarkSteelPowerPacket(requiredPower, DarkSteelItems.itemDarkSteelBoots.armorType));
    }

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
}
