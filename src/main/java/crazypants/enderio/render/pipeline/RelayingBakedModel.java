package crazypants.enderio.render.pipeline;

import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import org.apache.commons.lang3.tuple.Pair;

public class RelayingBakedModel implements ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel {

  private IBakedModel defaults;

  private IBakedModel getDefaults() {
    if (defaults == null) {
      try {
        defaults = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      } catch (Throwable t) {

      }
    }
    return defaults;
  }

  public RelayingBakedModel(IBakedModel defaults) {
    this.defaults = defaults;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
    return getDefaults().getFaceQuads(p_177551_1_);
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return getDefaults().getGeneralQuads();
  }

  @Override
  public boolean isAmbientOcclusion() {
    return getDefaults().isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return getDefaults().isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return getDefaults().getParticleTexture();
  }

  @SuppressWarnings("deprecation")
  @Override
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return net.minecraft.client.renderer.block.model.ItemCameraTransforms.DEFAULT;
  }

  @Override
  public IBakedModel handleBlockState(IBlockState stateIn) {
    long start = crazypants.util.Profiler.client.start();
    if (stateIn instanceof BlockStateWrapperBase) {
      final BlockStateWrapperBase state = (BlockStateWrapperBase) stateIn;
      IBakedModel model = state.getModel();
      if (model instanceof CollectedQuadBakedBlockModel) {
        ((CollectedQuadBakedBlockModel) model).setParticleTexture(getParticleTexture());
      }
      if (model != null) {
        crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (relayed)");
        return model;
      }
    }

    return this;
  }

  // @Override
  // public ItemOverrideList getOverrides() {
  // return EnderItemOverrideList.instance;
  // }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    return EnderItemOverrideList.handleItemState(getDefaults(), stack, null, null);
  }

  @Override
  public VertexFormat getFormat() {
    return Attributes.DEFAULT_BAKED_FORMAT;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
    if (getDefaults() instanceof IPerspectiveAwareModel) {
      Pair<? extends IFlexibleBakedModel, Matrix4f> perspective = ((IPerspectiveAwareModel) getDefaults()).handlePerspective(cameraTransformType);
      return Pair.of(this, perspective.getRight());
    }
    return Pair.of(this, null);
  }

}
