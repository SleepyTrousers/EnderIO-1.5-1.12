package crazypants.enderio.render.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * This class can hold all quads a backed item model needs. It can collect them from multiple source, including other ItemQuadCollectors.
 * <p>
 * <em>Details</em>
 * <p>
 * <strong>allFacesToGeneral</strong> - If allFacesToGeneral is set to true, all collected quads will be added to the list of "general" quads. This is needed if
 * the order in which the quads are being rendered is important, e.g. because there are overlaying translucent textures.
 *
 */
public class ItemQuadCollector {

  @SuppressWarnings("unchecked")
  private final List<BakedQuad>[] table = new List[EnumFacing.values().length + 1];

  private static Integer facing2Integer(EnumFacing facing) {
    return facing == null ? EnumFacing.values().length : facing.ordinal();
  }

  public void addQuads(EnumFacing side, List<BakedQuad> quads) {
    if (quads != null && !quads.isEmpty()) {
      Integer face = facing2Integer(side);
      List<BakedQuad> list = table[face];
      if (list == null) {
        table[face] = new ArrayList<BakedQuad>(quads);
      } else {
        list.addAll(quads);
      }
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
    addBlockState(state, stack, false);
  }

  public void addBlockState(IBlockState state, ItemStack stack, boolean allFacesToGeneral) {
    if (state != null) {
      if (stack == null) {
        stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
      }
      BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
      IBakedModel model = modelShapes.getModelForState(state);
      addBakedModel(model, stack, allFacesToGeneral);
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
    if (stack != null && stack.getItem() != null) {
      addItemBakedModel(Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null));
    }
  }

  public void addBakedModel(IBakedModel model, ItemStack stack) {
    addBakedModel(model, stack, false);
  }

  public void addBakedModel(IBakedModel model, ItemStack stack, boolean allFacesToGeneral) {
    model = model.getOverrides().handleItemState(model, stack, (World) null, (EntityLivingBase) null);
    if (model == null) {
      model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
    }
    addItemBakedModel(model, allFacesToGeneral);
  }

  public void addItemBakedModel(IBakedModel model) {
    addItemBakedModel(model, false);
  }

  public void addItemBakedModel(IBakedModel model, boolean allFacesToGeneral) {
    List<BakedQuad> generalQuads = model.getQuads((IBlockState) null, (EnumFacing) null, 0L);
    if (generalQuads != null && !generalQuads.isEmpty()) {
      addQuads(null, generalQuads);
    }
    for (EnumFacing face : EnumFacing.values()) {
      List<BakedQuad> faceQuads = model.getQuads((IBlockState) null, face, 0L);
      if (faceQuads != null && !faceQuads.isEmpty()) {
        addQuads(allFacesToGeneral ? null : face, faceQuads);
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
    for (int i = 0; i < table.length; i++) {
      result.table[i] = CompositeList.create(this.table[i], other.table[i]);
    }
    return result;
  }

}
