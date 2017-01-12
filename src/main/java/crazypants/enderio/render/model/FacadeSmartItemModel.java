package crazypants.enderio.render.model;

import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.property.EnumRenderPart;
import crazypants.enderio.render.util.ItemQuadCollector;
import crazypants.util.Prep;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

public class FacadeSmartItemModel implements IPerspectiveAwareModel {

  private final IPerspectiveAwareModel parent;

  public FacadeSmartItemModel(IPerspectiveAwareModel parent) {
    this.parent = parent;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
    return parent.getQuads(state, side, rand);
  }

  @Override
  public boolean isAmbientOcclusion() {
    return parent.isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return parent.isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return parent.getParticleTexture();
  }

  @SuppressWarnings("deprecation")
  @Override
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return parent.getItemCameraTransforms();
  }

  @Override
  public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
      net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType cameraTransformType) {
    return parent.handlePerspective(cameraTransformType);
  }

  private final ItemOverrideList overrides = new ItemOverrideList(Lists.<ItemOverride> newArrayList()) {
    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
      if (originalModel == null || Prep.isInvalid(stack)) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      }

      if (!YetaUtil.shouldHeldItemHideFacadesClient()) {
        IBlockState paintSource = PainterUtil2.getSourceBlock(stack);
        if (paintSource != null) {
          ItemQuadCollector quads = new ItemQuadCollector();
          quads.addItemBlockState(paintSource, null);
          quads.addBlockState(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY), null);
          return new CollectedItemQuadBakedBlockModel(originalModel, quads);
        }
      }

      return originalModel;
    }
  };

  @Override
  public ItemOverrideList getOverrides() {
    return overrides;
  }

}
