package crazypants.enderio.paint;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.paint.IPaintable.IPaintableTileEntity;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class YetaUtil {

  private static volatile @Nonnull YetaDisplayMode lastCheckResult = new YetaDisplayMode();
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
    toggled = lastCheckResult.isHideFacades() != checkResult;
    lastCheckResult.setHideFacades(checkResult);
    lastCheckResult.setDisplayMode(getDisplayMode(player));
  }

  public static boolean shouldHeldItemHideFacadesClient() {
    return lastCheckResult.isHideFacades();
  }

  public static ConduitDisplayMode getDisplayModeClient() {
    return lastCheckResult.getDisplayMode();
  }

  public static @Nonnull YetaDisplayMode getYetaDisplayMode() {
    return new YetaDisplayMode(lastCheckResult);
  }

  public static void refresh(TileEntity te) {
    if (toggled && te instanceof IPaintable.IPaintableTileEntity && ((IPaintable.IPaintableTileEntity) te).getPaintSource() != null) {
      BlockPos pos = te.getPos();
      IBlockState bs = te.getWorld().getBlockState(pos);
      te.getWorld().notifyBlockUpdate(pos, bs, bs, 3);
    }
  }

  public static ConduitDisplayMode getDisplayMode(EntityPlayer player) {
    player = player == null ? EnderIO.proxy.getClientPlayer() : player;
    if (player == null) {
      return ConduitDisplayMode.ALL;
    }
    ItemStack equipped = player.getHeldItemMainhand();
    if (equipped == null) {
      return ConduitDisplayMode.ALL;
    }
  
    ConduitDisplayMode result = ConduitDisplayMode.getDisplayMode(equipped);
    if (result == null) {
      return ConduitDisplayMode.ALL;
    }
    return result;
  }

  public static boolean isFacadeHidden(IPaintableTileEntity bundle, EntityPlayer player) {
    if (bundle.getPaintSource() == null) {
      return false;
    }
    if (player == null || player.world.isRemote) {
      return shouldHeldItemHideFacadesClient();
    }
    return shouldHeldItemHideFacades(player);
  }

  public static boolean isSolidFacadeRendered(IConduitBundle bundle, EntityPlayer player) {
    return bundle.hasFacade() && !isFacadeHidden(bundle, player);
  }

  public static boolean renderConduit(EntityPlayer player, Class<? extends IConduit> conduitType) {
    if (player == null || player.world.isRemote) {
      return lastCheckResult.renderConduit(conduitType);
    }
    return getDisplayMode(player).renderConduit(conduitType);
  }

  public static boolean renderConduit(EntityPlayer player, IConduit con) {
    return renderConduit(player, con.getBaseConduitType());
  }

  public static class YetaDisplayMode {
    private boolean hideFacades = false;
    private ConduitDisplayMode displayMode = ConduitDisplayMode.ALL;

    private YetaDisplayMode() {
    }

    private YetaDisplayMode(YetaDisplayMode global) {
      hideFacades = global.hideFacades;
      displayMode = global.displayMode;
    }

    public boolean isHideFacades() {
      return hideFacades;
    }

    public ConduitDisplayMode getDisplayMode() {
      return displayMode;
    }

    void setHideFacades(boolean hideFacades) {
      this.hideFacades = hideFacades;
    }

    void setDisplayMode(ConduitDisplayMode displayMode) {
      this.displayMode = displayMode;
    }

    public boolean renderConduit(Class<? extends IConduit> conduitType) {
      return displayMode.renderConduit(conduitType);
    }

    public boolean renderConduit(IConduit con) {
      return renderConduit(con.getBaseConduitType());
    }

  }

}
