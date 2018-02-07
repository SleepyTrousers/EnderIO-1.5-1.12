package crazypants.enderio.machines.machine.soul;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.common.util.IBlockAccessWrapper;

import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.property.EnumRenderMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import static crazypants.enderio.machines.init.MachineObject.block_soul_binder;

public class SoulBinderTESR extends ManagedTESR<TileSoulBinder> {

  public SoulBinderTESR() {
    super(block_soul_binder.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileSoulBinder te, @Nonnull IBlockState blockState, int renderPass) {
    return te.isWorking() && (te.getPaintSource() == null || YetaUtil.shouldHeldItemHideFacadesClient());
  }

  @Override
  protected void renderTileEntity(@Nonnull TileSoulBinder te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    GlStateManager.translate(0.5f, 0, 0.5f);
    GlStateManager.rotate(360 * te.getProgress(), 0, 1, 0);
    GlStateManager.translate(-0.5f, 0, -0.5f);

    GL11.glDisable(GL11.GL_LIGHTING); // sic!

    EnumRenderMode renderMode = te.isActive() ? EnumRenderMode.FRONT_ON : EnumRenderMode.FRONT;
    renderBlockModel(te.getWorld(), te.getPos(), blockState.withProperty(EnumRenderMode.RENDER, renderMode.rotate(te.getFacing())), true,
        te.getProgress() > 0.005 && te.getProgress() < 0.995);

    GL11.glEnable(GL11.GL_LIGHTING); // sic!
  }

  @SuppressWarnings("null")
  public static void renderBlockModel(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean translateToOrigin, boolean relight) {
    BufferBuilder wr = Tessellator.getInstance().getBuffer();
    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    if (translateToOrigin) {
      wr.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
    }
    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    BlockModelShapes modelShapes = blockrendererdispatcher.getBlockModelShapes();
    IBakedModel ibakedmodel = modelShapes.getModelForState(state);
    final IBlockAccess worldWrapper = relight ? new WorldWrapper(world, pos) : world;
    for (BlockRenderLayer layer : BlockRenderLayer.values()) {
      if (state.getBlock().canRenderInLayer(state, layer)) {
        ForgeHooksClient.setRenderLayer(layer);
        blockrendererdispatcher.getBlockModelRenderer().renderModel(worldWrapper, ibakedmodel, state, pos, wr, false);
      }
    }
    ForgeHooksClient.setRenderLayer(null);
    if (translateToOrigin) {
      wr.setTranslation(0, 0, 0);
    }
    Tessellator.getInstance().draw();
  }

  /**
   * Wraps the world so that all sides of a block have the same light value. Needed when rotating the model around the z axis. Otherwise quads facing a wall
   * would be black.
   * <p>
   * We cheat a bit here by pretending the block is surrounded by air blocks. That's so much easier than to compute 6 virtual blocks from all surrounding
   * blocks.
   *
   */
  private static class WorldWrapper extends IBlockAccessWrapper {

    private final Map<BlockPos, Pair<Integer, Integer>> light = new HashMap<BlockPos, Pair<Integer, Integer>>();

    @SuppressWarnings("null")
    public WorldWrapper(@Nonnull World world, @Nonnull BlockPos pos) {
      super(world);

      // faces

      int maxSky = 0, maxBlk = 0;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        maxSky = Math.max(maxSky, world.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos.offset(face)));
        maxBlk = Math.max(maxBlk, world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos.offset(face)));
      }
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        light.put(pos.offset(face), Pair.of(maxSky, maxBlk));
      }

      // above faces

      maxSky = maxBlk = 0;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        maxSky = Math.max(maxSky, world.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos.offset(face).up()));
        maxBlk = Math.max(maxBlk, world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos.offset(face).up()));
      }
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        light.put(pos.offset(face).up(), Pair.of(maxSky, maxBlk));
      }

      // below faces

      maxSky = maxBlk = 0;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        maxSky = Math.max(maxSky, world.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos.offset(face).down()));
        maxBlk = Math.max(maxBlk, world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos.offset(face).down()));
      }
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        light.put(pos.offset(face).down(), Pair.of(maxSky, maxBlk));
      }

      // high corners

      maxSky = maxBlk = 0;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        maxSky = Math.max(maxSky, world.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos.offset(face).up().offset(face.rotateY())));
        maxBlk = Math.max(maxBlk, world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos.offset(face).up().offset(face.rotateY())));
      }
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        light.put(pos.offset(face).up().offset(face.rotateY()), Pair.of(maxSky, maxBlk));
      }

      // low corners

      maxSky = maxBlk = 0;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        maxSky = Math.max(maxSky, world.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos.offset(face).down().offset(face.rotateY())));
        maxBlk = Math.max(maxBlk, world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos.offset(face).down().offset(face.rotateY())));
      }
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        light.put(pos.offset(face).down().offset(face.rotateY()), Pair.of(maxSky, maxBlk));
      }

      // corners

      maxSky = maxBlk = 0;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        maxSky = Math.max(maxSky, world.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos.offset(face).offset(face.rotateY())));
        maxBlk = Math.max(maxBlk, world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos.offset(face).offset(face.rotateY())));
      }
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        light.put(pos.offset(face).offset(face.rotateY()), Pair.of(maxSky, maxBlk));
      }
    }

    @Override
    public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
      if (light.containsKey(pos)) {
        Pair<Integer, Integer> pair = light.get(pos);
        int sky = pair.getLeft(), blk = pair.getRight();
        if (blk < lightValue) {
          blk = lightValue;
        }
        return sky << 20 | blk << 4;
      }
      return wrapped.getCombinedLight(pos, lightValue);
    }

    @Override
    public @Nonnull IBlockState getBlockState(@Nonnull BlockPos pos) {
      if (light.containsKey(pos)) {
        return Blocks.AIR.getDefaultState();
      }
      return super.getBlockState(pos);
    }

    @Override
    public boolean isAirBlock(@Nonnull BlockPos pos) {
      if (light.containsKey(pos)) {
        return true;
      }
      return super.isAirBlock(pos);
    }

  }

}
