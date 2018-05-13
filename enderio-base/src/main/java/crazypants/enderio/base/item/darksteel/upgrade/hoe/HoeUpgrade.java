package crazypants.enderio.base.item.darksteel.upgrade.hoe;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HoeUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "hoe";

  public static final @Nonnull HoeUpgrade INSTANCE = new HoeUpgrade();

  public HoeUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.hoe", new ItemStack(Items.DIAMOND_HOE), DarkSteelConfig.darkSteelHoeCost.get());
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isAxe() && item.hasUpgradeCallbacks(this) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack) && !hasUpgrade(stack, item);
  }

}
