package crazypants.enderio.integration.buildcraft;

import javax.annotation.Nonnull;

import buildcraft.api.tools.IToolWrench;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.tool.IToolProvider;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

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

    @Override
    public boolean canUse(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
      return ((IToolWrench) stack.getItem()).canWrench(player, pos);
    }

    @Override
    public void used(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
      ((IToolWrench) stack.getItem()).wrenchUsed(player, pos);
    }

    @Override
    public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
      return true;
    }
  }

}
