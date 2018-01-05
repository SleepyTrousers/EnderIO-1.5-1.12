package crazypants.enderio.base.item.darksteel.upgrade.elytra;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.glider.GliderUpgrade;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElytraUpgrade extends AbstractUpgrade {

  private static @Nonnull String UPGRADE_NAME = "elytra";
  
  public static final @Nonnull ElytraUpgrade INSTANCE = new ElytraUpgrade();
  
  public ElytraUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.elytra", new ItemStack(Items.ELYTRA), Config.darkSteelElytraCost);
  }  
  
  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelChestplate.getItem() && !ElytraUpgrade.INSTANCE.hasUpgrade(stack)
        && !GliderUpgrade.INSTANCE.hasUpgrade(stack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return ElytraUpgradeLayer.instance;
  }

}
