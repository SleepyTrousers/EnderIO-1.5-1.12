package crazypants.enderio.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

public class MachineSmartModel implements ISmartBlockModel, ISmartItemModel {

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

  public MachineSmartModel(IBakedModel defaults) {
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

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return ItemCameraTransforms.DEFAULT;
  }

  @Override
  public IBakedModel handleBlockState(IBlockState state) {
    IRenderCache rc = null;
    if (state instanceof BlockStateWrapper) {
      TileEntity tileEntity = ((BlockStateWrapper) state).getTileEntity();

      if (tileEntity instanceof IRenderCache) {
        rc = (IRenderCache) tileEntity;
        IBakedModel cachedModel = rc.getCachedModel();
        if (cachedModel != null) {
          return cachedModel;
        }
      }

      Block block = state.getBlock();
      IBlockAccess world = ((BlockStateWrapper) state).getWorld();
      BlockPos pos = ((BlockStateWrapper) state).getPos();

      if (block instanceof ISmartRenderAwareBlock) {
        IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper(state, world, pos);
        List<IBlockState> states = renderMapper.mapBlockRender(state, world, pos);
        CombinedBakedModel bakedModel = CombinedBakedModel.buildFromStates(null, states);
        if (rc != null) {
          rc.cacheModel(bakedModel);
        }
        return bakedModel;
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
        if (block instanceof ISmartRenderAwareBlock) {
          IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper(stack);
          List<IBlockState> states = renderMapper.mapBlockRender(block, stack);
          CombinedBakedModel bakedModel = CombinedBakedModel.buildFromStates(getDefaults(), states);
          return bakedModel;
        }
      }
    }
    return this;
  }

}
