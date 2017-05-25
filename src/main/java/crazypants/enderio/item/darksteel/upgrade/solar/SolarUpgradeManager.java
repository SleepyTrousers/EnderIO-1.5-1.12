package crazypants.enderio.item.darksteel.upgrade.solar;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.handler.darksteel.DarkSteelRecipeManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SolarUpgradeManager {

  private SolarUpgradeManager() {
  }

  public static final @Nonnull NNList<SolarUpgrade> UPGRADES = new NNList<>();
  private static @Nonnull NNList<Integer> RF = new NNList<>();
  private static final @Nonnull String NAME = "enderio.darksteel.upgrade.solar_";

  public static void enableSolarUpgrade(@Nonnull Item item, int[] levelCostList, int[] rfList) {
    for (int i = 0; i < rfList.length; i++) {
      int level = UPGRADES.size() + i + 1;
      SolarUpgrade upgrade = new SolarUpgrade(NAME + level, new ItemStack(item, 1, i), level, levelCostList[i]);
      UPGRADES.add(upgrade);
      RF.add(rfList[i]);
      DarkSteelRecipeManager.instance.addUpgrade(upgrade);
    }
  }

  protected static int getRFforLevel(int level) {
    return level < 0 || level >= RF.size() ? 0 : RF.get(level);
  }
}
