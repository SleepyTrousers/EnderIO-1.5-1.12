package crazypants.enderio.base.item.darksteel.upgrade.speed;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.item.darksteel.attributes.DarkSteelAttributeModifiers;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.util.NbtValue;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class SpeedUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "speedBoost";

  private static final String[] numbers = { "one", "two", "three" };

  // TODO 1.13: Fix level range for consistency
  public static final @Nonnull SpeedUpgrade SPEED_ONE = new SpeedUpgrade(1);
  public static final @Nonnull SpeedUpgrade SPEED_TWO = new SpeedUpgrade(2);
  public static final @Nonnull SpeedUpgrade SPEED_THREE = new SpeedUpgrade(3);

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(SPEED_ONE);
    event.getRegistry().register(SPEED_TWO);
    event.getRegistry().register(SPEED_THREE);
  }

  private final short level;

  public static SpeedUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    if (SPEED_THREE.hasUpgrade(stack)) {
      return SPEED_THREE;
    }
    if (SPEED_TWO.hasUpgrade(stack)) {
      return SPEED_TWO;
    }
    if (SPEED_ONE.hasUpgrade(stack)) {
      return SPEED_ONE;
    }
    return null;
  }

  public static boolean isEquipped(@Nonnull EntityPlayer player) {
    return loadAnyFromItem(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)) != null;
  }

  public SpeedUpgrade(int level) {
    super(UPGRADE_NAME, level, "enderio.darksteel.upgrade.speed_" + numbers[level - 1], DarkSteelConfig.speedUpgradeCost.get(level - 1));
    this.level = (short) level;
  }

  @Override
  protected int getMinVariant() {
    return 1;
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.LEGS), EnergyUpgrade.HAS_ANY, Rules.withLevels(level, null, SPEED_ONE, SPEED_TWO),
        Rules.itemTypeTooltip(EntityEquipmentSlot.LEGS));
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return !EnergyUpgradeManager.isLowestPowerUpgrade(other);
  }

  public short getLevel() {
    return level;
  }

  @Override
  public void addAttributeModifiers(@Nonnull EntityEquipmentSlot slot, @Nonnull ItemStack stack, @Nonnull Multimap<String, AttributeModifier> map) {
    if (slot == EntityEquipmentSlot.LEGS && !NbtValue.SPEED_DISABLED.getBoolean(stack)) {
      EnergyUpgradeHolder upgradeHolder = EnergyUpgradeManager.loadFromItem(stack);
      if (upgradeHolder != null && upgradeHolder.getEnergy() > 0) {
        AttributeModifier modifier = DarkSteelAttributeModifiers.getWalkSpeed(getLevel(), upgradeHolder.getUpgrade().getLevel());
        map.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), modifier);
      }
    }
  }

  @Override
  public void onPlayerTick(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull EntityPlayer player) {
    if (player.world.isRemote || player.capabilities.isFlying || (!player.onGround && !player.isInWater())) {
      return;
    }

    final boolean isEnabled = DarkSteelController.isActive(player, this);
    if (isEnabled == NbtValue.SPEED_DISABLED.getBoolean(stack)) {
      NbtValue.SPEED_DISABLED.setBoolean(stack, !isEnabled);
    }
    if (!isEnabled) {
      return;
    }

    double horzMovement = Math.abs(player.distanceWalkedModified - player.prevDistanceWalkedModified);
    int costModifier = (player.isSprinting() ? DarkSteelConfig.darkSteelSpeedSprintEnergyCost : DarkSteelConfig.darkSteelSpeedWalkEnergyCost).get();
    int cost = (int) (horzMovement * costModifier);
    DarkSteelController.usePlayerEnergy(player, EntityEquipmentSlot.LEGS, cost);
  }

}
