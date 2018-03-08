package crazypants.enderio.base.integration.thaumcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GogglesOfRevealingUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "gogglesRevealing";

  public static final @Nonnull GogglesOfRevealingUpgrade INSTANCE = new GogglesOfRevealingUpgrade();

  public static @Nonnull ItemStack getGoggles() {
    Item i = Item.REGISTRY.getObject(new ResourceLocation("Thaumcraft", "ItemGoggles"));
    if (i != null) {
      return new ItemStack(i);
    }
    return Prep.getEmpty();
  }

  public static boolean isUpgradeEquipped(@Nonnull EntityPlayer player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    return GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(helmet);
  }

  public GogglesOfRevealingUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.gogglesOfRevealing", getGoggles(), Config.darkSteelGogglesOfRevealingCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    if (!item.isForSlot(EntityEquipmentSlot.HEAD) || Prep.isInvalid(getGoggles())) {
      return false;
    }
    return item.hasUpgradeCallbacks(this) && !hasUpgrade(stack, item);
  }

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    if (Prep.isValid(upgradeItem)) {
      return upgradeItem;
    }
    upgradeItem = getGoggles();
    return upgradeItem;
  }

  @Override
  public @Nonnull String getUpgradeItemName() {
    if (Prep.isInvalid(getUpgradeItem())) {
      return "Goggles of Revealing";
    }
    return super.getUpgradeItemName();
  }

}
