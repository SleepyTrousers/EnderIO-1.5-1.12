package crazypants.enderio.base.item.darksteel.upgrade.explosive;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ExplosiveUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "tnt";

  public static final @Nonnull ExplosiveUpgrade INSTANCE = new ExplosiveUpgrade();

  public ExplosiveUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.tnt", new ItemStack(Blocks.TNT), DarkSteelConfig.explosiveUpgradeCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelPickaxe.getItemNN() && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack) && !hasUpgrade(stack);
  }

}
