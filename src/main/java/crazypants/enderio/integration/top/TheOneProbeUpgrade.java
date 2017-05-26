package crazypants.enderio.integration.top;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.init.ModObject;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

public class TheOneProbeUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "top";
  public static final @Nonnull String PROBETAG = "theoneprobe";

  public static final @Nonnull TheOneProbeUpgrade INSTANCE = new TheOneProbeUpgrade();
  
  @ItemStackHolder("theoneprobe:probe")
  public static ItemStack probe = null;
  
  public static TheOneProbeUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new TheOneProbeUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    return NullHelper.first(probe, Prep.getEmpty());
  }

  public TheOneProbeUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public TheOneProbeUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.top", Prep.getEmpty(), Config.darkSteelTOPCost);
  }  
  
  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (probe == null || stack.getItem() != ModObject.itemDarkSteelHelmet.getItem()) {
      return false;
    }
    TheOneProbeUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
  }

  @Override
  public void writeToItem(@Nonnull ItemStack stack) {
    super.writeToItem(stack);
    ItemUtil.getOrCreateNBT(stack).setInteger(PROBETAG, 1);
  }

  public boolean isAvailable() {
    return probe != null;
  }

}
