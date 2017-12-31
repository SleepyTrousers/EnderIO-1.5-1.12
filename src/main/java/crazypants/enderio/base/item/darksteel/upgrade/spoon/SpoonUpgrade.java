package crazypants.enderio.base.item.darksteel.upgrade.spoon;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SpoonUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "spoon";

  public static final @Nonnull SpoonUpgrade INSTANCE = new SpoonUpgrade();

  public SpoonUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.spoon", new ItemStack(Items.DIAMOND_SHOVEL), Config.darkSteelSpoonCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelPickaxe.getItemNN() && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack) && !hasUpgrade(stack);
  }

}
