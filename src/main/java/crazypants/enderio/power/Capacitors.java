package crazypants.enderio.power;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;

public enum Capacitors {

  BASIC_CAPACITOR(
      new BasicCapacitor(20, 10000, 2),
      "basicCapacitor"),

  ACTIVATED_CAPACITOR(
      new BasicCapacitor(40, 20000, 6),
      "activatedCapacitor"),

  ENDER_CAPACITOR(
      new BasicCapacitor(100, 100000, 10),
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
    return new ItemStack(ModObject.itemBasicCapacitor.actualId, 1, ordinal());
  }

}
