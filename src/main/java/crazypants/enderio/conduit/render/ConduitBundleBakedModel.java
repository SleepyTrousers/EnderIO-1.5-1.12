package crazypants.enderio.conduit.render;

import java.util.Collections;
import java.util.List;

import jline.internal.Log;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;

@SuppressWarnings("deprecation")
public class ConduitBundleBakedModel implements ISmartBlockModel {

  private IBakedModel defaultModel;

  private ConduitRenderState state;

  public ConduitBundleBakedModel(IBakedModel defaultBakedModel) {
    defaultModel = defaultBakedModel;
  }

  public ConduitBundleBakedModel(IBakedModel defaultBakedModel, ConduitRenderState state) {
    defaultModel = defaultBakedModel;
    this.state = state;
  }

  @Override
  public IBakedModel handleBlockState(IBlockState state) {
    if (state instanceof ConduitRenderState) {
      ConduitRenderState crs = (ConduitRenderState) state;
      if (crs.getRenderFacade()) {
        try {
          BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
          IBakedModel res = blockrendererdispatcher.getBlockModelShapes().getModelForState(crs.getBundle().getFacade());  
          if(res != null) {
            int quadCount = 0;
            for(EnumFacing f : EnumFacing.values()) {
              List<BakedQuad> quads = res.getFaceQuads(f);
              if(quads != null) {
                quadCount += quads.size();
              }
            }
            if(quadCount > 3) {
              return res;
            }
            List<BakedQuad> quads = res.getGeneralQuads();
            if(quads != null && quadCount + quads.size() > 3) {
              return res;
            }
            //TODO: This or nothing?
            return getDefaults();
            
          }
        } catch (Exception e) {
          Log.warn("Could not get model for facade: " + crs.getBundle().getFacade());
          e.printStackTrace();
        }
      } 
      return new ConduitBundleBakedModel(defaultModel, crs);
    }
    return new ConduitBundleBakedModel(defaultModel);
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    if (state == null) {
      return getDefaults().getFaceQuads(facing);
    }
    return Collections.emptyList();
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    if (state == null) {
      return getDefaults().getGeneralQuads();
    }
    return ConduitBundleRenderManager.instance.getConduitBundleRenderer().getGeneralQuads(state);
  }

  @Override
  public boolean isAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean isGui3d() {
    return false;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return getDefaults().getParticleTexture();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return ItemCameraTransforms.DEFAULT;
  }

  private IBakedModel getDefaults() {
    if (defaultModel == null) {
      try {
        defaultModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      } catch (Throwable t) {

      }
    }
    return defaultModel;
  }

}
