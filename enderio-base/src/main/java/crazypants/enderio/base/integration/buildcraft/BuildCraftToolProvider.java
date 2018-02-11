package crazypants.enderio.base.integration.buildcraft;

import javax.annotation.Nonnull;

import buildcraft.api.tools.IToolWrench;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.base.tool.IToolProvider;
import crazypants.enderio.base.tool.ToolUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class BuildCraftToolProvider implements IToolProvider {

  private BCWrench wrench = new BCWrench();

  public BuildCraftToolProvider() throws Exception {
    // Do a check for so we throw an exception in the constructor if we don't have the
    // wrench class
    Class.forName("buildcraft.api.tools.IToolWrench");
    ToolUtil.getInstance().registerToolProvider(this);
  }

  @Override
  public ITool getTool(@Nonnull ItemStack stack) {
    if (MpsUtil.instance.isPowerFistEquiped(stack) && !MpsUtil.instance.isOmniToolActive(stack)) {
      return null;
    }
    if (stack.getItem() instanceof IToolWrench) {
      return wrench;
    }
    return null;
  }

  public static class BCWrench implements ITool {
    
    // TODO find a way to not provide dummy raytrace data here?

    @Override
    public boolean canUse(@Nonnull EnumHand hand, @Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
      ItemStack stack = player.getHeldItem(hand);
      return ((IToolWrench) stack.getItem()).canWrench(player, hand, stack, new RayTraceResult(new Vec3d(pos), EnumFacing.UP, pos));
    }

    @Override
    public void used(@Nonnull EnumHand hand, @Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
      ItemStack stack = player.getHeldItem(hand);
      ((IToolWrench) stack.getItem()).wrenchUsed(player, hand, stack, new RayTraceResult(new Vec3d(pos), EnumFacing.UP, pos));
    }

    @Override
    public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
      return true;
    }
  }

}
