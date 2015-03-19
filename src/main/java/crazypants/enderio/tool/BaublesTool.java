package crazypants.enderio.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class BaublesTool {

  private static final BaublesTool instance = new BaublesToolwithBaubles();
  
  public static BaublesTool getInstance() {
    return instance;
  }
  
  public boolean hasBaubles() {
    return false;
  } 
  
  public IInventory getBaubles(EntityPlayer player) {
    return null;
  }
  
}
