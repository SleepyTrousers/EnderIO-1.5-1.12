package crazypants.enderio.item.darksteel.upgrade.elytra;

import javax.annotation.Nonnull;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.item.darksteel.upgrade.glider.GliderUpgrade;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElytraUpgrade extends AbstractUpgrade {

  private static @Nonnull String UPGRADE_NAME = "elytra";
  
  public static final @Nonnull ElytraUpgrade INSTANCE = new ElytraUpgrade();
  
  public static ElytraUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new ElytraUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public ElytraUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public ElytraUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.elytra", new ItemStack(Items.ELYTRA), Config.darkSteelElytraCost);
  }  
  
  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelChestplate.getItem()) {
      return false;
    }
    ElytraUpgrade elytraUpgrade = ElytraUpgrade.loadFromItem(stack);
    GliderUpgrade gliderUpgrade = GliderUpgrade.loadFromItem(stack);
    if (elytraUpgrade == null && gliderUpgrade == null) {
      return true;
    }
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return ElytraUpgradeLayer.instance;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

}
