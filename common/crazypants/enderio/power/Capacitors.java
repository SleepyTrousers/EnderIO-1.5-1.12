package crazypants.enderio.power;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;

public enum Capacitors {

  BASIC_CAPACITOR(
      new BasicCapacitor(2, 10000),
      "Capacitor", "basicCapacitor"),

  ACTIVATED_CAPACITOR(
      new BasicCapacitor(4, 20000),
      "Activated Capacitor", "activatedCapacitor"),

  ENDER_CAPACITOR(
      new BasicCapacitor(10, 100000),
      "Ender Capacitor", "enderCapacitor");

  public final ICapacitor capacitor;
  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;

  private Capacitors(ICapacitor capacitor, String uiName, String iconKey) {
    this.capacitor = capacitor;
    this.uiName = uiName;
    this.iconKey = "enderio:" + iconKey;
    this.unlocalisedName = name();
  }

  public ItemStack getItemStack() {
    return new ItemStack(ModObject.itemBasicCapacitor.actualId, 1, ordinal());
  }

}
