package crazypants.enderio.machine.capbank.render;

import static crazypants.enderio.machine.capbank.render.EnumCapbankRenderMode.RENDER;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.dummy.BlockMachineIO;

public class CapBankRenderMapper implements IRenderMapper {

  public CapBankRenderMapper() {
  }

  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return new CapBankStateWrapper(state, world, pos);
  }

  private static class CapBankStateWrapper extends BlockStateWrapper {

    private final List<IBlockState> states = new ArrayList<IBlockState>();
    private final CapBankType myKind;
    private final Block myBlock;

    public CapBankStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
      super(state, world, pos);
      myKind = state.getValue(CapBankType.KIND);
      myBlock = getBlock();

      TileEntity tileEntity = getTileEntity();
      if (tileEntity instanceof TileCapBank && myBlock instanceof BlockCapBank) {
        for (EnumFacing face : EnumFacing.values()) {
          IoMode ioMode = ((TileCapBank) tileEntity).getIoMode(face);
          InfoDisplayType displayType = ((TileCapBank) tileEntity).getDisplayType(face);
          EnumIOMode iOMode = ((BlockCapBank) myBlock).mapIOMode(displayType, ioMode);
          states.add(BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode)));
        }
      } else {
        states.add(state.withProperty(RENDER, EnumCapbankRenderMode.sides).withProperty(CapBankType.KIND, CapBankType.NONE));
      }

      IBlockState stateMerged = state.withProperty(CapBankType.KIND, CapBankType.NONE);

      // For each of the 4 sides we add the top, bottom and right edge as well as the two corner between those. That are all
      // edges and corners---a cube has 6 sides, 2*4 corners and 3*4 edges.

      boolean block_up = isSameKind(world.getBlockState(pos.offset(EnumFacing.UP)));
      boolean block_down = isSameKind(world.getBlockState(pos.offset(EnumFacing.DOWN)));
      for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
        boolean block_facing = isSameKind(world.getBlockState(pos.offset(facing)));
        boolean block_right = isSameKind(world.getBlockState(pos.offset(facing.rotateYCCW())));
        boolean block_up_facing = isSameKind(world.getBlockState(pos.up().offset(facing)));
        boolean block_up_right = isSameKind(world.getBlockState(pos.up().offset(facing.rotateYCCW())));
        boolean block_right_facing = isSameKind(world.getBlockState(pos.offset(facing).offset(facing.rotateYCCW())));
        boolean block_down_facing = isSameKind(world.getBlockState(pos.down().offset(facing)));
        boolean block_down_right = isSameKind(world.getBlockState(pos.down().offset(facing.rotateYCCW())));
        boolean block_up_right_facing = isSameKind(world.getBlockState(pos.up().offset(facing.rotateYCCW()).offset(facing)));
        boolean block_down_right_facing = isSameKind(world.getBlockState(pos.down().offset(facing.rotateYCCW()).offset(facing)));

        // Our Edges //
        // ///////// //

        boolean upper_edge = hasEdge(block_up, block_facing);
        boolean lower_edge = hasEdge(block_down, block_facing);
        boolean right_edge = hasEdge(block_right, block_facing);

        states.add((upper_edge ? state : stateMerged).withProperty(RENDER, EnumCapbankRenderMode.get(facing, EnumFacing.UP)));
        states.add((lower_edge ? state : stateMerged).withProperty(RENDER, EnumCapbankRenderMode.get(facing, EnumFacing.DOWN)));
        states.add((right_edge ? state : stateMerged).withProperty(RENDER, EnumCapbankRenderMode.get(facing, facing.rotateYCCW())));

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

        states.add((upper_right_corner ? state : stateMerged).withProperty(RENDER, EnumCapbankRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.UP)));
        states.add((lower_right_corner ? state : stateMerged).withProperty(RENDER, EnumCapbankRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN)));
      }
    }

    private boolean isSameKind(IBlockState other) {
      return myKind.isMultiblock() && myBlock == other.getBlock() && myKind == other.getValue(CapBankType.KIND);
    }

    /**
     * There's only an edge if the block that own the edge is of the right kind and neither of the blocks that connect to the edge are.
     */
    private boolean hasEdge(boolean base, boolean dir1, boolean dir2) {
      return base && !dir1 && !dir2;
    }

    private boolean hasEdge(boolean dir1, boolean dir2) {
      return !dir1 && !dir2;
    }

    public List<IBlockState> getStates() {
      return states;
    }
    
  }

  @Override
  public List<IBlockState> mapBlockRender(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state instanceof CapBankStateWrapper) {
      return ((CapBankStateWrapper) state).getStates();
    }
    return null;
  }

  @Override
  public List<IBlockState> mapBlockRender(Block block, ItemStack stack) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    IBlockState defaultState = block.getDefaultState();
    states.add(defaultState.withProperty(RENDER, EnumCapbankRenderMode.sides).withProperty(CapBankType.KIND, CapBankType.NONE));
    CapBankType bankType = CapBankType.getTypeFromMeta(stack.getItemDamage());
    defaultState = defaultState.withProperty(CapBankType.KIND, bankType);
    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
      states.add(defaultState.withProperty(RENDER, EnumCapbankRenderMode.get(facing, EnumFacing.UP)));
      states.add(defaultState.withProperty(RENDER, EnumCapbankRenderMode.get(facing, EnumFacing.DOWN)));
      states.add(defaultState.withProperty(RENDER, EnumCapbankRenderMode.get(facing, facing.rotateYCCW())));
      states.add(defaultState.withProperty(RENDER, EnumCapbankRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.UP)));
      states.add(defaultState.withProperty(RENDER, EnumCapbankRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN)));
    }
    return states;
  }

}
