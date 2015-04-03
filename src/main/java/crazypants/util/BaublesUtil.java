package crazypants.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;

public class BaublesUtil {

  private static final BaublesUtil instance = new BaublesUtil();
  private static final boolean baublesLoaded;
  static {
    baublesLoaded = Loader.isModLoaded("Baubles");
  }

  private BaublesUtil() {
  }

  public static BaublesUtil instance() {
    return instance;
  }

  public boolean hasBaubles() {
    return baublesLoaded;
  }

  public IInventory getBaubles(EntityPlayer player) {
    return hasBaubles() ? getBaublesInvUnsafe(player) : null;
  }

  private IInventory getBaublesInvUnsafe(EntityPlayer player) {
    return BaublesApi.getBaubles(player);
  }
}
