package crazypants.enderio.machine.farm;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IRenderMapper;

public class FarmingStationRenderMapper extends MachineRenderMapper implements IRenderMapper.IRenderLayerAware {

  public FarmingStationRenderMapper() {
    super(null);
  }

  @Override
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer, AbstractMachineEntity tileEntity, AbstractMachineBlock block) {
    boolean active = tileEntity.isActive();
    if (blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
      if (active) {
        return Collections.singletonList(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON));
      } else {
        return Collections.singletonList(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON_SOUTH));
      }
    } else if (blockLayer == EnumWorldBlockLayer.SOLID) {
      return Collections.singletonList(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    }

    return null;
  }

}
