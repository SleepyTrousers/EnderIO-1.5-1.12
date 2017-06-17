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

    private static final FacadeUtil instance1 = new LegacyFacadeUtil();
    private static final FacadeUtil instance2 = new CTMFacadeUtil();

    @Override
    public boolean isFacaded(IBlockState state) {
      return instance1.isFacaded(state) || instance2.isFacaded(state);
    }

    @Override
    public IBlockState getFacade(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
      if (instance1.isFacaded(state)) {
        return instance1.getFacade(state, world, pos, side);
      } else if (instance2.isFacaded(state)) {
        return instance2.getFacade(state, world, pos, side);
      } else {
        return null;
      }
    }

  }

  private static class LegacyFacadeUtil extends FacadeUtil {

    @Override
    @Method(modid = "ChiselAPI")
    public boolean isFacaded(IBlockState state) {
      return state != null && state.getBlock() instanceof team.chisel.api.IFacade;
    }

    @Override
    @Method(modid = "ChiselAPI")
    public IBlockState getFacade(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
      return isFacaded(state) ? ((team.chisel.api.IFacade) state.getBlock()).getFacade(world, pos, side) : null;
    }

  }

  private static class CTMFacadeUtil extends FacadeUtil {

    @Override
    @Method(modid = "ctm-api")
    public boolean isFacaded(IBlockState state) {
      return state != null && state.getBlock() instanceof team.chisel.ctm.api.IFacade;
    }

    @Override
    @Method(modid = "ctm-api")
    public IBlockState getFacade(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
      return isFacaded(state) ? ((team.chisel.ctm.api.IFacade) state.getBlock()).getFacade(world, pos, side) : null;
    }

  }

  public boolean isFacaded(IBlockState state) {
    return false;
  }

  public IBlockState getFacade(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    return null;
  }

}
