package crazypants.enderio.paint;

import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class YetaUtil {

  private static volatile boolean lastCheckResult = false;
  private static boolean toggled = false;

  public static boolean shouldHeldItemHideFacades(EntityPlayer player) {
    ItemStack held = player.getHeldItemMainhand();
    boolean checkResult;
    if (held != null && held.getItem() instanceof IHideFacades) {
      checkResult = ((IHideFacades) held.getItem()).shouldHideFacades(held, player);
    } else {
      checkResult = ToolUtil.isToolEquipped(player, EnumHand.MAIN_HAND);
    }
    return checkResult;
  }
  
  public static void onClientTick() {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player == null) {
      return;
    }
    boolean checkResult = shouldHeldItemHideFacades(player);
    toggled = lastCheckResult != checkResult;
    lastCheckResult = checkResult;
  }

  public static boolean shouldHeldItemHideFacadesClient() {
    return lastCheckResult;
  }

  public static void refresh(TileEntity te) {
    if (toggled && te instanceof IPaintable.IPaintableTileEntity && ((IPaintable.IPaintableTileEntity) te).getPaintSource() != null) {
      BlockPos pos = te.getPos();
      IBlockState bs = te.getWorld().getBlockState(pos);
      te.getWorld().notifyBlockUpdate(pos, bs, bs, 3);
    }
  }

}
