package crazypants.enderio.base.item.darksteel.upgrade.speed;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.Log;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.item.darksteel.DarkSteelAttributeModifier;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpeedController {

  private boolean ignoreFovEvent = false;

  public void updateSpeed(@Nonnull EntityPlayer player) {
    if (player.world.isRemote || !player.onGround) {
      return;
    }

    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
    if (moveInst.getModifier(DarkSteelAttributeModifier.UU_ID) != null) {
      moveInst.removeModifier(DarkSteelAttributeModifier.UU_ID);
    }

    SpeedUpgrade speedUpgrade = getActiveSpeedUpgrade(player);
    if (speedUpgrade == null) {
      return;
    }

    double horzMovement = Math.abs(player.distanceWalkedModified - player.prevDistanceWalkedModified);
    double costModifier = player.isSprinting() ? Config.darkSteelSprintPowerCost : Config.darkSteelWalkPowerCost;
    int cost = (int) (horzMovement * costModifier);
    DarkSteelController.instance.usePlayerEnergy(player, EntityEquipmentSlot.LEGS, cost);
    moveInst.applyModifier(player.isSprinting() ? DarkSteelAttributeModifier.getSprintSpeed(speedUpgrade.getLevel())
        : DarkSteelAttributeModifier.getWalkSpeed(speedUpgrade.getLevel()));
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void handleFovUpdate(@Nonnull FOVUpdateEvent evt) {

    if (ignoreFovEvent) {
      return;
    }

    EntityPlayer player = NullHelper.notnullF(evt.getEntity(), "FOVUpdateEvent has no player");

    // set the same as vanilla does without our speed buff
    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
    if (moveInst.getModifier(DarkSteelAttributeModifier.UU_ID) != null) {
      moveInst.removeModifier(DarkSteelAttributeModifier.UU_ID);
      evt.setNewfov(getVanillaFovModifier(player));

      SpeedUpgrade speedUpgrade = getActiveSpeedUpgrade(player);
      if (speedUpgrade != null) {
        moveInst.applyModifier(player.isSprinting() ? DarkSteelAttributeModifier.getSprintSpeed(speedUpgrade.getLevel())
            : DarkSteelAttributeModifier.getWalkSpeed(speedUpgrade.getLevel()));
      }
    }
  }

  @SideOnly(Side.CLIENT)
  private float getVanillaFovModifier(@Nonnull EntityPlayer player) {
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

  private SpeedUpgrade getActiveSpeedUpgrade(@Nonnull EntityPlayer player) {
    ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
    SpeedUpgrade speedUpgrade = SpeedUpgrade.loadAnyFromItem(leggings);
    if (speedUpgrade == null) {
      return null;
    }
    if (DarkSteelController.instance.isSpeedActive(player) && DarkSteelController.instance.getPlayerEnergy(player, EntityEquipmentSlot.LEGS) > 0) {
      return speedUpgrade;
    }
    return null;
  }

}
