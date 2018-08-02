package crazypants.enderio.base.item.darksteel.upgrade.speed;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.Log;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.item.darksteel.attributes.DarkSteelAttributeModifiers;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class SpeedController {

  private static boolean ignoreFovEvent = false;

  public static void updateSpeed(@Nonnull EntityPlayer player) {
    if (player.world.isRemote) {
      return;
    }

    clearModifiers(player);

    if (!player.onGround) {
      return;
    }

    SpeedUpgrade speedUpgrade = getActiveSpeedUpgrade(player);
    if (speedUpgrade == null) {
      return;
    }

    double horzMovement = Math.abs(player.distanceWalkedModified - player.prevDistanceWalkedModified);
    int costModifier = (player.isSprinting() ? DarkSteelConfig.darkSteelSpeedSprintEnergyCost : DarkSteelConfig.darkSteelSpeedWalkEnergyCost).get();
    int cost = (int) (horzMovement * costModifier);
    setModifiers(player);
    DarkSteelController.usePlayerEnergy(player, EntityEquipmentSlot.LEGS, cost);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void handleFovUpdate(@Nonnull FOVUpdateEvent evt) {

    if (ignoreFovEvent) {
      return;
    }

    EntityPlayer player = NullHelper.notnullF(evt.getEntity(), "FOVUpdateEvent has no player");

    if (clearModifiers(player)) {
      // set the same as vanilla does without our speed buff
      evt.setNewfov(getVanillaFovModifier(player));
      setModifiers(player);
    }
  }

  private static void setModifiers(@Nonnull EntityPlayer player) {
    SpeedUpgrade speedUpgrade = getActiveSpeedUpgrade(player);
    if (speedUpgrade != null) {
      EnergyUpgrade energyUpgrade = getActiveEnergyUpgrade(player);
      if (energyUpgrade != null) {
        IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
        AttributeModifier modifier = DarkSteelAttributeModifiers.getWalkSpeed(player.isSprinting(), speedUpgrade.getLevel(), energyUpgrade.getLevel());
        moveInst.applyModifier(modifier);
      }
    }
  }

  private static boolean clearModifiers(@Nonnull EntityPlayer player) {
    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
    UUID uuid = DarkSteelAttributeModifiers.getWalkSpeed(false, 1, 0).getID();
    AttributeModifier modifier = moveInst.getModifier(uuid);
    if (modifier != null) {
      moveInst.removeModifier(modifier);
      return true;
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  private static float getVanillaFovModifier(@Nonnull EntityPlayer player) {
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

  private static SpeedUpgrade getActiveSpeedUpgrade(@Nonnull EntityPlayer player) {
    ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
    SpeedUpgrade speedUpgrade = SpeedUpgrade.loadAnyFromItem(leggings);
    if (speedUpgrade == null) {
      return null;
    }
    if (DarkSteelController.isSpeedActive(player) && DarkSteelController.getPlayerEnergy(player, EntityEquipmentSlot.LEGS) > 0) {
      return speedUpgrade;
    }
    return null;
  }

  private static EnergyUpgrade getActiveEnergyUpgrade(@Nonnull EntityPlayer player) {
    ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
    EnergyUpgradeHolder energyUpgradeHolder = EnergyUpgradeManager.loadFromItem(leggings);
    if (energyUpgradeHolder != null && energyUpgradeHolder.getEnergy() > 0) {
      return energyUpgradeHolder.getUpgrade();
    }
    return null;
  }

}
