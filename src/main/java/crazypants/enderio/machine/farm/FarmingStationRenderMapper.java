package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IRenderMapper;

public class FarmingStationRenderMapper extends MachineRenderMapper implements IRenderMapper.IRenderLayerAware {

  public FarmingStationRenderMapper() {
    super(null);
  }

  @Override
  protected Pair<List<IBlockState>, List<IBakedModel>> render(IBlockState state, IBlockAccess world, BlockPos pos,
      TileEntity tileEntity, Block block) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    
    boolean active = ((AbstractMachineEntity) tileEntity).isActive();
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

    return Pair.of(states, null);
  }

  @Override
  protected List<IBlockState> renderIO(TileEntity tileEntity, Block block) {
    if (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.CUTOUT) {
      return super.renderIO(tileEntity, block);
    } else {
      return null;
    }
  }

}
