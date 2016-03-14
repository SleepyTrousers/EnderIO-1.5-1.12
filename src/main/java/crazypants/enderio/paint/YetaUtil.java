package crazypants.enderio.paint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.tool.ToolUtil;

public class YetaUtil {

  private static YetaUtil instance = new YetaUtil();

  private long lastCheckTick = -1;
  private boolean lastCheckResult = false;
  private boolean toggled = false;

  private boolean shouldHide(EntityPlayer player) {
    long tickCount = EnderIO.proxy.getTickCount();
    if (tickCount != lastCheckTick) {
      if (player == null) {
        return false;
      }
      ItemStack held = player.getCurrentEquippedItem();
      boolean checkResult;
      if (held != null && held.getItem() instanceof IHideFacades) {
        checkResult = ((IHideFacades) held.getItem()).shouldHideFacades(held, player);
      } else {
        checkResult = ToolUtil.isToolEquipped(player);
      }
      toggled = (lastCheckResult != checkResult) && (lastCheckTick != -1);
      lastCheckResult = checkResult;
      lastCheckTick = tickCount;
    }
    return lastCheckResult;
  }

  public static boolean shouldHeldItemHideFacades() {
    return instance.shouldHide(EnderIO.proxy.getClientPlayer());
  }

  public static boolean shouldHeldItemHideFacades(EntityPlayer player) {
    return instance.shouldHide(player == null ? EnderIO.proxy.getClientPlayer() : player);
  }

  public static boolean refresh() {
    instance.shouldHide(EnderIO.proxy.getClientPlayer());
    return instance.toggled;
  }

  public static void refresh(TileEntity te) {
    if (te instanceof IPaintable.IPaintableTileEntity) {
      if (((IPaintable.IPaintableTileEntity) te).getPaintSource() != null) {
        instance.shouldHide(EnderIO.proxy.getClientPlayer());
        if (instance.toggled) {
          te.getWorld().markBlockForUpdate(te.getPos());
        }
      }
    }
  }

}
