package crazypants.enderio.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import cpw.mods.fml.common.Optional.Method;

public class BaublesTool {

  private static final BaublesTool instance = new withBaubles();
  
  public static BaublesTool getInstance() {
    return instance;
  }
  
  public boolean hasBaubles() {
    return false;
  } 
  
  public IInventory getBaubles(EntityPlayer player) {
    return null;
  }
  
  private static class withBaubles extends BaublesTool {
    @Method(modid = "Baubles")
    public boolean hasBaubles() {
      return true;
    } 
    @Method(modid = "Baubles")
    public IInventory getBaubles(EntityPlayer player) {
      return baubles.api.BaublesApi.getBaubles(player);
    }
  }
  
}
