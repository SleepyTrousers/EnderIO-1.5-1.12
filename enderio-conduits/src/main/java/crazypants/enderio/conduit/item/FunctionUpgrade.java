package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public enum FunctionUpgrade {

  // TODO Move Inventory Panel

  INVENTORY_PANEL("inventory_panel_upgrade", "item.item_inventory_panel_upgrade", 1);

  public final String baseName;
  public final String iconName;
  public final String unlocName;
  public final int maxStackSize;

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for (FunctionUpgrade c : values()) {
      res.add(new ResourceLocation(c.iconName));
    }
    return res;
  }

  private FunctionUpgrade(String name, String unlocName, int maxStackSize) {
    this.baseName = name;
    this.iconName = "enderio:" + name;
    this.unlocName = unlocName;
    this.maxStackSize = maxStackSize;
  }
}
