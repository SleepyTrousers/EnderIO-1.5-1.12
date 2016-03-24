package crazypants.enderio.render.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;

import com.google.common.base.Throwables;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

public class QuadCollector {

  private static final List<Integer> FACING = new ArrayList<Integer>();
  private static final List<EnumWorldBlockLayer> PASS = Arrays.asList(EnumWorldBlockLayer.values());

  static {
    FACING.add(-1);
    for (EnumFacing face : EnumFacing.values()) {
      FACING.add(face.ordinal());
    }
  }

  private final Table<Integer, EnumWorldBlockLayer, List<BakedQuad>> table = ArrayTable.create(FACING, PASS);

  private static Integer facing2Integer(EnumFacing facing) {
    return facing == null ? -1 : facing.ordinal();
  }

  public void addQuads(EnumFacing side, EnumWorldBlockLayer pass, List<BakedQuad> quads) {
    Integer face = facing2Integer(side);
    List<BakedQuad> list = table.get(face, pass);
    if (list == null) {
      table.put(face, pass, new ArrayList<BakedQuad>(quads));
    } else {
      list.addAll(quads);
    }
  }

  public List<BakedQuad> getQuads(EnumFacing side, EnumWorldBlockLayer pass) {
    Integer face = facing2Integer(side);
    List<BakedQuad> list = table.get(face, pass);
    if (list == null) {
      return Collections.<BakedQuad> emptyList();
    } else {
      return list;
    }
  }

  public void addFriendlyBlockStates(EnumWorldBlockLayer pass, List<IBlockState> states) {
    if (states == null || states.isEmpty()) {
      return;
    }

    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    for (IBlockState state : states) {
      IBakedModel model = modelShapes.getModelForState(state);
      List<BakedQuad> generalQuads = model.getGeneralQuads();
      if (generalQuads != null && !generalQuads.isEmpty()) {
        addQuads(null, pass, generalQuads);
      }
      for (EnumFacing face : EnumFacing.values()) {
        List<BakedQuad> faceQuads = model.getFaceQuads(face);
        if (faceQuads != null && !faceQuads.isEmpty()) {
          addQuads(face, pass, faceQuads);
        }
      }
    }
  }

  public List<String> addUnfriendlybakedModel(EnumWorldBlockLayer pass, IBakedModel model, IBlockState state, long rand) {
    if (model == null) {
      return null;
    }
    List<String> errors = new ArrayList<String>();

    try {
      List<BakedQuad> generalQuads = model.getGeneralQuads();
      if (generalQuads != null && !generalQuads.isEmpty()) {
        addQuads(null, pass, generalQuads);
      }
    } catch (Throwable t) {
      errors.add(Throwables.getStackTraceAsString(t));
    }
    for (EnumFacing face : EnumFacing.values()) {
      try {
        List<BakedQuad> faceQuads = model.getFaceQuads(face);
        if (faceQuads != null && !faceQuads.isEmpty()) {
          addQuads(face, pass, faceQuads);
        }
      } catch (Throwable t) {
        errors.add(Throwables.getStackTraceAsString(t));
      }
    }

    return errors.isEmpty() ? null : errors;
  }

  public Collection<EnumWorldBlockLayer> getBlockLayers() {
    return PASS;
  }

  public boolean isEmpty() {
    for (List<BakedQuad> entry : table.values()) {
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
    for (Integer facing : FACING) {
      for (EnumWorldBlockLayer pass : PASS) {
        result.table.put(facing, pass, CompositeList.create(this.table.get(facing, pass), other.table.get(facing, pass)));
      }
    }
    return result;
  }

}
