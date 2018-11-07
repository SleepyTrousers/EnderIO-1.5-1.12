package crazypants.enderio.base.item.darksteel.upgrade.speed;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.potion.PotionUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
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

  private static @Nonnull ItemStack createUpgradeItem() {
    return PotionUtil.createSwiftnessPotion(true, false);
  }

  public SpeedUpgrade(int level) {
    super(UPGRADE_NAME, level, "enderio.darksteel.upgrade.speed_" + numbers[level - 1], createUpgradeItem(), DarkSteelConfig.speedUpgradeCost.get(level - 1));
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    if (!item.isForSlot(EntityEquipmentSlot.LEGS) || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SpeedUpgrade up = loadAnyFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }

  public short getLevel() {
    return level;
  }

  @Override
  public boolean isUpgradeItem(@Nonnull ItemStack stack) {
    return super.isUpgradeItem(stack) && PotionUtils.getPotionFromItem(getUpgradeItem()).equals(PotionUtils.getPotionFromItem(stack));
  }

}
