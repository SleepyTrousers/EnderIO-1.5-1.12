package crazypants.enderio.base.item.darksteel.upgrade.glider;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GliderUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "glide";

  public static final @Nonnull GliderUpgrade INSTANCE = new GliderUpgrade();

  public static GliderUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new GliderUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public GliderUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public GliderUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.glider", Material.GLIDER_WINGS.getStack(), Config.darkSteelGliderCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelChestplate.getItemNN() && ElytraUpgrade.loadFromItem(stack) == null
        && GliderUpgrade.loadFromItem(stack) == null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return GliderUpgradeLayer.instance;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

}
