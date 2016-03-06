package crazypants.enderio.machine.capbank.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ITransformation;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.MergingBlockStateWrapper;

import static crazypants.enderio.render.EnumMergingBlockRenderMode.RENDER;

public class CapBankRenderMapper implements IRenderMapper {

  public CapBankRenderMapper() {
  }

  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return new CapBankStateWrapper(state, world, pos);
  }

  @Override
  public Pair<List<IBlockState>, List<Pair<IBakedModel, ITransformation>>> mapBlockRender(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state instanceof MergingBlockStateWrapper) {
      return Pair.of(((MergingBlockStateWrapper) state).getStates(), null);
    }
    return null;
  }

  @Override
  public Pair<List<IBlockState>, List<Pair<IBakedModel, ITransformation>>> mapBlockRender(Block block, ItemStack stack) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    IBlockState defaultState = block.getDefaultState();
    states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.sides).withProperty(CapBankType.KIND, CapBankType.NONE));
    CapBankType bankType = CapBankType.getTypeFromMeta(stack.getItemDamage());
    defaultState = defaultState.withProperty(CapBankType.KIND, bankType);
    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
      states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.UP)));
      states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.DOWN)));
      states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW())));
      states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.UP)));
      states.add(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN)));
    }
    return Pair.of(states, null);
  }

}
