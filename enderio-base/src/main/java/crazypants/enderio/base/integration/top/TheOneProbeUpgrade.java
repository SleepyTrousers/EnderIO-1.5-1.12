package crazypants.enderio.base.integration.top;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.util.NbtValue;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class TheOneProbeUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "top";
  public static final @Nonnull String PROBETAG = "theoneprobe";

  public static final @Nonnull TheOneProbeUpgrade INSTANCE = new TheOneProbeUpgrade();

  public TheOneProbeUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.top", DarkSteelConfig.topCost);
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.HEAD), Rules.itemTypeTooltip(EntityEquipmentSlot.HEAD));
  }

  @Override
  public void addToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    super.addToItem(stack, item);
    ItemUtil.getOrCreateNBT(stack).setInteger(PROBETAG, 1);
  }

  @Override
  public void removeFromItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    NbtValue.getOrCreateRoot(stack).removeTag(PROBETAG);
    super.removeFromItem(stack, item);
  }

}
