package crazypants.util;

import baubles.api.BaublesApi;
import crazypants.enderio.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

public class BaublesUtil {

  public static enum WhoAmI {
    SPCLIENT, MPCLIENT, SPSERVER, MPSERVER, OTHER;
  
    public static BaublesUtil.WhoAmI whoAmI(World world) {
      Side side = FMLCommonHandler.instance().getSide();
      if (side == Side.CLIENT) {
        if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
          if (world.isRemote) {
            return SPCLIENT;
          } else {
            return SPSERVER;
          }
        } else {
          return MPCLIENT;
        }
      } else if (side == Side.SERVER) {
        if (FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) {
          return MPSERVER;
        } else if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
          return SPSERVER;
        }
      }
      return OTHER;
    }
  }

  private static final BaublesUtil instance = new BaublesUtil();
  private static final boolean baublesLoaded;
  static {
    baublesLoaded = Config.enableBaublesIntegration && Loader.isModLoaded("Baubles");
  }

  private BaublesUtil() {
  }

  public static BaublesUtil instance() {
    return instance;
  }

  public boolean hasBaubles() {
    return baublesLoaded;
  }

  /**
   * Do NOT modify this inventory on the client side of a singleplayer game!
   * 
   * Wrap it in a ShadowInventory if you need to.
   */
  public IInventory getBaubles(EntityPlayer player) {
    return hasBaubles() ? getBaublesInvUnsafe(player) : null;
  }

  private IInventory getBaublesInvUnsafe(EntityPlayer player) {
    return BaublesApi.getBaubles(player);
  }

}
