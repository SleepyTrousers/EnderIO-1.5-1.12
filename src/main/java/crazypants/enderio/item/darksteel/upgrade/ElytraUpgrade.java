package crazypants.enderio.item.darksteel.upgrade;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElytraUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "elytra";
  
  public static final ElytraUpgrade INSTANCE = new ElytraUpgrade();
  
  public static ElytraUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new ElytraUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public ElytraUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public ElytraUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.elytra", new ItemStack(Items.ELYTRA), Config.darkSteelElytraCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ModObject.itemDarkSteelChestplate) {
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
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }

}
