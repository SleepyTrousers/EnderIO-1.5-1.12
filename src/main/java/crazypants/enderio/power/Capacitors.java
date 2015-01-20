package crazypants.enderio.power;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;

public enum Capacitors {

  BASIC_CAPACITOR(
      new BasicCapacitor(80, 100000, 20),
      "basicCapacitor"),

  ACTIVATED_CAPACITOR(
      new BasicCapacitor(240, 200000, 60),
      "activatedCapacitor"),

  ENDER_CAPACITOR(
      new BasicCapacitor(400, 500000, 100),
      "enderCapacitor");

  public final ICapacitor capacitor;
  public final String unlocalisedName;
  public final String iconKey;

  private Capacitors(ICapacitor capacitor, String unlocalisedName) {
    this.capacitor = capacitor;
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
  }

  public ItemStack getItemStack() {
    return new ItemStack(EnderIO.itemBasicCapacitor, 1, ordinal());
  }

}
