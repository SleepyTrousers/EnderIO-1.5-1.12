package crazypants.enderio.base.loot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorHelper.SetType;
import net.minecraft.util.WeightedRandom;

public class WeightedUpgrade {
  static {
    registerWeightedUpgrade(new WeightedUpgrade(SetType.TYPE, CapacitorKey.LEGACY_ENERGY_INTAKE, "intake"), 20);
    registerWeightedUpgrade(new WeightedUpgrade(SetType.TYPE, CapacitorKey.LEGACY_ENERGY_BUFFER, "buffer"), 20);
    // TODO 1.11 put into machine sub-mod
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.ALLOY_SMELTER_POWER_USE, "smelting", 10);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.CRAFTER_TICKS, "crafting", 10);
    // new WeightedUpgrade(SetType.TYPE, CapacitorKey.ATTRACTOR_RANGE, "area", 5);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.FARM_BONUS_SIZE, "green", 10);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.STIRLING_POWER_GEN, "red", 10);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.SPAWNER_SPEEDUP, "mobby", 5);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.SAG_MILL_POWER_USE, "crushed", 15);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.SLICE_POWER_USE, "cleancut", 5);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.SOUL_BINDER_POWER_USE, "tight", 5);
    // new WeightedUpgrade(SetType.NAME, CapacitorKey.PAINTER_POWER_USE, "aa", 10);

  }

  public final @Nonnull SetType setType;
  public final @Nonnull CapacitorKey capacitorKey;
  public final @Nonnull String langKey;

  private WeightedUpgrade(@Nonnull SetType setType, @Nonnull CapacitorKey capacitorKey, @Nonnull String langKey) {
    this.setType = setType;
    this.capacitorKey = capacitorKey;
    this.langKey = "loot.capacitor." + langKey;
  }

  public static void registerWeightedUpgrade(@Nonnull WeightedUpgrade upgrade, int weight) {
    WeightedUpgradeImpl.weightedUpgrades.add(new WeightedUpgradeImpl(weight, upgrade));
  }

  public static @Nonnull NNList<WeightedUpgrade.WeightedUpgradeImpl> getWeightedupgrades() {
    return WeightedUpgradeImpl.weightedUpgrades;
  }

  public static class WeightedUpgradeImpl extends WeightedRandom.Item {
    private static final @Nonnull NNList<WeightedUpgrade.WeightedUpgradeImpl> weightedUpgrades = new NNList<WeightedUpgrade.WeightedUpgradeImpl>();
    private final @Nonnull WeightedUpgrade upgrade;

    private WeightedUpgradeImpl(int weight, @Nonnull WeightedUpgrade upgrade) {
      super(weight);
      this.upgrade = upgrade;
    }

    public @Nonnull WeightedUpgrade getUpgrade() {
      return upgrade;
    }

  }

  public static @Nullable WeightedUpgrade getByRawString(String raw) {
    for (WeightedUpgradeImpl wa : WeightedUpgradeImpl.weightedUpgrades) {
      switch (wa.upgrade.setType) {
      case NAME:
        if (wa.upgrade.capacitorKey.getName().equals(raw)) {
          return wa.upgrade;
        }
        break;
      case TYPE:
        if (wa.upgrade.capacitorKey.getValueType().getName().equals(raw)) {
          return wa.upgrade;
        }
        break;
      default:
        break;
      }
    }
    return null;
  }

}