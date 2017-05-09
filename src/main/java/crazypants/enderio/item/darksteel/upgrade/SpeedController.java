package crazypants.enderio.item.darksteel.upgrade;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.Log;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelController;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpeedController {

  private static final @Nonnull AttributeModifier[] walkModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.WALK_MULTIPLIERS[2], 1), };

  private static final @Nonnull AttributeModifier[] sprintModifiers = new AttributeModifier[] {
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[0], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[1], 1),
      new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", SpeedUpgrade.SPRINT_MULTIPLIERS[2], 1), };

  private boolean ignoreFovEvent = false;

  public void updateSpeed(EntityPlayer player) {
    if (player.world.isRemote || !player.onGround) {
      return;
    }

    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
    if (moveInst.getModifier(walkModifiers[0].getID()) != null) {
      moveInst.removeModifier(walkModifiers[0].getID()); // any will so as they
                                                         // all have
      // the same UID
    } else if (moveInst.getModifier(sprintModifiers[0].getID()) != null) {
      moveInst.removeModifier(sprintModifiers[0].getID());
    }

    SpeedUpgrade speedUpgrade = getActiveSpeedUpgrade(player);
    if (speedUpgrade == null) {
      return;
    }

    double horzMovement = Math.abs(player.distanceWalkedModified - player.prevDistanceWalkedModified);
    double costModifier = player.isSprinting() ? Config.darkSteelSprintPowerCost : Config.darkSteelWalkPowerCost;
    costModifier = costModifier + (costModifier * speedUpgrade.getWalkMultiplier());
    int cost = (int) (horzMovement * costModifier);
    DarkSteelController.instance.usePlayerEnergy(player, DarkSteelItems.itemDarkSteelLeggings, cost);
    if (player.isSprinting()) {
      moveInst.applyModifier(sprintModifiers[speedUpgrade.getLevel() - 1]);
    } else {
      moveInst.applyModifier(walkModifiers[speedUpgrade.getLevel() - 1]);
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void handleFovUpdate(FOVUpdateEvent evt) {

    if (ignoreFovEvent) {
      return;
    }

    // Config.darkSteelSpeedLimitFovChanges;
    // Config.darkSteelSpeedRemoveFovChanges;
    boolean limitFov = true;
    boolean disableFov = false;
    if (!limitFov && !disableFov) {
      return;
    }

    EntityPlayer player = evt.getEntity();
    SpeedUpgrade speedUpgrade = getActiveSpeedUpgrade(player);
    if (speedUpgrade == null) {
      return;
    }

    if (disableFov) {
      if (!isBowDrawn(player)) {
        evt.setNewfov(1);
      } else {
        evt.setNewfov(getVanillaFovModifier(player));
      }
    } else if (limitFov) {
      // set the same as vanilla does without our speed buff
      IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (moveInst.getModifier(walkModifiers[0].getID()) != null) {
        moveInst.removeModifier(walkModifiers[0].getID());
      } else if (moveInst.getModifier(sprintModifiers[0].getID()) != null) {
        moveInst.removeModifier(sprintModifiers[0].getID());
      }
      evt.setNewfov(getVanillaFovModifier(player));
      if (player.isSprinting()) {
        moveInst.applyModifier(sprintModifiers[speedUpgrade.getLevel() - 1]);
      } else {
        moveInst.applyModifier(walkModifiers[speedUpgrade.getLevel() - 1]);
      }
    }

  }

  @SideOnly(Side.CLIENT)
  private float getVanillaFovModifier(EntityPlayer player) {
    if (!(player instanceof AbstractClientPlayer)) {
      Log.warn("invalid player type when adjusting FOV " + player);
      return 1;
    }
    try {
      ignoreFovEvent = true;
      return ((AbstractClientPlayer) player).getFovModifier();
    } finally {
      ignoreFovEvent = false;
    }
  }

  private boolean isBowDrawn(EntityPlayer player) {
    return player.isHandActive() && player.getActiveItemStack() != null && player.getActiveItemStack().getItem() == Items.BOW;
  }

  private SpeedUpgrade getActiveSpeedUpgrade(EntityPlayer player) {
    ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
    if (leggings == null || leggings.getItem() != DarkSteelItems.itemDarkSteelLeggings) {
      return null;
    }
    SpeedUpgrade speedUpgrade = SpeedUpgrade.loadFromItem(leggings);
    if (speedUpgrade == null) {
      return null;
    }
    if (DarkSteelController.instance.isSpeedActive(player) && DarkSteelController.instance.getPlayerEnergy(player, DarkSteelItems.itemDarkSteelLeggings) > 0) {
      return speedUpgrade;
    }
    return null;
  }

}
