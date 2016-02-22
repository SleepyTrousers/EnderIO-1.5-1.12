package crazypants.enderio.machine.alloy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IRenderCache;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.PropertyIO;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.dummy.BlockMachineIO;

public class RenderMapperAlloySmelter implements IRenderMapper {

  // TODO get a better way of mapping these depending on the block
  private static final IOMode.EnumIOMode[] io2io = { IOMode.EnumIOMode.NONE, IOMode.EnumIOMode.PULL, IOMode.EnumIOMode.PUSH, IOMode.EnumIOMode.PUSHPULL,
      IOMode.EnumIOMode.DISABLED };

  @Override
  public List<IBlockState> mapBlockRender(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state instanceof BlockStateWrapper) {
      TileEntity tileEntity = ((BlockStateWrapper) state).getTileEntity();

      if (tileEntity instanceof AbstractMachineEntity) {
        EnumFacing facing = ((AbstractMachineEntity) tileEntity).getFacing();
        boolean active = ((AbstractMachineEntity) tileEntity).isActive();

        List<IBlockState> states = new ArrayList<IBlockState>();

        states.add(BlockMachineBase.block.getDefaultState().withProperty(BlockMachineBase.SUB, EnumRenderPart.BODY.rotate(facing)));

        System.out.println("Alloy Smelter facing=" + facing + " body=" + EnumRenderPart.BODY.rotate(facing) + " front="
            + (active ? EnumRenderPart.ALLOY_SMELTER_ON.rotate(facing) : EnumRenderPart.ALLOY_SMELTER.rotate(facing)));

        if (active) {
          states.add(BlockMachineBase.block.getDefaultState().withProperty(BlockMachineBase.SUB, EnumRenderPart.ALLOY_SMELTER_ON.rotate(facing)));
        } else {
          states.add(BlockMachineBase.block.getDefaultState().withProperty(BlockMachineBase.SUB, EnumRenderPart.ALLOY_SMELTER.rotate(facing)));
        }

        for (EnumFacing face : EnumFacing.values()) {
          IoMode ioMode = ((AbstractMachineEntity) tileEntity).getIoMode(face);
          if (ioMode != IoMode.NONE) {
            states.add(BlockMachineIO.block.getDefaultState().withProperty(PropertyIO.getInstance(), IOMode.get(face, io2io[ioMode.ordinal()])));
          }
        }

        return states;
      }
    }
    return null;
  }

}
