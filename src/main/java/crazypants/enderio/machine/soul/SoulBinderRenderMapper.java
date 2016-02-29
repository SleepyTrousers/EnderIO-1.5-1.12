package crazypants.enderio.machine.soul;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.EnumRenderMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class SoulBinderRenderMapper extends MachineRenderMapper {

  public SoulBinderRenderMapper() {
    super(null);
  }

  @Override
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, TileEntity tileEntity, Block block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    EnumFacing facing = ((AbstractMachineEntity) tileEntity).getFacing();
    boolean active = ((AbstractMachineEntity) tileEntity).isActive();
    if (active) {
      states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON.rotate(facing)));
    } else {
      states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT.rotate(facing)));
    }

    renderIO(tileEntity, block, states);

    return states;
  }

}
