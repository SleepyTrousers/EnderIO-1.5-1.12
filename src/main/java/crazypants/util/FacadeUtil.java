package crazypants.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Optional.Method;

public class FacadeUtil {

  public static final FacadeUtil instance = new InnerFacadeUtil();

  private FacadeUtil() {
  }

  private static class InnerFacadeUtil extends FacadeUtil {

    @Override
    @Method(modid = "ctm-api")
    public boolean isFacaded(@Nullable IBlockState state) {
      return state != null && state.getBlock() instanceof team.chisel.ctm.api.IFacade;
    }

    @Override
    @Method(modid = "ctm-api")
    public @Nullable IBlockState getFacade(@Nullable IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
      return state != null && isFacaded(state) ? ((team.chisel.ctm.api.IFacade) state.getBlock()).getFacade(world, pos, side) : null;
    }

  }

  public boolean isFacaded(@Nullable IBlockState state) {
    return false;
  }

  public @Nullable IBlockState getFacade(@Nullable IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    return null;
  }

}
