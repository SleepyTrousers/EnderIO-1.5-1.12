package crazypants.enderio.power;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(Capacitors c : values()) {
      res.add(new ResourceLocation(c.iconKey));
    }
    return res;
  }
  
  public final ICapacitor capacitor;
  public final String baseName;
  public final String unlocalisedName;
  public final String iconKey;

  private Capacitors(ICapacitor capacitor, String baseName) {
    this.capacitor = capacitor;
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
  }

  public ItemStack getItemStack() {
    return new ItemStack(EnderIO.itemBasicCapacitor, 1, ordinal());
  }

}
