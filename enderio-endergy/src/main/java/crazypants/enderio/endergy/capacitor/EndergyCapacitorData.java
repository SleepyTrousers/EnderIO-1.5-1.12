package crazypants.enderio.endergy.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.EnderIO;

public enum EndergyCapacitorData implements ICapacitorData {

  GRAINY_CAPACITOR("grains", 1.0f),
  CRYSTALLINE_CAPACITOR("crystalline", 3.5f),
  MELODIC_CAPACITOR("melodic", 4.0f),
  STELLAR_CAPACITOR("stellar", 5.0f),
  TOTEMIC_CAPACITOR("totemic", 3.5f),

  // Modded ingots progression using silver and tin instead of gold and copper
  SILVER_CAPACITOR("silver", 1.0f),
  ENERGETIC_SILVER_CAPACITOR("energetic_silver", 2.0f),
  VIVID_CAPACITOR("vivid", 3.0f),

  ;

  private final @Nonnull String unlocalizedName;
  private final float baselevel;
  private final boolean regular;

  private EndergyCapacitorData(@Nonnull String unlocalizedName) {
    this(unlocalizedName, 1, false);
  }

  private EndergyCapacitorData(@Nonnull String unlocalizedName, float baselevel) {
    this(unlocalizedName, baselevel, true);
  }

  private EndergyCapacitorData(@Nonnull String unlocalizedName, float baselevel, boolean regular) {
    this.unlocalizedName = unlocalizedName;
    this.baselevel = baselevel;
    this.regular = regular;
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return unlocalizedName;
  }

  @Override
  public @Nonnull String getLocalizedName() {
    return EnderIO.lang.localize(getUnlocalizedName() + ".name");
  }

  @Override
  public float getUnscaledValue(@Nonnull ICapacitorKey key) {
    return baselevel;
  }

  public boolean isRegular() {
    return regular;
  }

}
