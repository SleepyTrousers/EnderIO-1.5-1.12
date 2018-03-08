package crazypants.enderio.base.item.darksteel.upgrade.elytra;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.glider.GliderUpgrade;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElytraUpgrade extends AbstractUpgrade implements IHasPlayerRenderer {

  private static @Nonnull String UPGRADE_NAME = "elytra";

  public static final @Nonnull ElytraUpgrade INSTANCE = new ElytraUpgrade();

  public ElytraUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.elytra", new ItemStack(Items.ELYTRA), Config.darkSteelElytraCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.CHEST) && item.hasUpgradeCallbacks(this) && !hasUpgrade(stack, item)
        && !GliderUpgrade.INSTANCE.hasUpgrade(stack, item);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderUpgrade getRender() {
    return ElytraUpgradeLayer.instance;
  }

}
