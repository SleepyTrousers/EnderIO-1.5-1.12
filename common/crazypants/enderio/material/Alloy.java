package crazypants.enderio.material;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;

public enum Alloy {

  ELECTRICAL_STEEL("Electrical Steel", "electricalSteel",
      new ItemStack(ModObject.itemMaterial.actualId, 1, Material.SILICON.ordinal()),
      new ItemStack(Item.ingotIron),
      new ItemStack(Item.coal)),

  ENERGETIC_ALLOY("Energetic Alloy", "energeticAlloy",
      new ItemStack(Item.redstone),
      new ItemStack(Item.ingotGold),
      new ItemStack(Item.glowstone)),

  PHASED_GOLD("Phased Alloy", "phasedGold",
      new ItemStack(ModObject.itemAlloy.actualId, 1, ENERGETIC_ALLOY.ordinal()),
      new ItemStack(Item.enderPearl)),

  REDSTONE_ALLOY("Redstone Alloy", "redstoneAlloy",
      new ItemStack(Item.redstone),
      new ItemStack(ModObject.itemMaterial.actualId, 1, Material.SILICON.ordinal())),

  CONDUCTIVE_IRON("Conductive Iron", "conductiveIron",
      new ItemStack(Item.redstone, 2),
      new ItemStack(Item.ingotIron)),

  PHASED_IRON("Phased Iron", "phasedIron",
      new ItemStack(Item.ingotIron),
      new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_ENDER.ordinal()));

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
