package crazypants.enderio.paint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.tool.ToolUtil;

public class YetaUtil {

  private static volatile boolean lastCheckResult = false;
  private static boolean toggled = false;

  public static void onTick() {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player == null) {
      return;
    }
    ItemStack held = player.getCurrentEquippedItem();
    boolean checkResult;
    if (held != null && held.getItem() instanceof IHideFacades) {
      checkResult = ((IHideFacades) held.getItem()).shouldHideFacades(held, player);
    } else {
      checkResult = ToolUtil.isToolEquipped(player);
    }
    toggled = lastCheckResult != checkResult;
    lastCheckResult = checkResult;
  }

  public static boolean shouldHeldItemHideFacades() {
    return lastCheckResult;
  }

  public static boolean refresh() {
    return toggled;
  }

  public static void refresh(TileEntity te) {
    if (toggled && te instanceof IPaintable.IPaintableTileEntity && ((IPaintable.IPaintableTileEntity) te).getPaintSource() != null) {
      te.getWorld().markBlockForUpdate(te.getPos());
    }
  }

}
