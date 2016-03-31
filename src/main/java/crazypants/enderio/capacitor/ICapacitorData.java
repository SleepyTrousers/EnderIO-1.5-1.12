package crazypants.enderio.capacitor;

public interface ICapacitorData {

  float getUnscaledValue(CapacitorKey key);

  String getUnlocalizedName();

  String getLocalizedName();

}