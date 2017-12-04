package crazypants.enderio.base.gui.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEioGuiHandler {

  @Nullable
  Object getGuiElement(boolean server, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1,
      int param2, int param3);

  public interface WithPos extends IEioGuiHandler {
    @Override
    default @Nullable Object getGuiElement(boolean server, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos,
        @Nullable EnumFacing facing, int param1, int param2, int param3) {
      if (world.isBlockLoaded(pos)) {
        if (server) {
          return getServerGuiElement(player, world, pos, facing, param1);
        } else {
          return getClientGuiElement(player, world, pos, facing, param1);
        }
      } else {
        return null;
      }
    }

    @Nullable
    Object getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1);

    @Nullable
    Object getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1);
  }
}