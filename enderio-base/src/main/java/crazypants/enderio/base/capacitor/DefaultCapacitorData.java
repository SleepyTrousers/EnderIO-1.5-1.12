package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;

public enum DefaultCapacitorData implements ICapacitorData {

  BASIC_CAPACITOR("basic", 1),
  ACTIVATED_CAPACITOR("activated", 2),
  ENDER_CAPACITOR("ender", 3),
  SPECIAL_CAPACITOR("special"),
  SPECIAL2_CAPACITOR("special2");

  public static final @Nonnull ICapacitorData NONE = new ICapacitorData() {

    @Override
    public float getUnscaledValue(@Nonnull ICapacitorKey key) {
      return 0;
    }

    @Override
    @Nonnull
    public String getUnlocalizedName() {
      return "none";
    }

    @Override
    @Nonnull
    public String getLocalizedName() {
      return "none";
    }

    @Override
    public int hashCode() {
      return 42;
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj;
    }

  };

  private final @Nonnull String unlocalizedName;
  private final int baselevel;
  private final boolean regular;

  private DefaultCapacitorData(@Nonnull String unlocalizedName) {
    this(unlocalizedName, 1, false);
  }

  private DefaultCapacitorData(@Nonnull String unlocalizedName, int baselevel) {
    this(unlocalizedName, baselevel, true);
  }

  private DefaultCapacitorData(@Nonnull String unlocalizedName, int baselevel, boolean regular) {
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