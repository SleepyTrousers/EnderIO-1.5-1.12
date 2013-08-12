package crazypants.enderio.material;

import net.minecraft.block.Block;
import net.minecraft.item.*;

//public static final String[] alloyNames = new String[] {"Activated Iron", "Activated Gold", "Ender Iron", "Ender Gold"};

public enum Alloy {
    
  ACTIVATED_IRON("Activated Iron", "activatedIron", new ItemStack(Item.ingotIron), new ItemStack(Item.redstone), new ItemStack(Item.glowstone)),
  ACTIVATED_GOLD("Activated Gold", "activatedGold", new ItemStack(Item.ingotGold), new ItemStack(Item.redstone), new ItemStack(Item.glowstone)),
  ENDER_IRON("Ender Iron", "enderIron", new ItemStack(Item.ingotIron), new ItemStack(Item.enderPearl), new ItemStack(Item.glowstone)),
  ENDER_GOLD("Ender Gold", "enderGold", new ItemStack(Item.ingotGold), new ItemStack(Item.enderPearl), new ItemStack(Item.glowstone)),
  BLUE_STEEL("Blue Steel", "blueSteel", new ItemStack(Item.ingotIron), new ItemStack(Item.dyePowder,1,4), new ItemStack(Block.obsidian));  
  
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
