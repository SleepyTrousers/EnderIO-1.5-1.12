package crazypants.enderio.capacitor;

public interface ICapacitorData {

  int getBaseLevel();

  float getUnscaledValue(ICapacitorKey key);

  String getUnlocalizedName();

  String getLocalizedName();

}