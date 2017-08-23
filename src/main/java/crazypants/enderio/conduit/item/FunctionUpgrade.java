package crazypants.enderio.conduit.item;

public enum FunctionUpgrade {

  INVENTORY_PANEL("enderio:inventoryPanelUpgrade", "item.itemInventoryPanelUpgrade", 1);

  public final String iconName;
  public final String unlocName;
  public final int maxStackSize;

  private FunctionUpgrade(String iconName, String unlocName, int maxStackSize) {
    this.iconName = iconName;
    this.unlocName = unlocName;
    this.maxStackSize = maxStackSize;
  }
}
