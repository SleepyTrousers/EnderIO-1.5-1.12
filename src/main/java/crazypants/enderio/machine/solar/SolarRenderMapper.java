package crazypants.enderio.machine.solar;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.MergingBlockStateWrapper;

import static crazypants.enderio.render.EnumMergingBlockRenderMode.RENDER;

public class SolarRenderMapper implements IRenderMapper {

  public SolarRenderMapper() {
  }

  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return new SolarStateWrapper(state, world, pos);
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    if (state instanceof MergingBlockStateWrapper) {
      return Pair.of(((MergingBlockStateWrapper) state).getStates(), null);
    }
    return null;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(Block block, ItemStack stack) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    IBlockState defaultState = block.getDefaultState();
    SolarType bankType = SolarType.getTypeFromMeta(stack.getItemDamage());
    defaultState = defaultState.withProperty(SolarType.KIND, bankType);

    states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.sides));

    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
      states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.DOWN)));
      states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN)));
    }
    return Pair.of(states, null);
  }

  @Override
  public List<IBlockState> mapOverlayLayer(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    return null;
  }

}
