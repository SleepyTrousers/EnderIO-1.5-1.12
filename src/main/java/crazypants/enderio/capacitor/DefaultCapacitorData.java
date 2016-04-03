package crazypants.enderio.capacitor;

import net.minecraft.util.ResourceLocation;
import crazypants.enderio.EnderIO;

public enum DefaultCapacitorData implements ICapacitorData {

  BASIC_CAPACITOR("basicCapacitor", 1),
  ACTIVATED_CAPACITOR("activatedCapacitor", 2),
  ENDER_CAPACITOR("enderCapacitor", 3);

  private final String unlocalizedName;
  private final int baselevel;

  private DefaultCapacitorData(String unlocalizedName, int baselevel) {
    this.unlocalizedName = unlocalizedName;
    this.baselevel = baselevel;
  }

  @Override
  public String getUnlocalizedName() {
    return EnderIO.DOMAIN + "." + unlocalizedName;
  }

  @Override
  public String getLocalizedName() {
    return EnderIO.lang.localize(unlocalizedName + ".name");
  }

  @Override
  public float getUnscaledValue(ICapacitorKey key) {
    return baselevel;
  }

  public static ResourceLocation[] getResourceLocations() {
    ResourceLocation[] result = new ResourceLocation[values().length];
    for (int i = 0; i < values().length; i++) {
      result[i] = new ResourceLocation(EnderIO.DOMAIN, values()[i].unlocalizedName);
    }
    return result;
  }

  @Override
  public int getBaseLevel() {
    return baselevel;
  }

}