package crazypants.enderio.conduit.render;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.render.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.render.paint.PaintWrangler;
import jline.internal.Log;
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

  private final ConduitRenderState state;
  
  private final IBakedModel facadeModel;

  public ConduitBundleBakedModel(IBakedModel defaultBakedModel) {
    this(defaultBakedModel, null, null);    
  }

  public ConduitBundleBakedModel(IBakedModel defaultBakedModel, ConduitRenderState state, IBakedModel facadeModel) {
    this.defaultModel = defaultBakedModel;
    this.state = state;
    this.facadeModel = facadeModel;
  }

  @Override
  public IBakedModel handleBlockState(IBlockState stateIn) {
    ConduitRenderState crs = null;
    IBakedModel facade = null;
    if (stateIn instanceof ConduitRenderState) {
      crs = (ConduitRenderState) stateIn;      
      if (crs.getRenderFacade()) {
        try {
          Pair<IBakedModel, Boolean> res = PaintWrangler.handlePaint(crs, (IBlockPaintableBlock) crs.getBlock(), crs.getWorld(), crs.getPos());

          if (res.getLeft() != null) {
            if (crs.getBundle().getPaintSource().getBlock().isOpaqueCube()) {
              return res.getLeft();
            } else {
              facade = res.getLeft();
            }
          }
        } catch (Exception e) {
          Log.warn("Could not get model for facade: " + crs.getBundle().getPaintSource());
          e.printStackTrace();
        }
      }       
    }
    return new ConduitBundleBakedModel(defaultModel,crs, facade);
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    if (state == null) {
      return getDefaults().getFaceQuads(facing);
    }
    if(facadeModel != null) {
      return facadeModel.getFaceQuads(facing);
    }
    return Collections.emptyList();
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    if (state == null) {
      return getDefaults().getGeneralQuads();
    }
    List<BakedQuad> quads = ConduitBundleRenderManager.instance.getConduitBundleRenderer().getGeneralQuads(state);
    if(facadeModel != null) {
      if(!quads.isEmpty()) {
        quads.addAll(facadeModel.getGeneralQuads());
      } else {
        quads = facadeModel.getGeneralQuads();
      }
    }    
    return quads; 
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
