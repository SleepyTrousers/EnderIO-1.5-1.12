package crazypants.enderio.powertools.machine.capbank.render;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.property.IOMode;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.rendermapper.ConnectedBlockRenderMapper;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.powertools.machine.capbank.BlockCapBank;
import crazypants.enderio.powertools.machine.capbank.CapBankType;
import crazypants.enderio.powertools.machine.capbank.InfoDisplayType;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.render.property.EnumMergingBlockRenderMode.RENDER;

public class CapBankBlockRenderMapper extends ConnectedBlockRenderMapper {

  public CapBankBlockRenderMapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    super(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> renderBody(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    TileEntity tileEntity = state.getTileEntity();
    if (tileEntity instanceof TileCapBank && state.getBlock() instanceof BlockCapBank) {
      NNList.FACING.apply(new Callback<EnumFacing>() {
        @Override
        public void apply(@Nonnull EnumFacing face) {
          IoMode ioMode = ((TileCapBank) tileEntity).getIoMode(face);
          InfoDisplayType displayType = ((TileCapBank) tileEntity).getDisplayType(face);
          EnumIOMode iOMode = ((BlockCapBank) state.getBlock()).mapIOMode(displayType, ioMode);
          states.add(ModObject.block_machine_io.getBlockNN().getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode)));
        }
      });
    } else {
      states.add(state.getState().withProperty(RENDER, EnumMergingBlockRenderMode.sides).withProperty(CapBankType.KIND, CapBankType.NONE));
    }
    return states;
  }

  @Override
  protected boolean isSameKind(@Nonnull IBlockState state, @Nonnull IBlockState other) {
    CapBankType myKind = state.getValue(CapBankType.KIND);
    return myKind.isMultiblock() && state.getBlock() == other.getBlock() && myKind == other.getValue(CapBankType.KIND);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getMergedBlockstate(@Nonnull IBlockState state) {
    return state.withProperty(CapBankType.KIND, CapBankType.NONE);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected IBlockState getBorderedBlockstate(@Nonnull IBlockState state) {
    return state;
  }

}
