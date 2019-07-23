package crazypants.enderio.base.integration.top;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
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
  @Nonnull
  public List<Supplier<String>> getItemClassesForTooltip() {
    return new NNList<>(Lang.DSU_CLASS_ARMOR_HEAD::get);
  }

  @Override
  public void addToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    super.addToItem(stack, item);
    ItemUtil.getOrCreateNBT(stack).setInteger(PROBETAG, 1);
  }

  @Override
  public @Nonnull Pair<ItemStack, Integer> removeFromItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    NbtValue.getOrCreateRoot(stack).removeTag(PROBETAG);
    return super.removeFromItem(stack, item);
  }

  public boolean isAvailable() {
    return probe != null && Prep.isValid(probe);
  }

}
