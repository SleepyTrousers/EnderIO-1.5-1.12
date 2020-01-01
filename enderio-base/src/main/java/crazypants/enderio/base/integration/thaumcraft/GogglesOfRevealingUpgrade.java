package crazypants.enderio.base.integration.thaumcraft;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class GogglesOfRevealingUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "gogglesOfRevealing";

  public static final @Nonnull GogglesOfRevealingUpgrade INSTANCE = new GogglesOfRevealingUpgrade();

  public static boolean isUpgradeEquipped(@Nonnull EntityPlayer player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    return GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(helmet);
  }

  public GogglesOfRevealingUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade." + UPGRADE_NAME, DarkSteelConfig.gogglesOfRevealingCost);
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.HEAD), Rules.callbacksFor(this), Rules.itemTypeTooltip(EntityEquipmentSlot.HEAD));
  }

  @Override
  public boolean keybindingDefault() {
    return true;
  }

}
