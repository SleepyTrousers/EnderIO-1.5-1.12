package crazypants.enderio.item.darksteel.upgrade;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GliderUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "glide";
  
  public static final GliderUpgrade INSTANCE = new GliderUpgrade();
  
  public static GliderUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new GliderUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public GliderUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public GliderUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.glider", new ItemStack(ModObject.itemGliderWing,1,1), Config.darkSteelGliderCost);
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
    return GliderUpgradeLayer.instance;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }

}
