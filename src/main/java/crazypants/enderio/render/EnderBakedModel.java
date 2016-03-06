package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ITransformation;

import org.apache.commons.lang3.tuple.Pair;

public class EnderBakedModel implements IPerspectiveAwareModel {

  private final List<BakedQuad> generalQuads = new ArrayList<BakedQuad>();
  private final List<List<BakedQuad>> faceQuads = new ArrayList<List<BakedQuad>>();
  private final boolean ambientOcclusion;
  private final boolean gui3d;
  private final TextureAtlasSprite texture;
  private final ItemCameraTransforms cameraTransforms;
  private final VertexFormat format;
  private final Matrix4f[] transformTypes = new Matrix4f[TransformType.values().length];

  public EnderBakedModel(IBakedModel transforms, Pair<List<IBlockState>, List<Pair<IBakedModel, ITransformation>>> data) {
    this((IPerspectiveAwareModel) (transforms instanceof IPerspectiveAwareModel ? transforms : null), data);
  }

  public EnderBakedModel(IPerspectiveAwareModel transforms, Pair<List<IBlockState>, List<Pair<IBakedModel, ITransformation>>> data) {

    for (EnumFacing face : EnumFacing.values()) {
      faceQuads.add(new ArrayList<BakedQuad>());
    }

    Boolean _ambientOcclusion = null;
    Boolean _gui3d = null;
    TextureAtlasSprite _texture = null;
    ItemCameraTransforms _cameraTransforms = null;
    VertexFormat _format = null;

    List<IBlockState> states = data != null ? data.getLeft() : null;
    List<Pair<IBakedModel, ITransformation>> models = data != null ? data.getRight() : Collections.singletonList(Pair.of(getMissingModel(), (ITransformation) null));
    if (models == null) {
      models = new ArrayList<Pair<IBakedModel, ITransformation>>();
    }

    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    IBakedModel missingModel = modelShapes.getModelManager().getMissingModel();
    for (IBlockState state : states) {
      IBakedModel model = modelShapes.getModelForState(state);
      models.add(Pair.of(model, (ITransformation) null));
      if (_ambientOcclusion == null) {
        _ambientOcclusion = model.isAmbientOcclusion();
        _gui3d = model.isGui3d();
        _texture = model.getParticleTexture();
        _cameraTransforms = model.getItemCameraTransforms();
      }
      if (_format == null && model instanceof IFlexibleBakedModel) {
        _format = ((IFlexibleBakedModel) model).getFormat();
      }
    }

    if (transforms != null) {
      _gui3d = transforms.isAmbientOcclusion();
      _format = transforms.getFormat();
      _cameraTransforms = transforms.getItemCameraTransforms();
      for (TransformType transformType : TransformType.values()) {
        Pair<? extends IFlexibleBakedModel, Matrix4f> pair = transforms.handlePerspective(transformType);
        this.transformTypes[transformType.ordinal()] = pair.getRight();
      }
    }

    for (Pair<IBakedModel, ITransformation> pair : models) {
      IBakedModel bakedModel = pair.getLeft();
      ITransformation transformation = pair.getRight();
      if (transformation == null) {
        transformation = ModelRotation.X0_Y0;
      }
      for (EnumFacing face : EnumFacing.VALUES) {
        faceQuads.get(transformation.rotate(face).ordinal()).addAll(bakedModel.getFaceQuads(face));
      }
      generalQuads.addAll(bakedModel.getGeneralQuads());
    }

    if (_ambientOcclusion == null) {
      _ambientOcclusion = true;
    }
    if (_texture == null) {
      _texture = getMissingModel().getParticleTexture();
    }
    if (_gui3d == null) {
      _gui3d = true;
    }
    if (_cameraTransforms == null) {
      _cameraTransforms = getMissingModel().getItemCameraTransforms();
    }
    if (_format == null) {
      _format = Attributes.DEFAULT_BAKED_FORMAT;
    }

    this.ambientOcclusion = _ambientOcclusion;
    this.gui3d = _gui3d;
    this.texture = _texture;
    this.cameraTransforms = _cameraTransforms;
    this.format = _format;
  }

  private static IBakedModel getMissingModel() {
    return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
  }

  @Override
  public VertexFormat getFormat() {
    return format;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    return (List) this.faceQuads.get(facing.ordinal());
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return generalQuads;
  }

  @Override
  public boolean isAmbientOcclusion() {
    return ambientOcclusion;
  }

  @Override
  public boolean isGui3d() {
    return gui3d;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return texture;
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return cameraTransforms;
  }

  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
    return Pair.of(this, transformTypes[cameraTransformType.ordinal()]);
  }

}
