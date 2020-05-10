package crazypants.enderio.power;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;

public enum Capacitors {

  BASIC_CAPACITOR(
	new BasicCapacitor(1, 80, 100000, 20),
      "basicCapacitor",
      "Basic"),

  ACTIVATED_CAPACITOR(
	new BasicCapacitor(2, 240, 200000, 60),
      "activatedCapacitor",
      "Advanced"),

  ENDER_CAPACITOR(
	new BasicCapacitor(3, 400, 500000, 100),
      "enderCapacitor",
      "Ender"),

  CRYSTALLINE_CAPACITOR(
    new BasicCapacitor(4, 840, 1000000, 260),
      "crystallineCapacitor",
      "Crystalline"),

  MELODIC_CAPACITOR(
    new BasicCapacitor(5, 1640, 2000000, 460),
	  "melodicCapacitor",
	  "Melodic"),

  STELLAR_CAPACITOR(
    new BasicCapacitor(6, 3320, 5000000, 980),
      "stellarCapacitor",
      "Stellar"),

  TOTEMIC_CAPACITOR(
    new BasicCapacitor(7, 6600, 10000000, 1900),
      "totemicCapacitor",
      "Totemic"),

  SILVER_CAPACITOR(
	new BasicCapacitor(1, 80, 100000, 20),
      "silverCapacitor",
      "Basic"),

  ENDERGETIC_CAPACITOR(
    new BasicCapacitor(2, 240, 200000, 60),
      "endergeticCapacitor",
      "Advanced"),

  ENDERGISED_CAPACITOR(
    new BasicCapacitor(3, 400, 500000, 100),
      "endergisedCapacitor",
      "Ender");

  public final ICapacitor capacitor;
  public final String unlocalisedName;
  public final String iconKey;
  public final String oreDict;

  private Capacitors(ICapacitor capacitor, String unlocalisedName, String oreDict) {
    this.capacitor = capacitor;
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
    this.oreDict = oreDict;
  }

  public String getOreTag() {
	  return "capacitor"+oreDict;
  }

  public ItemStack getItemStack() {
    return new ItemStack(EnderIO.itemBasicCapacitor, 1, ordinal());
  }

}
