package crazypants.enderio.machine.killera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.client.render.VertexTranslation;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.pipeline.ItemQuadCollector;
import crazypants.enderio.render.pipeline.QuadCollector;
import crazypants.enderio.tool.SmartTank;

public class KillerJoeRenderMapper extends MachineRenderMapper implements IRenderMapper.IBlockRenderMapper.IRenderLayerAware,
    IItemRenderMapper.IDynamicOverlayMapper {

  public static final KillerJoeRenderMapper instance = new KillerJoeRenderMapper();

  protected KillerJoeRenderMapper() {
    super(null);
  }

  @Override
  protected EnumMap<EnumFacing, EnumIOMode> renderIO(@Nonnull AbstractMachineEntity tileEntity, @Nonnull AbstractMachineBlock<?> block) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer,
      QuadCollector quadCollector) {
    Block block = state.getBlock();
    if (blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
      return Collections.singletonList(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON));
    } else if (blockLayer == EnumWorldBlockLayer.SOLID) {
      quadCollector.addQuads(null, EnumWorldBlockLayer.SOLID, renderHead());
      return Collections.singletonList(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    }
    return null;
  }

  private static final Double px = 1d / 16d;

  private List<BakedQuad> renderHead() {
    BoundingBox bb = new BoundingBox(4 * px, 4 * px, 4 * px, 12 * px, 12 * px, 12 * px);
    VertexTransform rotx = new VertexRotation(0.03054326, new Vector3d(1, 0, 0), new Vector3d(8 * px, 8 * px, 8 * px));
    VertexTransform roty = new VertexRotation(0.17453290, new Vector3d(0, 1, 0), new Vector3d(8 * px, 8 * px, 8 * px));
    VertexTransform rotz = new VertexRotation(0.23928460, new Vector3d(0, 0, 1), new Vector3d(8 * px, 8 * px, 8 * px));
    VertexTransform mov = new VertexTranslation(0, -1 * px, 0);
    TextureAtlasSprite tex1 = BlockKillerJoe.textureHead1.get(TextureAtlasSprite.class);
    TextureAtlasSprite tex2 = BlockKillerJoe.textureHead2.get(TextureAtlasSprite.class);
    List<BakedQuad> quads = new ArrayList<BakedQuad>();

    bake(quads, bb, EnumFacing.NORTH, 0f, .5f, 0f, .5f, tex1, null, rotx, roty, rotz, mov);
    bake(quads, bb, EnumFacing.EAST, .5f, 1f, 0f, .5f, tex1, null, rotx, roty, rotz, mov);
    bake(quads, bb, EnumFacing.SOUTH, 0f, .5f, .5f, 1f, tex1, null, rotx, roty, rotz, mov);
    bake(quads, bb, EnumFacing.WEST, .5f, 1f, .5f, 1f, tex1, null, rotx, roty, rotz, mov);
    bake(quads, bb, EnumFacing.UP, 0f, .5f, 0f, .5f, tex2, null, rotx, roty, rotz, mov);
    bake(quads, bb, EnumFacing.DOWN, .5f, 1f, 0f, .5f, tex2, null, rotx, roty, rotz, mov);

    return quads;
  }

  private void bake(List<BakedQuad> quads, BoundingBox bb, EnumFacing face, float umin, float umax, float vmin, float vmax, TextureAtlasSprite text,
      Vector4f color, VertexTransform... xforms) {
    List<Vertex> corners = bb.getCornersWithUvForFace(face, umin, umax, vmin, vmax);
    for (Vertex vertex : corners) {
      for (VertexTransform xform : xforms) {
        xform.apply(vertex);
      }
    }
    RenderUtil.addBakedQuads(quads, corners, text, color);
  }

  private List<BakedQuad> renderFuel(ItemStack stack) {
    if (stack.hasTagCompound()) {
      SmartTank tank = TileKillerJoe.loadTank(stack.getTagCompound());
      float ratio = tank.getFilledRatio();
      if (ratio > 0.01) {

        float height = 1 - ratio;

        ResourceLocation still = tank.getFluid().getFluid().getStill(tank.getFluid());
        int color = tank.getFluid().getFluid().getColor(tank.getFluid());
        Vector4f vecC = new Vector4f((color >> 16 & 0xFF) / 255d * 2, (color >> 8 & 0xFF) / 255d * 2, (color & 0xFF) / 255d * 2, 1);
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(still.toString());

        BoundingBox bb = new BoundingBox(2.51 * px, 1 * px, 2.51 * px, 13.49 * px, (13d * ratio + 1) * px, 13.49 * px);

        List<BakedQuad> quads = new ArrayList<BakedQuad>();

        bake(quads, bb, EnumFacing.NORTH, 0f, 1f, height, 1f, sprite, vecC);
        bake(quads, bb, EnumFacing.EAST, 0f, 1f, height, 1f, sprite, vecC);
        bake(quads, bb, EnumFacing.SOUTH, 0f, 1f, height, 1f, sprite, vecC);
        bake(quads, bb, EnumFacing.WEST, 0f, 1f, height, 1f, sprite, vecC);
        bake(quads, bb, EnumFacing.UP, 0f, 1f, 0f, 1f, sprite, vecC);

        return quads;
      }
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT), stack));
    if (!stack.hasTagCompound()) {
      states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON), stack));
    }
    itemQuadCollector.addQuads(null, renderHead());
    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ICacheKey getCacheKey(Block block, ItemStack stack, ICacheKey cacheKey) {
    return cacheKey.addCacheKey(stack.hasTagCompound());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemQuadCollector mapItemDynamicOverlayRender(Block block, ItemStack stack) {
    if (stack.hasTagCompound()) {
      ItemQuadCollector result = new ItemQuadCollector();
      result.addQuads(null, renderFuel(stack));
      result.addBlockState(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON), stack);
      return result;
    } else {
      return null;
    }
  }

}
