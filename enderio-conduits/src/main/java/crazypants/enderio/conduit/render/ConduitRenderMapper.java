package crazypants.enderio.conduit.render;

import java.util.EnumMap;
import java.util.List;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.conduit.TileConduitBundle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ConduitRenderMapper implements IRenderMapper.IBlockRenderMapper.IRenderLayerAware.IPaintAware {

  public static final ConduitRenderMapper instance = new ConduitRenderMapper();

  @Override
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
      QuadCollector quadCollector) {

    TileEntity tileEntity = state.getTileEntity();

    if (tileEntity instanceof TileConduitBundle) {
      IBlockState paintSource = ((TileConduitBundle) tileEntity).getPaintSource();
      if (paintSource != null && (paintSource.isOpaqueCube() || ((TileConduitBundle) tileEntity).getFacadeType().isTransparent())
          && !state.getYetaDisplayMode().isHideFacades()) {
        return null;
      }
      List<BakedQuad> quads = ConduitBundleRenderManager.instance.getConduitBundleRenderer().getGeneralQuads(state, blockLayer);
      quadCollector.addQuads(null, blockLayer, quads);
    }

    return null;
  }

  @Override
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

}
