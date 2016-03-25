package crazypants.enderio.machine.reservoir;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IRenderMapper;

import static crazypants.enderio.render.EnumMergingBlockRenderMode.RENDER;

public class ReservoirItemRenderMapper implements IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final ReservoirItemRenderMapper instance = new ReservoirItemRenderMapper();

  protected ReservoirItemRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    IBlockState defaultState = block.getDefaultState();

    states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.sides), (ItemStack) null));

    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.UP)), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.DOWN)), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW())), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.UP)), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN)), (ItemStack) null));
    }
    return states;
  }

}
