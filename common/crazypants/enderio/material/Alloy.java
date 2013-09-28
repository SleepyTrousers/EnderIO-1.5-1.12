package crazypants.enderio.material;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;

public enum Alloy {

  ELECTRICAL_STEEL("Silicon Steel", "electricalSteel", new ItemStack(Item.ingotIron), new ItemStack(Item.coal, 2, 1), new ItemStack(
      ModObject.itemMaterial.actualId, 1, Material.SILICON.ordinal())),
  ENERGETIC_ALLOY("Energetic Alloy", "energeticAlloy", new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal()), new ItemStack(
      Item.redstone), new ItemStack(Item.glowstone)),
  PHASED_IRON("Phased Iron", "phasedIron", new ItemStack(Item.ingotIron), new ItemStack(Item.glowstone), new ItemStack(Item.enderPearl)),
  PHASED_GOLD("Phased Gold", "phasedGold", new ItemStack(Item.ingotGold), new ItemStack(Item.glowstone), new ItemStack(Item.enderPearl)),
  REDSTONE_ALLOY("Redstone Alloy", "redstoneAlloy", new ItemStack(Item.ingotIron), new ItemStack(Item.redstone, 4, 0));

  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;
  public final ItemStack[] ingrediants;

  private Alloy(String uiName, String iconKey, ItemStack... ingrediants) {
    this.unlocalisedName = name();
    this.uiName = uiName;
    this.iconKey = "enderio:" + iconKey;
    this.ingrediants = ingrediants;
  }

}
