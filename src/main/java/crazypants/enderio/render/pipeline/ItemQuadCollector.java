package crazypants.enderio.render.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import org.apache.commons.lang3.tuple.Pair;

public class ItemQuadCollector {

  private static final List<Integer> FACING = new ArrayList<Integer>();

  static {
    FACING.add(-1);
    for (EnumFacing face : EnumFacing.values()) {
      FACING.add(face.ordinal());
    }
  }

  @SuppressWarnings("unchecked")
  private final List<BakedQuad>[] table = new List[FACING.size()];

  private static Integer facing2Integer(EnumFacing facing) {
    return facing == null ? facing.values().length : facing.ordinal();
  }

  public void addQuads(EnumFacing side, List<BakedQuad> quads) {
    Integer face = facing2Integer(side);
    List<BakedQuad> list = table[face];
    if (list == null) {
      table[face] = new ArrayList<BakedQuad>(quads);
    } else {
      list.addAll(quads);
    }
  }

  public List<BakedQuad> getQuads(EnumFacing side) {
    Integer face = facing2Integer(side);
    if (table[face] == null) {
      return Collections.<BakedQuad> emptyList();
    } else {
      return table[face];
    }
  }

  public void addBlockStates(List<Pair<IBlockState, ItemStack>> states, ItemStack parent, Block parentBlock) {
    if (states == null || states.isEmpty()) {
      return;
    }

    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    for (Pair<IBlockState, ItemStack> pair : states) {
      IBlockState state = pair.getLeft();
      if (state != null) {
        ItemStack stack = pair.getRight();
        if (stack == null) {
          if (state.getBlock() == parentBlock) {
            stack = parent;
          } else {
            stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
          }
        }
        IBakedModel model = modelShapes.getModelForState(state);
        addBakedModel(model, stack);
      }
    }
  }

  public void addItemBlockStates(List<Pair<IBlockState, ItemStack>> states, ItemStack parent, Block parentBlock) {
    if (states == null || states.isEmpty()) {
      return;
    }

    for (Pair<IBlockState, ItemStack> pair : states) {
      IBlockState state = pair.getLeft();
      if (state != null) {
        ItemStack stack = pair.getRight();
        if (stack == null) {
          if (state.getBlock() == parentBlock) {
            stack = parent;
          } else {
            stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
          }
        }
        addItemModel(stack);
      }
    }
  }

  public void addBlockState(IBlockState state, ItemStack stack) {
    if (state != null) {
      if (stack == null) {
        stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
      }
      BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
      IBakedModel model = modelShapes.getModelForState(state);
      addBakedModel(model, stack);
    }
  }

  public void addItemBlockState(IBlockState state, ItemStack stack) {
    if (state != null) {
      if (stack == null) {
        stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
      }
      addItemModel(stack);
    }
  }

  public void addItemModel(ItemStack stack) {
    if (stack != null) {
      addItemBakedModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack));
    }
  }

  public void addBakedModel(IBakedModel model, ItemStack stack) {
    if (model instanceof net.minecraftforge.client.model.ISmartItemModel) {
      model = ((net.minecraftforge.client.model.ISmartItemModel) model).handleItemState(stack);
    }
    // model = model.getOverrides().handleItemState(model, stack, (World)null, (EntityLivingBase)null);
    if (model == null) {
      model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
    }
    addItemBakedModel(model);
  }

  public void addItemBakedModel(IBakedModel model) {
    List<BakedQuad> generalQuads = model.getGeneralQuads(); // model.getQuads((IBlockState)null, (EnumFacing)null, 0L);
    if (generalQuads != null && !generalQuads.isEmpty()) {
      addQuads(null, generalQuads);
    }
    for (EnumFacing face : EnumFacing.values()) {
      List<BakedQuad> faceQuads = model.getFaceQuads(face); // model.getQuads((IBlockState)null, face, 0L);
      if (faceQuads != null && !faceQuads.isEmpty()) {
        addQuads(face, faceQuads);
      }
    }
  }

  public boolean isEmpty() {
    for (List<BakedQuad> entry : table) {
      if (entry != null && !entry.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public @Nonnull ItemQuadCollector combine(@Nullable ItemQuadCollector other) {
    if (other == null || other.isEmpty()) {
      return this;
    }
    if (this.isEmpty()) {
      return other;
    }
    ItemQuadCollector result = new ItemQuadCollector();
    for (Integer facing : FACING) {
      result.table[facing] = CompositeList.create(this.table[facing], other.table[facing]);
    }
    return result;
  }

}
