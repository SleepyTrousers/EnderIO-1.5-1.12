package crazypants.enderio.base.item.darksteel.upgrade.storage;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;

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
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(
        Rules.or(Rules.forSlot(EntityEquipmentSlot.FEET), Rules.forSlot(EntityEquipmentSlot.LEGS), Rules.forSlot(EntityEquipmentSlot.CHEST),
            Rules.forSlot(EntityEquipmentSlot.HEAD)),
        EnergyUpgrade.HAS_ANY, Rules.withLevels(variant, INSTANCE, INSTANCE2, INSTANCE3), Rules.itemTypeTooltip(Lang.DSU_CLASS_ARMOR));
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return !EnergyUpgradeManager.isLowestPowerUpgrade(other);
  }

  /**
   * How many inventory slots does an item for the given slot have with an upgrade of the given level?
   * <p>
   * This will always be a multiple of the column count. Or 0 if the given level is invalid.
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

  @Override
  @Nullable
  public IItemHandler getInventoryHandler(@Nonnull ItemStack stack) {
    return new StorageCap(NbtValue.INVENTORY, stack);
  }
}
