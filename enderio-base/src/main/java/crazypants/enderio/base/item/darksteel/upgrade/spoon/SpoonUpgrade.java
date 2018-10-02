package crazypants.enderio.base.item.darksteel.upgrade.spoon;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SpoonUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "spoon";

  public static final @Nonnull SpoonUpgrade INSTANCE = new SpoonUpgrade();

  public SpoonUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.spoon", new ItemStack(Items.DIAMOND_SHOVEL), DarkSteelConfig.spoonCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isPickaxe() && item.hasUpgradeCallbacks(this) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack) && !hasUpgrade(stack, item);
  }

}
