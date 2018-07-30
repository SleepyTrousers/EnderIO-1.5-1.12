package crazypants.enderio.base.loot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.capacitor.CapacitorHelper.SetType;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorKey;
import net.minecraft.util.WeightedRandom;

public class WeightedUpgrade {
  static {
    registerWeightedUpgrade(SetType.TYPE, CapacitorKey.LEGACY_ENERGY_INTAKE, "intake", 20);
    registerWeightedUpgrade(SetType.TYPE, CapacitorKey.LEGACY_ENERGY_BUFFER, "buffer", 20);
  }

  public final @Nonnull SetType setType;
  public final @Nonnull ICapacitorKey capacitorKey;
  public final @Nonnull String langKey;

  private WeightedUpgrade(@Nonnull SetType setType, @Nonnull ICapacitorKey capacitorKey, @Nonnull String langKey) {
    this.setType = setType;
    this.capacitorKey = capacitorKey;
    this.langKey = "loot.capacitor." + langKey;
  }

  public static void registerWeightedUpgrade(@Nonnull SetType setType, @Nonnull ICapacitorKey capacitorKey, @Nonnull String langKey, int weight) {
    registerWeightedUpgrade(new WeightedUpgrade(setType, capacitorKey, langKey), weight);
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
        if (wa.upgrade.capacitorKey.getLegacyName().equals(raw)) {
          return wa.upgrade;
        }
        if (wa.upgrade.capacitorKey.getRegistryName().toString().equals(raw)) {
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