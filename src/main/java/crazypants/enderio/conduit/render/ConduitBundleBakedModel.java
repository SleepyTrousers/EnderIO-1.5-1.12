package crazypants.enderio.conduit.render;

import java.util.Collections;
import java.util.List;

import jline.internal.Log;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import crazypants.enderio.render.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.render.paint.PaintWrangler;

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
  public IBakedModel handleBlockState(IBlockState stateIn) {
    if (stateIn instanceof ConduitRenderState) {
      ConduitRenderState crs = (ConduitRenderState) stateIn;
      if (crs.getRenderFacade()) {
        try {
          IBakedModel res = PaintWrangler.handlePaint(crs, (IBlockPaintableBlock) crs.getBlock(), crs.getWorld(), crs.getPos());

          if (crs.getBundle().getPaintSource().getBlock().isOpaqueCube()) {
            return res;
          } else {
            // TODO render conduits, too. (Combine models with EnderBakedModel, OverlayBakedModel or UnderlayBakedModel)
            return res;
          }
        } catch (Exception e) {
          Log.warn("Could not get model for facade: " + crs.getBundle().getPaintSource());
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
