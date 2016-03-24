package crazypants.enderio.render.pipeline;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.dummy.BlockMachineIO;

public class OverlayHolder {

  private final static QuadCollector[][] data = new QuadCollector[EnumFacing.values().length][IOMode.EnumIOMode.values().length];

  public static void collectOverlayQuads(ModelBakeEvent event) {
    Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(BlockMachineIO.block);

    for (EnumFacing face : EnumFacing.values()) {
      for (EnumIOMode iOMode : EnumIOMode.values()) {
        IBlockState state = BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode));
        ModelResourceLocation mrl = locations.get(state);
        IBakedModel model = event.modelRegistry.getObject(mrl);
        if (model == null) {
          throw new RuntimeException("Model for state " + state + " failed to load from " + mrl + ".");
        }

        QuadCollector quads = new QuadCollector();

        EnumWorldBlockLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
        EnumWorldBlockLayer layer = BlockMachineIO.block.getBlockLayer();
        ForgeHooksClient.setRenderLayer(layer);
        List<BakedQuad> generalQuads = model.getGeneralQuads();
        if (generalQuads != null && !generalQuads.isEmpty()) {
          quads.addQuads(null, layer, generalQuads);
        }
        for (EnumFacing face1 : EnumFacing.values()) {
          List<BakedQuad> faceQuads = model.getFaceQuads(face);
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
