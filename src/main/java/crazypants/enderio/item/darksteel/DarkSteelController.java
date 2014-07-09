package crazypants.enderio.item.darksteel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.Util;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector4d;

public class DarkSteelController {

  public static final DarkSteelController instance = new DarkSteelController();

  private AttributeModifier[] walkModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[2], 1),
  };

  private AttributeModifier[] sprintModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[2], 1),
  };

  private AttributeModifier swordDamageModifierPowered = new AttributeModifier(new UUID(63242325, 320981923), "Weapon modifier",
      2, 0);

  private boolean wasJumping;
  private int jumpCount;
  private int ticksSinceLastJump;
  //private boolean isGlideActive = false;
  private Map<String, Boolean> glideActiveMap = new HashMap<String, Boolean>();

  private DarkSteelController() {
    PacketHandler.INSTANCE.registerMessage(PacketDarkSteelPowerPacket.class, PacketDarkSteelPowerPacket.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketGlideState.class, PacketGlideState.class, PacketHandler.nextID(), Side.SERVER);
  }

  public void setGlideActive(EntityPlayer player, boolean isGlideActive) {
    if(player.getGameProfile().getName() != null) {
      glideActiveMap.put(player.getGameProfile().getName(), isGlideActive);
    }
  }

  public boolean isGlideActive(EntityPlayer player) {
    Boolean isActive = glideActiveMap.get(player.getGameProfile().getName());
    if(isActive == null) {
      return false;
    }
    return isActive.booleanValue();
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
    }

  }

  private void updateGlide(EntityPlayer player) {
    if(!isGlideActive(player) || !isGliderUpgradeEquipped(player)) {
      return;
    }

    if(!player.onGround && player.motionY < 0 && !player.isSneaking()) {

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
      if(Config.darkSteelSwordPowerUsePerHit <= 0 || EnderIO.itemDarkSteelSword.getEnergyStored(sword) >= Config.darkSteelSwordPowerUsePerHit) {
        attackInst.applyModifier(swordDamageModifierPowered);
      }
    }
  }

  private void updateSpeed(EntityPlayer player) {
    if(player.worldObj.isRemote) {
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
    if(leggings != null && leggings.getItem() == EnderIO.itemDarkSteelLeggings && speedUpgrade != null) {

      double horzMovement = Math.abs(player.distanceWalkedOnStepModified - player.prevDistanceWalkedModified);
      double costModifier = player.isSprinting() ? Config.darkSteelSprintPowerCost : Config.darkSteelWalkPowerCost;
      costModifier = costModifier + (costModifier * speedUpgrade.walkMultiplier);
      int cost = (int) (horzMovement * costModifier);
      int totalEnergy = getPlayerEnergy(player, EnderIO.itemDarkSteelLeggings);
      
      if(totalEnergy > 0) {        
        usePlayerEnergy(player, EnderIO.itemDarkSteelLeggings, cost);
        if(player.isSprinting()) {
          moveInst.applyModifier(sprintModifiers[speedUpgrade.level - 1]);
        } else {
          moveInst.applyModifier(walkModifiers[speedUpgrade.level - 1]);
        }
      }
    }
  }

  private void updateStepHeightAndFallDistance(EntityPlayer player) {
    ItemStack boots = player.getEquipmentInSlot(1);

    if(boots != null && boots.getItem() == EnderIO.itemDarkSteelBoots) {
      int costedDistance = (int) player.fallDistance;
      if(costedDistance > 0) {
        int energyCost = costedDistance * Config.darkSteelFallDistanceCost;
        int totalEnergy = getPlayerEnergy(player, EnderIO.itemDarkSteelBoots);
        if(totalEnergy > 0 && totalEnergy >= energyCost) {
          usePlayerEnergy(player, EnderIO.itemDarkSteelBoots, energyCost);
          player.fallDistance -= costedDistance;
        }
      }
    }

    JumpUpgrade jumpUpgrade = JumpUpgrade.loadFromItem(boots);
    if(jumpUpgrade != null && boots != null && boots.getItem() == EnderIO.itemDarkSteelBoots) {
      player.stepHeight = 1.0023F;
    } else if(player.stepHeight == 1.0023F) {
      player.stepHeight = 0.5001F;
    }
  }

  void usePlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor, int cost) {
    if(cost == 0) {
      return;
    }
    boolean extracted = false;
    int remaining = cost;
    if(Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        if(stack != null && stack.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem cont = (IEnergyContainerItem) stack.getItem();
          int used = cont.extractEnergy(stack, remaining, false);
          remaining -= used;
          extracted |= used > 0;
          if(remaining <= 0) {
            player.inventory.markDirty();
            return;
          }
        }
      }
    }
    if(armor != null && remaining > 0) {
      ItemStack stack = player.inventory.armorInventory[3 - armor.armorType];
      if(stack != null) {
        int used = armor.extractEnergy(stack, remaining, false);
        extracted |= used > 0;
      }
    }
    if(extracted) {
      player.inventory.markDirty();
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

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if(event.phase == TickEvent.Phase.END) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if(player == null) {
        return;
      }
      MovementInput input = player.movementInput;
      if(input.jump && !wasJumping) {
        doJump(player);
      } else if(input.jump && jumpCount < 3 && ticksSinceLastJump > 5) {
        doJump(player);
      }

      wasJumping = !player.isCollidedVertically;
      if(!wasJumping) {
        jumpCount = 0;
      }
      ticksSinceLastJump++;
    }
  }

  @SideOnly(Side.CLIENT)
  private void doJump(EntityClientPlayerMP player) {

    ItemStack boots = player.getEquipmentInSlot(1);
    JumpUpgrade jumpUpgrade = JumpUpgrade.loadFromItem(boots);

    if(jumpUpgrade == null || boots == null || boots.getItem() != EnderIO.itemDarkSteelBoots) {
      return;
    }

    int requiredPower = Config.darkSteelBootsJumpPowerCost * (int) Math.pow(jumpCount + 1, 2.5);
    int availablePower = getPlayerEnergy(player, EnderIO.itemDarkSteelBoots);
    if(availablePower > 0 && requiredPower <= availablePower && jumpCount < jumpUpgrade.level) {
      jumpCount++;
      player.motionY += 0.15 * Config.darkSteelBootsJumpModifier * jumpCount;
      ticksSinceLastJump = 0;
      usePlayerEnergy(player, EnderIO.itemDarkSteelBoots, requiredPower);
      PacketHandler.INSTANCE.sendToServer(new PacketDarkSteelPowerPacket(requiredPower, EnderIO.itemDarkSteelBoots.armorType));
    }

  }

}
