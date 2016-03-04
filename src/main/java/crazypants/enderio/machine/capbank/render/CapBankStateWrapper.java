package crazypants.enderio.machine.capbank.render;

import static crazypants.enderio.render.EnumMergingBlockRenderMode.RENDER;

import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.MergingBlockStateWrapper;
import crazypants.enderio.render.dummy.BlockMachineIO;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class CapBankStateWrapper extends MergingBlockStateWrapper {

  public CapBankStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  protected IBlockState getBorderedBlockstate() {
    return getState();
  }

  @Override
  protected IBlockState getMergedBlockstate() {
    return getState().withProperty(CapBankType.KIND, CapBankType.NONE);
  }

  @Override
  protected void renderBody() {
    TileEntity tileEntity = getTileEntity();
    if (tileEntity instanceof TileCapBank && getBlock() instanceof BlockCapBank) {
      for (EnumFacing face : EnumFacing.values()) {
        IoMode ioMode = ((TileCapBank) tileEntity).getIoMode(face);
        InfoDisplayType displayType = ((TileCapBank) tileEntity).getDisplayType(face);
        EnumIOMode iOMode = ((BlockCapBank) getBlock()).mapIOMode(displayType, ioMode);
        states.add(BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode)));
      }
    } else {
      states.add(getState().withProperty(RENDER, EnumMergingBlockRenderMode.sides).withProperty(CapBankType.KIND, CapBankType.NONE));
    }
  }

  @Override
  protected boolean isSameKind(IBlockState other) {
    CapBankType myKind = getState().getValue(CapBankType.KIND);
    return myKind.isMultiblock() && getBlock() == other.getBlock() && myKind == other.getValue(CapBankType.KIND);
  }
  
}