package crazypants.enderio.base.item.darksteel.upgrade.solar;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SolarUpgradeManager {

  private SolarUpgradeManager() {
  }

  public static final @Nonnull NNList<SolarUpgrade> UPGRADES = new NNList<>();
  private static @Nonnull NNList<IValue<Integer>> RF = new NNList<>();
  private static final @Nonnull String NAME = "enderio.darksteel.upgrade.solar_";

  public static void enableSolarUpgrade(@Nonnull Item item, NNList<IValue<Integer>> levelCostList, NNList<IValue<Integer>> rfList) {
    for (int i = 0; i < rfList.size(); i++) {
      int level = UPGRADES.size() + i + 1;
      SolarUpgrade upgrade = new SolarUpgrade(NAME + level, new ItemStack(item, 1, i), level, levelCostList.get(i));
      UPGRADES.add(upgrade);
      RF.add(rfList.get(i));
      DarkSteelRecipeManager.instance.addUpgrade(upgrade);
    }
  }

  protected static int getRFforLevel(int level) {
    return level < 0 || level >= RF.size() ? 0 : RF.get(level).get();
  }
}
