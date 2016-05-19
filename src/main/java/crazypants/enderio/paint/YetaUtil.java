package crazypants.enderio.paint;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
    ItemStack held = player.getHeldItemMainhand();
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
      BlockPos pos = te.getPos();
      IBlockState bs = te.getWorld().getBlockState(pos);
      te.getWorld().notifyBlockUpdate(pos, bs, bs, 3);
    }
  }

}
