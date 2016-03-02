package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import static crazypants.enderio.render.EnumMergingBlockRenderMode.RENDER;

public abstract class MergingBlockStateWrapper extends BlockStateWrapper {

  protected final List<IBlockState> states = new ArrayList<IBlockState>();

  protected abstract boolean isSameKind(IBlockState other);

  protected abstract void renderBody();

  protected abstract IBlockState getMergedBlockstate();

  protected abstract IBlockState getBorderedBlockstate();

  public MergingBlockStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    super(state, world, pos);

    render(pos);
  }

  protected void render(BlockPos pos) {
    renderBody();
  
    IBlockState stateMerged = getMergedBlockstate();
    IBlockState stateBordered = getBorderedBlockstate();
  
    // For each of the 4 sides we add the top, bottom and right edge as well as the two corner between those. That are all
    // edges and corners---a cube has 6 sides, 2*4 corners and 3*4 edges.
  
    boolean block_up = isSameKind(pos.offset(EnumFacing.UP));
    boolean block_down = isSameKind(pos.offset(EnumFacing.DOWN));
    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
      boolean block_facing = isSameKind(pos.offset(facing));
      boolean block_right = isSameKind(pos.offset(facing.rotateYCCW()));
      boolean block_up_facing = isSameKind(pos.up().offset(facing));
      boolean block_up_right = isSameKind(pos.up().offset(facing.rotateYCCW()));
      boolean block_right_facing = isSameKind(pos.offset(facing).offset(facing.rotateYCCW()));
      boolean block_down_facing = isSameKind(pos.down().offset(facing));
      boolean block_down_right = isSameKind(pos.down().offset(facing.rotateYCCW()));
      boolean block_up_right_facing = isSameKind(pos.up().offset(facing.rotateYCCW()).offset(facing));
      boolean block_down_right_facing = isSameKind(pos.down().offset(facing.rotateYCCW()).offset(facing));
  
      // Our Edges //
      // ///////// //
  
      boolean upper_edge = hasEdge(block_up, block_facing);
      boolean lower_edge = hasEdge(block_down, block_facing);
      boolean right_edge = hasEdge(block_right, block_facing);
  
      add(upper_edge, stateBordered, stateMerged, RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.UP));
      add(lower_edge, stateBordered, stateMerged, RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.DOWN));
      add(right_edge, stateBordered, stateMerged, RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW()));
  
      // Our Corners //
      // /////////// //
  
      // The 3 edges that connect to the corners and belong to us. Two of them are checked above, this is the third one:
  
      boolean upper_edge_around_right_corner = hasEdge(block_up, block_right);
      boolean lower_edge_around_right_corner = hasEdge(block_down, block_right);
  
      // The 3 edges that connect to the corners but belong to our neighbors:
  
      boolean right_edge_above = hasEdge(block_up, block_up_right, block_up_facing);
      boolean right_edge_below = hasEdge(block_down, block_down_right, block_down_facing);
  
      boolean top_facing_edge_right = hasEdge(block_right, block_up_right, block_right_facing);
      boolean bottom_facing_edge_right = hasEdge(block_right, block_down_right, block_right_facing);
  
      boolean facing_top_edge_right = hasEdge(block_facing, block_right_facing, block_up_facing);
      boolean facing_bottom_edge_right = hasEdge(block_facing, block_right_facing, block_down_facing);
  
      // The pathological case of having blocks over edge connecting to a common base. This adds the corner where the 2 edges meet:
  
      boolean checker_top_a = hasEdge(block_up_facing, block_facing, block_up_right_facing) //
          && hasEdge(block_right_facing, block_facing, block_up_right_facing);
      boolean checker_top_b = hasEdge(block_up_right, block_right, block_up_right_facing) //
          && hasEdge(block_right_facing, block_right, block_up_right_facing);
      boolean checker_top_c = hasEdge(block_up_facing, block_up, block_up_right_facing) //
          && hasEdge(block_up_right, block_up, block_up_right_facing);
      boolean checker_bottom_a = hasEdge(block_down_facing, block_facing, block_down_right_facing) //
          && hasEdge(block_right_facing, block_facing, block_down_right_facing);
      boolean checker_bottom_b = hasEdge(block_down_right, block_right, block_down_right_facing) //
          && hasEdge(block_right_facing, block_right, block_down_right_facing);
      boolean checker_bottom_c = hasEdge(block_down_facing, block_down, block_down_right_facing) //
          && hasEdge(block_down_right, block_down, block_down_right_facing);
  
      boolean upper_right_corner = upper_edge || right_edge || upper_edge_around_right_corner || right_edge_above || top_facing_edge_right
          || facing_top_edge_right || checker_top_a || checker_top_b || checker_top_c;
      boolean lower_right_corner = lower_edge || right_edge || lower_edge_around_right_corner || right_edge_below || bottom_facing_edge_right
          || facing_bottom_edge_right || checker_bottom_a || checker_bottom_b || checker_bottom_c;
  
      add(upper_right_corner, stateBordered, stateMerged, RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.UP));
      add(lower_right_corner, stateBordered, stateMerged, RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN));
    }
  }

  protected <T extends Comparable<T>, V extends T> void add(boolean border, IBlockState stateBordered, IBlockState stateMerged, IProperty<T> property, V value) {
    IBlockState state = border ? stateBordered : stateMerged;
    if (state != null) {
      states.add(state.withProperty(property, value));
    }
  }

  protected boolean isSameKind(BlockPos other) {
    return isSameKind(getWorld().getBlockState(other));
  }

  /**
   * There's only an edge if the block that own the edge is of the right kind and neither of the blocks that connect to the edge are.
   */
  protected boolean hasEdge(boolean base, boolean dir1, boolean dir2) {
    return base && !dir1 && !dir2;
  }

  protected boolean hasEdge(boolean dir1, boolean dir2) {
    return !dir1 && !dir2;
  }

  public List<IBlockState> getStates() {
    return states;
  }

}