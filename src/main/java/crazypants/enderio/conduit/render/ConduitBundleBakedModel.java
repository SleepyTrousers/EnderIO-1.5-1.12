package crazypants.enderio.conduit.render;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
      ConduitRenderState iExtendedBlockState = (ConduitRenderState) state;
      return new ConduitBundleBakedModel(defaultModel, iExtendedBlockState);
    }    
    return new ConduitBundleBakedModel(defaultModel);
  }
  
  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    if(state == null) {
      return getDefaults().getFaceQuads(facing);
    }
    //TODO: Facades
    return Collections.emptyList();
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {    
    if(state == null) {
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
