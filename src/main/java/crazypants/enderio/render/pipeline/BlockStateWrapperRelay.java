package crazypants.enderio.render.pipeline;

import javax.annotation.Nonnull;

import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.IPaintable.IWrenchHideablePaint;
import crazypants.enderio.render.model.CollectedQuadBakedBlockModel;
import crazypants.enderio.render.model.NullModel;
import crazypants.enderio.render.util.QuadCollector;
import crazypants.util.Profiler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;

public class BlockStateWrapperRelay extends BlockStateWrapperBase {

  private static final NullModel NULL = new NullModel();

  public BlockStateWrapperRelay(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    super(state, world, pos, null);
  }

  @Override
  public void bakeModel() {
    long start = Profiler.instance.start();
    @Nonnull
    QuadCollector paintQuads = new QuadCollector();
    boolean hasPaintRendered = false;
    String cacheResult;

    if (block instanceof IBlockPaintableBlock && (!(block instanceof IWrenchHideablePaint) || !getYetaDisplayMode().isHideFacades())) {
      hasPaintRendered = PaintWrangler.wrangleBakedModel(world, pos, ((IBlockPaintableBlock) block).getPaintSource(state, world, pos), paintQuads);
    }

    if (!hasPaintRendered) {
      if (MinecraftForgeClient.getRenderLayer() == null || MinecraftForgeClient.getRenderLayer() == block.getBlockLayer()) {
        model = null;
        cacheResult = "relaying";
      } else {
        model = NULL;
        cacheResult = "none";
      }
    } else {
      model = new CollectedQuadBakedBlockModel(paintQuads);
      cacheResult = "paint only";
    }

    Profiler.instance.stop(start, state.getBlock().getLocalizedName() + " (bake, cache=" + cacheResult + ")");
  }

  @Override
  public IBakedModel getModel() {
    return model;
  }

}
