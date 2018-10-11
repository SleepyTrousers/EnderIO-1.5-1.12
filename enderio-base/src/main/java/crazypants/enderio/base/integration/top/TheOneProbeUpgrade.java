package crazypants.enderio.base.integration.top;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

public class TheOneProbeUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "top";
  public static final @Nonnull String PROBETAG = "theoneprobe";

  static private TheOneProbeUpgrade INSTANCE;

  public static @Nonnull TheOneProbeUpgrade getInstance() {
    // need to delay creation so we don't run when the ItemStackHolder classloads us, which is too early
    return INSTANCE != null ? INSTANCE : (INSTANCE = new TheOneProbeUpgrade());
  }

  @ItemStackHolder("theoneprobe:probe")
  public static ItemStack probe = null;

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    return NullHelper.first(probe, Prep.getEmpty());
  }

  public TheOneProbeUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.top", Prep.getEmpty(), DarkSteelConfig.topCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return isAvailable() && item.isForSlot(EntityEquipmentSlot.HEAD) && !hasUpgrade(stack, item);
  }

  @Override
  public void addToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    super.addToItem(stack, item);
    ItemUtil.getOrCreateNBT(stack).setInteger(PROBETAG, 1);
  }

  public boolean isAvailable() {
    return probe != null && Prep.isValid(probe);
  }

}
