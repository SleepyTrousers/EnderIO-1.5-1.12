package crazypants.enderio.integration.top;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.upgrade.AbstractUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

public class TheOneProbeUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "top";
  public static String PROBETAG = "theoneprobe";

  public static final TheOneProbeUpgrade INSTANCE = new TheOneProbeUpgrade();
  
  @ItemStackHolder("theoneprobe:probe")
  public static ItemStack probe = null;
  
  public static TheOneProbeUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new TheOneProbeUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  @Override
  public ItemStack getUpgradeItem() {
    return probe;
  }

  public TheOneProbeUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public TheOneProbeUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.top", null, Config.darkSteelTOPCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if (probe == null || stack == null || stack.getItem() != DarkSteelItems.itemDarkSteelHelmet) {
      return false;
    }
    TheOneProbeUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {    
  }

  @Override
  public void writeToItem(ItemStack stack) {
    super.writeToItem(stack);
    ItemUtil.getOrCreateNBT(stack).setInteger(PROBETAG, 1);
  }

  public boolean isAvailable() {
    return probe != null;
  }

}
