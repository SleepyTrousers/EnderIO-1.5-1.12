package crazypants.enderio.machine.obelisk.render;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;

public class ObeliskBakedModel implements ISmartBlockModel {

  public static final TextureSupplier particleIcon = TextureRegistry.registerTexture("blocks/blockSoulMachineBottom");
  
  private IBakedModel defaultModel;
  
  private final boolean isActive;
  
  private ObeliskBakedModel activeModel;
  private ObeliskBakedModel model;
  
  public ObeliskBakedModel(IBakedModel defaultBakedModel) {
    this(defaultBakedModel, false);
  }
  
  public ObeliskBakedModel(IBakedModel defaultBakedModel, boolean b) {
    defaultModel = defaultBakedModel;
    isActive = b;
  }


  @Override
  public IBakedModel handleBlockState(IBlockState state) {
    if(state instanceof IBlockStateWrapper) {
      TileEntity te = ((IBlockStateWrapper) state).getTileEntity();
      if(te instanceof AbstractMachineEntity) {
        if(((AbstractMachineEntity) te).isActive()) {
          if(activeModel == null) {
            activeModel = new ObeliskBakedModel(defaultModel, true);
          }
          return activeModel;
        } 
      }
    }
    if(model == null) {
      model = new ObeliskBakedModel(defaultModel, false);
    }
    return model;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
    return Collections.emptyList();
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    if(isActive) {
      return ObeliskModelQuads.INSTANCE_ACTIVE.getQuads();
    }
    return ObeliskModelQuads.INSTANCE.getQuads();
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
    return particleIcon.get(TextureAtlasSprite.class);
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return getDefaults().getItemCameraTransforms();
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
