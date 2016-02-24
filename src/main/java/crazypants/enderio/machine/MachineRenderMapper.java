package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.dummy.BlockMachineIO;

public class MachineRenderMapper implements IRenderMapper {

  private final EnumRenderPart body;

  public MachineRenderMapper(EnumRenderPart body) {
    this.body = body;
  }

  @Override
  public List<IBlockState> mapBlockRender(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state instanceof BlockStateWrapper) {
      TileEntity tileEntity = ((BlockStateWrapper) state).getTileEntity();
      Block block = state.getBlock();

      if ((tileEntity instanceof AbstractMachineEntity) && (block instanceof AbstractMachineBlock)) {
        List<IBlockState> states = new ArrayList<IBlockState>();

        EnumFacing facing = ((AbstractMachineEntity) tileEntity).getFacing();
        boolean active = ((AbstractMachineEntity) tileEntity).isActive();

        states.add(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, body.rotate(facing)));

        if (active) {
          states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON.rotate(facing)));
        } else {
          states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT.rotate(facing)));
        }

        for (EnumFacing face : EnumFacing.values()) {
          IoMode ioMode = ((AbstractMachineEntity) tileEntity).getIoMode(face);
          if (ioMode != IoMode.NONE) {
            EnumIOMode iOMode = ((AbstractMachineBlock) block).mapIOMode(ioMode);
            states.add(BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode)));
          }
        }

        return states;
      }
    }
    return null;
  }

  @Override
  public List<IBlockState> mapBlockRender(Block block, ItemStack stack) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    states.add(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, body));
    states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    return states;
  }

}
