package crazypants.enderio.render.pipeline;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import crazypants.enderio.render.dummy.BlockMachineIO;
import crazypants.enderio.render.property.IOMode;
import crazypants.enderio.render.property.IOMode.EnumIOMode;
import crazypants.enderio.render.util.QuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;

public class OverlayHolder {

  private final static QuadCollector[][] data = new QuadCollector[EnumFacing.values().length][IOMode.EnumIOMode.values().length];

  public static void collectOverlayQuads(ModelBakeEvent event) {
    Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(BlockMachineIO.block);

    for (EnumFacing face : EnumFacing.values()) {
      for (EnumIOMode iOMode : EnumIOMode.values()) {
        IBlockState state = BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode));
        ModelResourceLocation mrl = locations.get(state);
        IBakedModel model = event.getModelRegistry().getObject(mrl);
        if (model == null) {
          throw new RuntimeException("Model for state " + state + " failed to load from " + mrl + ".");
        }

        QuadCollector quads = new QuadCollector();

        BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
        BlockRenderLayer layer = BlockMachineIO.block.getBlockLayer();
        ForgeHooksClient.setRenderLayer(layer);
        List<BakedQuad> generalQuads = model.getQuads(state, null, 0);
        if (generalQuads != null && !generalQuads.isEmpty()) {
          quads.addQuads(null, layer, generalQuads);
        }
        for (EnumFacing face1 : EnumFacing.values()) {
          List<BakedQuad> faceQuads = model.getQuads(state, face, 0);
          if (faceQuads != null && !faceQuads.isEmpty()) {
            quads.addQuads(face1, layer, faceQuads);
          }
        }
        ForgeHooksClient.setRenderLayer(oldRenderLayer);

        data[face.ordinal()][iOMode.ordinal()] = quads;
      }
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
