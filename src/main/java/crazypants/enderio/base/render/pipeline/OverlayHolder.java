package crazypants.enderio.base.render.pipeline;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.render.property.IOMode;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;

public class OverlayHolder {

  private final static @Nonnull QuadCollector[][] data = new QuadCollector[EnumFacing.values().length][IOMode.EnumIOMode.values().length];

  public static void collectOverlayQuads(@Nonnull ModelBakeEvent event) {
    final Block block = ModObject.block_machine_io.getBlockNN();
    Map<IBlockState, ModelResourceLocation> locations = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(block);

    NNIterator<IOMode> modes = crazypants.enderio.base.render.property.IOMode.MODES.iterator();
    while (modes.hasNext()) {
      IOMode mode = modes.next();
      IBlockState state = block.getDefaultState().withProperty(IOMode.IO, mode);
      ModelResourceLocation mrl = locations.get(state);
      if (mrl == null) {
        throw new RuntimeException("Model for state " + state + " failed to load from " + mrl + ". ");
      }
      IBakedModel model = event.getModelRegistry().getObject(mrl);
      if (model == null) {
        throw new RuntimeException("Model for state " + state + " failed to load from " + mrl + ".");
      }

      QuadCollector quads = new QuadCollector();

      BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
      BlockRenderLayer layer = block.getBlockLayer();
      ForgeHooksClient.setRenderLayer(layer);
      List<BakedQuad> generalQuads = model.getQuads(state, null, 0);
      if (!generalQuads.isEmpty()) {
        quads.addQuads(null, layer, generalQuads);
      }
      for (EnumFacing face1 : EnumFacing.values()) {
        List<BakedQuad> faceQuads = model.getQuads(state, mode.getDirection(), 0);
        if (!faceQuads.isEmpty()) {
          quads.addQuads(face1, layer, faceQuads);
        }
      }
      ForgeHooksClient.setRenderLayer(oldRenderLayer);

      data[mode.getDirection().ordinal()][mode.getIomode().ordinal()] = quads;
    }
  }

  public static @Nullable QuadCollector getOverlay(EnumMap<EnumFacing, IOMode.EnumIOMode> overlay) {
    QuadCollector quads = null;
    if (overlay != null) {
      for (Entry<EnumFacing, EnumIOMode> entry : overlay.entrySet()) {
        quads = data[entry.getKey().ordinal()][entry.getValue().ordinal()].combine(quads);
      }
    }
    return quads;
  }

}
