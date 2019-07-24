package crazypants.enderio.base.item.darksteel.upgrade.storage;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class StorageUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "inv";

  public static final @Nonnull StorageUpgrade INSTANCE = new StorageUpgrade(0);
  public static final @Nonnull StorageUpgrade INSTANCE2 = new StorageUpgrade(1);
  public static final @Nonnull StorageUpgrade INSTANCE3 = new StorageUpgrade(2);

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
    event.getRegistry().register(INSTANCE2);
    event.getRegistry().register(INSTANCE3);
  }

  /**
   * Helper list to easily loop over the armor EntityEquipmentSlots in top-to-bottom order
   **/
  protected static final @Nonnull EntityEquipmentSlot[] ARMOR = { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS,
      EntityEquipmentSlot.FEET };

  public StorageUpgrade(int level) {
    super(UPGRADE_NAME, level, "enderio.darksteel.upgrade." + UPGRADE_NAME + "." + level, DarkSteelConfig.inventoryUpgradeCost.get(level));
  }

  // Note: The GUI is bound to ModObject.itemDarkSteelChestplate, but that is just for technical reasons. It supports any armor item with this upgrade
  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return (item.isForSlot(EntityEquipmentSlot.FEET) || item.isForSlot(EntityEquipmentSlot.LEGS) || item.isForSlot(EntityEquipmentSlot.CHEST)
        || item.isForSlot(EntityEquipmentSlot.HEAD)) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack) && getUpgradeVariantLevel(stack) == variant - 1;
  }

  @Override
  @Nonnull
  public List<IDarkSteelUpgrade> getDependencies() {
    switch (variant) {
    case 1:
      return new NNList<>(EnergyUpgrade.UPGRADES.get(0), INSTANCE);
    case 2:
      return new NNList<>(EnergyUpgrade.UPGRADES.get(0), INSTANCE2);
    default:
      return new NNList<>(EnergyUpgrade.UPGRADES.get(0));
    }
  }

  @Override
  @Nonnull
  public List<Supplier<String>> getItemClassesForTooltip() {
    return new NNList<>(Lang.DSU_CLASS_ARMOR::get);
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return !EnergyUpgradeManager.isLowestPowerUpgrade(other);
  }

  /**
   * How many inventory slots does an item for the given slot have with an upgrade of the given level?
   * <p>
   * This will always be a multiple of the column count. Or 0 if the given levelk is invalid.
   */
  protected static int slots(@Nonnull EntityEquipmentSlot slot, int level) {
    return level < 0 ? 0 : cols(slot) * DarkSteelConfig.inventoryUpgradeRows.get(level).get();
  }

  /**
   * How many inventory columns does an item for the given slot have if it's upgraded?
   * <p>
   * Only gives valid data for armor-type slots...
   */
  protected static int cols(@Nonnull EntityEquipmentSlot slot) {
    return DarkSteelConfig.inventoryUpgradeCols.get(slot.getIndex()).get().cols;
  }

}
