package crazypants.enderio.render.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Throwables;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

public class QuadCollector {

  @SuppressWarnings("unchecked")
  private final List<BakedQuad>[] table = new List[mkKey(EnumFacing.values()[EnumFacing.values().length - 1],
      BlockRenderLayer.values()[BlockRenderLayer.values().length - 1]) + 1];

  private static Integer mkKey(EnumFacing side, BlockRenderLayer pass) {
    return (side == null ? 0 : side.ordinal() + 1) * BlockRenderLayer.values().length + (pass == null ? 0 : pass.ordinal());
  }

  public void addQuads(EnumFacing side, BlockRenderLayer pass, List<BakedQuad> quads) {
    if (quads != null && !quads.isEmpty()) {
      Integer key = mkKey(side, pass);
      if (table[key] == null) {
        table[key] = new ArrayList<BakedQuad>(quads);
      } else {
        if (!(table[key] instanceof ArrayList)) {
          // don't want to add if we have a CompositeList or a Collections.emptyList()
          table[key] = new ArrayList<BakedQuad>(table[key]);
        }
        table[key].addAll(quads);
        ((ArrayList<BakedQuad>) table[key]).trimToSize();
      }
    }
  }

  public List<BakedQuad> getQuads(EnumFacing side, BlockRenderLayer pass) {
    Integer key = mkKey(side, pass);
    if (table[key] == null) {
      return Collections.<BakedQuad> emptyList();
    } else {
      return table[key];
    }
  }

  /**
   * Adds the baked model(s) of the given block states to the quad lists for the given block layer. The models are expected to behave. The block layer will be
   * NOT set when the models are asked for their quads.
   */
  public void addFriendlyBlockStates(BlockRenderLayer pass, List<IBlockState> states) {
    if (states == null || states.isEmpty()) {
      return;
    }

    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    for (IBlockState state : states) {
      IBakedModel model = modelShapes.getModelForState(state);
      List<BakedQuad> generalQuads = model.getQuads(state, null, 0);
      if (generalQuads != null && !generalQuads.isEmpty()) {
        addQuads(null, pass, generalQuads);
      }
      for (EnumFacing face : EnumFacing.values()) {
        List<BakedQuad> faceQuads = model.getQuads(state, face, 0);
        if (faceQuads != null && !faceQuads.isEmpty()) {
          addQuads(face, pass, faceQuads);
        }
      }
    }
  }

  /**
   * Adds a baked model that is may blow up to the quad lists for the given block layer. The block layer will NOT be set when the model is asked for its quads.
   * <p>
   * Any errors from the model will be returned.
   */
  public List<String> addUnfriendlybakedModel(BlockRenderLayer pass, IBakedModel model, IBlockState state, long rand) {
    if (model == null) {
      return null;
    }
    List<String> errors = new ArrayList<String>();

    try {
      List<BakedQuad> generalQuads = model.getQuads(state, null, rand);
      if (generalQuads != null && !generalQuads.isEmpty()) {
        addQuads(null, pass, generalQuads);
      }
    } catch (Throwable t) {
      errors.add(Throwables.getStackTraceAsString(t));
    }
    for (EnumFacing face : EnumFacing.values()) {
      try {
        List<BakedQuad> faceQuads = model.getQuads(state, face, rand);
        if (faceQuads != null && !faceQuads.isEmpty()) {
          addQuads(face, pass, faceQuads);
        }
      } catch (Throwable t) {
        errors.add(Throwables.getStackTraceAsString(t));
      }
    }

    return errors.isEmpty() ? null : errors;
  }

  /**
   * Adds a baked model that is expected to behave to the quad lists for the given block layer. The block layer will be set when the model is asked for its
   * quads.
   */
  public void addFriendlybakedModel(BlockRenderLayer pass, IBakedModel model, @Nullable IBlockState state, long rand) {
    if (model != null) {
      BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
      ForgeHooksClient.setRenderLayer(pass);
      List<BakedQuad> generalQuads = model.getQuads(state, null, rand);
      if (generalQuads != null && !generalQuads.isEmpty()) {
        addQuads(null, pass, generalQuads);
      }
      for (EnumFacing face : EnumFacing.values()) {
        List<BakedQuad> faceQuads = model.getQuads(state, face, rand);
        if (faceQuads != null && !faceQuads.isEmpty()) {
          addQuads(face, pass, faceQuads);
        }
      }
      ForgeHooksClient.setRenderLayer(oldRenderLayer);
    }
  }

  public Collection<BlockRenderLayer> getBlockLayers() {
    return Arrays.asList(BlockRenderLayer.values());
  }

  public boolean isEmpty() {
    for (List<BakedQuad> entry : table) {
      if (entry != null && !entry.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public @Nonnull QuadCollector combine(@Nullable QuadCollector other) {
    if (other == null || other.isEmpty()) {
      return this;
    }
    if (this.isEmpty()) {
      return other;
    }
    QuadCollector result = new QuadCollector();
    for (int i = 0; i < table.length; i++) {
      result.table[i] = CompositeList.create(this.table[i], other.table[i]);
    }
    return result;
  }

}
