package crazypants.enderio.capacitor;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.EnderIO;
import net.minecraft.util.ResourceLocation;

public enum DefaultCapacitorData implements ICapacitorData {

  BASIC_CAPACITOR("basicCapacitor", 1),
  ACTIVATED_CAPACITOR("activatedCapacitor", 2),
  ENDER_CAPACITOR("enderCapacitor", 3),
  SPECIAL_CAPACITOR("specialCapacitor", 1);

  public static final @Nonnull ICapacitorData NONE = new ICapacitorData() {

    @Override
    public int getBaseLevel() {
      return 0;
    }

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

  };

  private final @Nonnull String unlocalizedName;
  private final int baselevel;

  private DefaultCapacitorData(@Nonnull String unlocalizedName, int baselevel) {
    this.unlocalizedName = unlocalizedName;
    this.baselevel = baselevel;
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return EnderIO.DOMAIN + "." + unlocalizedName;
  }

  @Override
  public @Nonnull String getLocalizedName() {
    return EnderIO.lang.localize(unlocalizedName + ".name");
  }

  @Override
  public float getUnscaledValue(@Nonnull ICapacitorKey key) {
    return baselevel;
  }

  public static NNList<ResourceLocation> getResourceLocations() {
    NNList<ResourceLocation> result = new NNList<ResourceLocation>();
    for (int i = 0; i < values().length; i++) {
      result.add(new ResourceLocation(EnderIO.DOMAIN, values()[i].unlocalizedName));
    }
    return result;
  }

  @Override
  public int getBaseLevel() {
    return baselevel;
  }

}