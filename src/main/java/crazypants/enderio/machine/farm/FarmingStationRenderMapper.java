package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
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
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, AbstractMachineEntity tileEntity, AbstractMachineBlock block) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    
    boolean active = tileEntity.isActive();
    if (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.TRANSLUCENT) {
      if (active) {
        states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON));
      } else {
        states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON_SOUTH));
      }
    } else if (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.SOLID) {
      states.add(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    } else {
      return null;
    }

    return states;
  }

}
