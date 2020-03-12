package crazypants.enderio.item.darksteel;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyContainerItem;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4d;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.PacketUpgradeState.Type;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.GliderUpgrade;
import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import crazypants.enderio.item.darksteel.upgrade.IRenderUpgrade;
import crazypants.enderio.item.darksteel.upgrade.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.NightVisionUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SolarUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpeedUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SwimUpgrade;
import crazypants.enderio.machine.solar.TileEntitySolarPanel;
import crazypants.enderio.network.PacketHandler;

public class DarkSteelController {

  public static final DarkSteelController instance = new DarkSteelController();

  private final AttributeModifier[] walkModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[2], 1),
  };

  private final AttributeModifier[] sprintModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[2], 1),
  };

  private final AttributeModifier swordDamageModifierPowered = new AttributeModifier(new UUID(63242325, 320981923), "Weapon modifier",
      2, 0);

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
    if(active == null) {
      active = DEFAULT_ACTIVE.clone();
      if(id != null) {
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
    if(isActive) {
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

    if(event.phase == Phase.START) {
      //boots
      updateStepHeightAndFallDistance(player);

      //leggings
      updateSpeed(player);

      //sword
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

    ItemStack helm = player.getEquipmentInSlot(4);
    SolarUpgrade upgrade = SolarUpgrade.loadFromItem(helm);
    if(upgrade == null) {
      return;
    }

    int RFperSecond = Math.round((float) upgrade.getRFPerSec() * TileEntitySolarPanel.calculateLightRatio(player.worldObj, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + 1), MathHelper.floor_double(player.posZ)));

    int leftover = RFperSecond % 20;
    boolean addExtraRF = player.worldObj.getTotalWorldTime() % 20 < leftover;

    int toAdd = (RFperSecond / 20) + (addExtraRF ? 1 : 0);

    if(toAdd != 0) {

      int nextIndex = player.getEntityData().getInteger("dsarmor:solar") % 4;

      for (int i = 0; i < 4 && toAdd > 0; i++) {
        ItemStack stack = player.inventory.armorInventory[nextIndex];
        if(stack != null && (EnergyUpgrade.loadFromItem(stack) != null || (Config.darkSteelSolarChargeOthers && stack.getItem() instanceof IEnergyContainerItem))) {
          toAdd -= ((IEnergyContainerItem) stack.getItem()).receiveEnergy(stack, toAdd, false);
        }
        nextIndex = (nextIndex + 1) % 4;
      }

      player.getEntityData().setInteger("dsarmor:solar", nextIndex);
    }
  }

  private void updateSwim(EntityPlayer player) {
    ItemStack boots = player.getEquipmentInSlot(1);
    SwimUpgrade upgrade = SwimUpgrade.loadFromItem(boots);
    if(upgrade == null) {
      return;
    }
    if(player.isInWater() && !player.capabilities.isFlying) {
      player.motionX *= 1.1;
      player.motionZ *= 1.1;
    }
  }

  private void updateGlide(EntityPlayer player) {
    if(!isGlideActive(player) || !isGliderUpgradeEquipped(player)) {
      return;
    }

    if(!player.onGround && player.motionY < 0 && !player.isSneaking() && !player.isInWater()) {

      double horizontalSpeed = Config.darkSteelGliderHorizontalSpeed;
      double verticalSpeed = Config.darkSteelGliderVerticalSpeed;
      if(player.isSprinting()) {
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
      if(dist < minDist) {
        double dropRate = (minDist * 10) - (dist * 10);
        verticalSpeed = verticalSpeed + (verticalSpeed * dropRate * 8);
        horizontalSpeed -= (0.02 * dropRate);
      }

      double x = Math.cos(Math.toRadians(player.rotationYawHead + 90))
          * horizontalSpeed;
      double z = Math.sin(Math.toRadians(player.rotationYawHead + 90))
          * horizontalSpeed;

      player.motionX += x;
      player.motionZ += z;

      player.motionY = verticalSpeed;
      player.fallDistance = 0f;

    }

  }

  public boolean isGliderUpgradeEquipped(EntityPlayer player) {
    ItemStack chestPlate = player.getEquipmentInSlot(3);
    GliderUpgrade glideUpgrade = GliderUpgrade.loadFromItem(chestPlate);
    if(glideUpgrade == null) {
      return false;
    }
    return true;
  }

  private void updateSword(EntityPlayer player) {
    if(ItemDarkSteelSword.isEquipped(player)) {
      IAttributeInstance attackInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage);
      attackInst.removeModifier(swordDamageModifierPowered);

      ItemStack sword = player.getCurrentEquippedItem();
      if(Config.darkSteelSwordPowerUsePerHit <= 0 || EnergyUpgrade.getEnergyStored(sword) >= Config.darkSteelSwordPowerUsePerHit) {
        attackInst.applyModifier(swordDamageModifierPowered);
      }
    }
  }

  private void updateSpeed(EntityPlayer player) {
    if(player.worldObj.isRemote || !player.onGround) {
      return;
    }

    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
    if(moveInst.getModifier(walkModifiers[0].getID()) != null) {
      moveInst.removeModifier(walkModifiers[0]); //any will so as they all have the same UID
    } else if(moveInst.getModifier(sprintModifiers[0].getID()) != null) {
      moveInst.removeModifier(sprintModifiers[0]);
    }

    ItemStack leggings = player.getEquipmentInSlot(2);
    SpeedUpgrade speedUpgrade = SpeedUpgrade.loadFromItem(leggings);
    if(leggings != null && DarkSteelItems.isArmorPart(leggings.getItem(),2) && speedUpgrade != null && isSpeedActive(player)) {

      double horzMovement = Math.abs(player.distanceWalkedModified - player.prevDistanceWalkedModified);
      double costModifier = player.isSprinting() ? Config.darkSteelSprintPowerCost : Config.darkSteelWalkPowerCost;
      costModifier = costModifier + (costModifier * speedUpgrade.getWalkMultiplier());
      int cost = (int) (horzMovement * costModifier);
      int totalEnergy = getPlayerEnergy(player, DarkSteelItems.itemDarkSteelLeggings);

      if(totalEnergy > 0) {
        usePlayerEnergy(player, DarkSteelItems.itemDarkSteelLeggings, cost);
        if(player.isSprinting()) {
          moveInst.applyModifier(sprintModifiers[speedUpgrade.getLevel() - 1]);
        } else {
          moveInst.applyModifier(walkModifiers[speedUpgrade.getLevel() - 1]);
        }
      }
    }
  }

  private void updateStepHeightAndFallDistance(EntityPlayer player) {
    ItemStack boots = player.getEquipmentInSlot(1);

    if(boots != null && !DarkSteelItems.isArmorPart(boots.getItem(), 3) && !player.capabilities.allowFlying) {
      int costedDistance = (int) player.fallDistance;
      if(costedDistance > 0) {
        int energyCost = costedDistance * Config.darkSteelFallDistanceCost;
        int totalEnergy = getPlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots);
        if(totalEnergy > 0 && totalEnergy >= energyCost) {
          usePlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots, energyCost);
          player.fallDistance -= costedDistance;
        }
      }
    }

    JumpUpgrade jumpUpgrade = JumpUpgrade.loadFromItem(boots);
    if(jumpUpgrade != null && boots != null && boots.getItem() == DarkSteelItems.itemDarkSteelBoots && isStepAssistActive(player)) {
      player.stepHeight = 1.0023F;
    } else if(player.stepHeight == 1.0023F) {
      player.stepHeight = 0.5001F;
    }
  }

  void usePlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor, int cost) {
    if(cost == 0) {
      return;
    }
    int remaining = cost;
    if(Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        if(stack != null && stack.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem cont = (IEnergyContainerItem) stack.getItem();
          int used = cont.extractEnergy(stack, remaining, false);
          remaining -= used;
          if(remaining <= 0) {
            return;
          }
        }
      }
    }
    if(armor != null && remaining > 0) {
      ItemStack stack = player.inventory.armorInventory[3 - armor.armorType];
      if(stack != null) {
        armor.extractEnergy(stack, remaining, false);
      }
    }
  }

  private int getPlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor) {
    int res = 0;

    if(Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        if(stack != null && stack.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem cont = (IEnergyContainerItem) stack.getItem();
          res += cont.extractEnergy(stack, Integer.MAX_VALUE, true);
        }
      }
    }
    if(armor != null) {
      ItemStack stack = player.inventory.armorInventory[3 - armor.armorType];
      res = armor.getEnergyStored(stack);
    }
    return res;
  }

  @SubscribeEvent
  public void onStartTracking(PlayerEvent.StartTracking event) {
    if (event.target instanceof EntityPlayerMP) {
      for (PacketUpgradeState.Type type : PacketUpgradeState.Type.values()) {
        PacketHandler.sendTo(new PacketUpgradeState(type, isActive((EntityPlayer) event.target, type), event.target.getEntityId()), (EntityPlayerMP) event.entityPlayer);
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if(event.phase == TickEvent.Phase.END) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if (player == null) {
        return;
      }
      updateNightvision(player);
      if (player.capabilities.isFlying) {
        return;
      }
      MovementInput input = player.movementInput;
      if(input.jump && !wasJumping) {
        doJump(player);
      } else if(input.jump && jumpCount < 3 && ticksSinceLastJump > 5) {
        doJump(player);
      }

      wasJumping = !player.onGround;
      if(!wasJumping) {
        jumpCount = 0;
      }
      ticksSinceLastJump++;
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onPlayerRender(RenderPlayerEvent.Specials.Post event) {
    if (event.entityLiving.getActivePotionEffect(Potion.invisibility) != null) {
      return;
    }

    EntityPlayer player = event.entityPlayer;
    ItemStack[] armors = player.inventory.armorInventory;

    dispatchRenders(armors, event, false);

    float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * event.partialRenderTick;
    float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick;
    float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * event.partialRenderTick;

    GL11.glPushMatrix();
    if(player.isSneaking()) {
      GL11.glTranslatef(0, 0.0625f, 0);
    }
    GL11.glRotatef(yawOffset, 0, -1, 0);
    GL11.glRotatef(yaw - 270, 0, 1, 0);
    GL11.glRotatef(pitch, 0, 0, 1);
    dispatchRenders(armors, event, true);
    GL11.glPopMatrix();
  }

  private void dispatchRenders(ItemStack[] armors, RenderPlayerEvent event, boolean head) {
    for (int i = 0; i < armors.length; i++) {
      ItemStack stack = armors[i];
      if (stack != null) {
        Item item = stack.getItem();

        if (item instanceof IDarkSteelItem) {
          for (IDarkSteelUpgrade upg : DarkSteelRecipeManager.instance.getUpgrades()) {
            if (upg.hasUpgrade(stack)) {
              GL11.glPushMatrix();
              GL11.glColor4f(1F, 1F, 1F, 1F);
              IRenderUpgrade render = upg.getRender();
              if (render != null) {
                upg.getRender().render(event, stack, head);
              }
              GL11.glPopMatrix();
            }
          }
        }
      }
    }
  }

  @SideOnly(Side.CLIENT)
  private void doJump(EntityClientPlayerMP player) {
    if (!isJumpActive(player)) {
      return;
    }

    ItemStack boots = player.getEquipmentInSlot(1);
    JumpUpgrade jumpUpgrade = JumpUpgrade.loadFromItem(boots);

    if(jumpUpgrade == null || boots == null || !DarkSteelItems.isArmorPart(boots.getItem(), 3)) {
      return;
    }

    int requiredPower = Config.darkSteelBootsJumpPowerCost * (int) Math.pow(jumpCount + 1, 2.5);
    int availablePower = getPlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots);
    if(availablePower > 0 && requiredPower <= availablePower && jumpCount < jumpUpgrade.getLevel()) {
      jumpCount++;
      player.motionY += 0.15 * Config.darkSteelBootsJumpModifier * jumpCount;
      ticksSinceLastJump = 0;
      usePlayerEnergy(player, DarkSteelItems.itemDarkSteelBoots, requiredPower);
      player.worldObj.playSound(player.posX, player.posY, player.posZ, EnderIO.MODID + ":ds.jump", 1.0f, player.worldObj.rand.nextFloat() * 0.5f + 0.75f, false);
      Random rand = player.worldObj.rand;
      for (int i = rand.nextInt(10) + 5; i >= 0; i--) {
        EntityReddustFX fx = new EntityReddustFX(player.worldObj, player.posX + (rand.nextDouble() * 0.5 - 0.25), player.posY - player.yOffset, player.posZ
            + (rand.nextDouble() * 0.5 - 0.25), 1, 1, 1);
        fx.setVelocity(player.motionX + (rand.nextDouble() * 0.5 - 0.25), (player.motionY / 2) + (rand.nextDouble() * -0.05),
            player.motionZ + (rand.nextDouble() * 0.5 - 0.25));
        Minecraft.getMinecraft().effectRenderer.addEffect(fx);
      }
      PacketHandler.INSTANCE.sendToServer(new PacketDarkSteelPowerPacket(requiredPower, DarkSteelItems.itemDarkSteelBoots.armorType));
    }

  }


  private void updateNightvision(EntityPlayer player) {
    if(isNightVisionUpgradeOrEnchEquipped(player) && nightVisionActive) {
      player.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 210, 0, true));
    }
    if(!isNightVisionUpgradeOrEnchEquipped(player) && nightVisionActive) {
      nightVisionActive = false;
      removeNightvision = true;
    }
    if(removeNightvision) {
      player.removePotionEffect(Potion.nightVision.getId());
      removeNightvision = false;
    }
  }

  // --- Thaumic Exploration Night Vision enchant support

  private static int nightVisionEnchID = -6;

  public static int getNightVisionEnchID() {
    if (nightVisionEnchID == -6) setNightVisionEnchID();
    return nightVisionEnchID;
  }

  private static void setNightVisionEnchID() {
    for (Enchantment ench : Enchantment.enchantmentsList) {
      if (ench != null && ench.getName().equals("enchantment.nightVision")) {
        nightVisionEnchID = ench.effectId;
        return;
      }
    } nightVisionEnchID = -1;
  }

  public boolean isNightVisionEnch(ItemStack helmet) {
    if (helmet == null) return false;
    NBTTagList stackEnch = helmet.getEnchantmentTagList();
    if (getNightVisionEnchID () >= 0 && stackEnch != null) {
      for (int i = 0; i < stackEnch.tagCount(); i++) {
        int id = stackEnch.getCompoundTagAt(i).getInteger("id");
        if (id == getNightVisionEnchID())
          return true;
      }
    }
    return false;
  }

  // ---

  public boolean isNightVisionUpgradeOrEnchEquipped(EntityPlayer player) {
    ItemStack helmet = player.getEquipmentInSlot(4);
    return (NightVisionUpgrade.loadFromItem(helmet) != null || isNightVisionEnch(helmet));
  }

  public void setNightVisionActive(boolean isNightVisionActive) {
    if(nightVisionActive && !isNightVisionActive) {
      removeNightvision = true;
    }
    this.nightVisionActive = isNightVisionActive;
  }

  public boolean isNightVisionActive() {
    return nightVisionActive;
  }
}
