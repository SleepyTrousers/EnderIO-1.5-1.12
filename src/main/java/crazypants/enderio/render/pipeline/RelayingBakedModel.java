package crazypants.enderio.render.pipeline;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.IPaintable.IWrenchHideablePaint;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.paint.render.PaintWrangler;
import crazypants.enderio.render.EnderBakedModel;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.UnderlayBakedModel;
import crazypants.enderio.render.dummy.BlockMachineBase;

public class RelayingBakedModel implements ISmartBlockModel, ISmartItemModel {

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

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    if (stack != null) {
      Item item = stack.getItem();
      if (item instanceof ItemBlock) {
        Block block = ((ItemBlock) item).getBlock();
        if (block instanceof IBlockPaintableBlock && (!(block instanceof IWrenchHideablePaint) || !YetaUtil.shouldHeldItemHideFacades())) {
          IBakedModel paint = PaintWrangler.handlePaint(stack, (IBlockPaintableBlock) block);
          if (paint != null) {
            if (block instanceof ISmartRenderAwareBlock) {
              // Combine(!) it with the mapBlockRender() because that may want to add overlays to mark the block as "painted"
              IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper();
              Pair<List<IBlockState>, List<IBakedModel>> paintOverlay = renderMapper.mapItemPaintOverlayRender(block, stack);
              if (paintOverlay == null) {
                IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
                EnderBakedModel bakedModel = new EnderBakedModel(getDefaults(), null, Collections.singletonList(stdOverlay));
                return new UnderlayBakedModel(bakedModel, paint);
              } else if ((paintOverlay.getLeft() == null || paintOverlay.getLeft().isEmpty())
                  && (paintOverlay.getRight() == null || paintOverlay.getRight().isEmpty())) {
                return paint;
              } else {
                EnderBakedModel bakedModel = new EnderBakedModel(getDefaults(), paintOverlay);
                return new UnderlayBakedModel(bakedModel, paint);
              }
            }
            return paint;
          }
        }
        if (block instanceof ISmartRenderAwareBlock) {
          IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper();
          return new EnderBakedModel(getDefaults(), renderMapper.mapItemRender(block, stack));
        }
      }
    }
    return this;
  }

}
