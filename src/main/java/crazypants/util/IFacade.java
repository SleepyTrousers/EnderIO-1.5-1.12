package crazypants.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(iface = "team.chisel.api.IFacade", modid = "ChiselAPI")
public interface IFacade extends team.chisel.api.IFacade {

  @Override
  @Nonnull
  IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side);
  
}
