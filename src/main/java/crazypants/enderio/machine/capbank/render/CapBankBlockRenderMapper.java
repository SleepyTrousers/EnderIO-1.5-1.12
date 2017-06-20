package crazypants.enderio.machine.capbank.render;

import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.dummy.BlockMachineIO;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.property.IOMode;
import crazypants.enderio.render.property.IOMode.EnumIOMode;
import crazypants.enderio.render.rendermapper.ConnectedBlockRenderMapper;
import crazypants.enderio.render.util.QuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static crazypants.enderio.render.property.EnumMergingBlockRenderMode.RENDER;

public class CapBankBlockRenderMapper extends ConnectedBlockRenderMapper {

  public CapBankBlockRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> renderBody(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, QuadCollector quadCollector) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    TileEntity tileEntity = state.getTileEntity();
    if (tileEntity instanceof TileCapBank && state.getBlock() instanceof BlockCapBank) {
      for (EnumFacing face : EnumFacing.values()) {
        IoMode ioMode = ((TileCapBank) tileEntity).getIoMode(face);
        InfoDisplayType displayType = ((TileCapBank) tileEntity).getDisplayType(face);
        EnumIOMode iOMode = ((BlockCapBank) state.getBlock()).mapIOMode(displayType, ioMode);
        states.add(BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode)));
      }
    } else {
      states.add(state.getState().withProperty(RENDER, EnumMergingBlockRenderMode.sides).withProperty(CapBankType.KIND, CapBankType.NONE));
    }
    return states;
  }

  @Override
  protected boolean isSameKind(IBlockState state, IBlockState other) {
    CapBankType myKind = state.getValue(CapBankType.KIND);
    return myKind.isMultiblock() && state.getBlock() == other.getBlock() && myKind == other.getValue(CapBankType.KIND);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getMergedBlockstate(IBlockState state) {
    return state.withProperty(CapBankType.KIND, CapBankType.NONE);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getBorderedBlockstate(IBlockState state) {
    return state;
  }

}
