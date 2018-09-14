package crazypants.enderio.machines.machine.reservoir;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vertex;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReservoirRenderer extends ManagedTESR<TileReservoirBase> {

  public ReservoirRenderer(BlockReservoirBase res) {
    super(res);
  }

  @Override
  protected boolean shouldRender(@Nonnull TileReservoirBase te, @Nonnull IBlockState blockState, int renderPass) {
    return !te.getTank().isEmpty();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileReservoirBase te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    NNList<RenderFace> faces = computeGeometry(te.getTank(), te.getWorld(), te.getPos());

    if (faces.isEmpty()) {
      return;
    }

    final Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();

    for (int i = 0; i <= 1; i++) {
      GlStateManager.cullFace(i == 0 ? CullFace.FRONT : CullFace.BACK);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      faces.apply(new RenderFaces(buffer));
      tessellator.draw();
    }
  }

  @Override
  public void renderTileEntityFast(@Nonnull TileReservoirBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial,
      @Nonnull BufferBuilder buffer) {
    if (NullHelper.untrust(te) != null && te.hasWorld() && !te.isInvalid() && !te.getTank().isEmpty()) {
      buffer.setTranslation(x, y, z);
      computeGeometry(te.getTank(), te.getWorld(), te.getPos()).apply(new RenderFacesFast(buffer));
      buffer.setTranslation(0, 0, 0);
    }
  }

  private BoundingBox mkFace(@Nonnull boolean[][][] merge, @Nonnull EnumFacing face, float fullness, float fullness2, boolean gas) {
    // TODO: implement gas
    double minX = 0f, minY = 0f, minZ = 0f, maxX = 1f, maxY = 1f, maxZ = 1f;
    switch (face) {
    case DOWN:
      if (merge[1 + 0][1 - 1][1 + 0]) {
        if (fullness2 == 1) {
          return null;
        } else {
          minY = 0;
        }
      } else {
        minY = 0.001;
      }
      minX = merge[1 - 1][1 + 0][1 + 0] ? merge[1 - 1][1 - 1][1 + 0] ? -0.001 : 0 : 0.001;
      maxX = merge[1 + 1][1 + 0][1 + 0] ? merge[1 + 1][1 - 1][1 + 0] ? +1.001 : 1 : 0.999;
      minZ = merge[1 + 0][1 + 0][1 - 1] ? merge[1 + 0][1 - 1][1 - 1] ? -0.001 : 0 : 0.001;
      maxZ = merge[1 + 0][1 + 0][1 + 1] ? merge[1 + 0][1 - 1][1 + 1] ? +1.001 : 1 : 0.999;
      break;
    case EAST:
      if (merge[1 + 1][1 + 0][1 + 0]) {
        if (fullness <= fullness2) {
          return null;
        } else {
          maxX = 1;
        }
      } else {
        maxX = 0.999;
      }
      minY = merge[1 + 0][1 - 1][1 + 0] ? merge[1 + 1][1 - 1][1 + 0] ? -0.001 : 0 : 0.001;
      maxY = merge[1 + 0][1 + 1][1 + 0] ? merge[1 + 1][1 + 1][1 + 0] ? +1.001 : 1 : 0.999;
      minZ = merge[1 + 0][1 + 0][1 - 1] ? merge[1 + 1][1 + 0][1 - 1] ? -0.001 : 0 : 0.001;
      maxZ = merge[1 + 0][1 + 0][1 + 1] ? merge[1 + 1][1 + 0][1 + 1] ? +1.001 : 1 : 0.999;
      break;
    case NORTH:
      if (merge[1 + 0][1 + 0][1 - 1]) {
        if (fullness <= fullness2) {
          return null;
        } else {
          minZ = 0;
        }
      } else {
        minZ = 0.001;
      }
      minX = merge[1 - 1][1 + 0][1 + 0] ? merge[1 - 1][1 + 0][1 - 1] ? -0.001 : 0 : 0.001;
      maxX = merge[1 + 1][1 + 0][1 + 0] ? merge[1 + 1][1 + 0][1 - 1] ? +1.001 : 1 : 0.999;
      minY = merge[1 + 0][1 - 1][1 + 0] ? merge[1 + 0][1 - 1][1 - 1] ? -0.001 : 0 : 0.001;
      maxY = merge[1 + 0][1 + 1][1 + 0] ? merge[1 + 0][1 + 1][1 - 1] ? +1.001 : 1 : 0.999;
      break;
    case SOUTH:
      if (merge[1 + 0][1 + 0][1 + 1]) {
        if (fullness <= fullness2) {
          return null;
        } else {
          maxZ = 1;
        }
      } else {
        maxZ = 0.999;
      }
      minX = merge[1 - 1][1 + 0][1 + 0] ? merge[1 - 1][1 + 0][1 + 1] ? -0.001 : 0 : 0.001;
      maxX = merge[1 + 1][1 + 0][1 + 0] ? merge[1 + 1][1 + 0][1 + 1] ? +1.001 : 1 : 0.999;
      minY = merge[1 + 0][1 - 1][1 + 0] ? merge[1 + 0][1 - 1][1 + 1] ? -0.001 : 0 : 0.001;
      maxY = merge[1 + 0][1 + 1][1 + 0] ? merge[1 + 0][1 + 1][1 + 1] ? +1.001 : 1 : 0.999;
      break;
    case UP:
      if (merge[1 + 0][1 + 1][1 + 0]) {
        if (fullness2 > 0 && fullness == 1) {
          return null;
        } else {
          maxY = 1;
        }
      } else {
        maxY = 0.999;
      }
      minX = merge[1 - 1][1 + 0][1 + 0] ? merge[1 - 1][1 + 1][1 + 0] ? -0.001 : 0 : 0.001;
      maxX = merge[1 + 1][1 + 0][1 + 0] ? merge[1 + 1][1 + 1][1 + 0] ? +1.001 : 1 : 0.999;
      minZ = merge[1 + 0][1 + 0][1 - 1] ? merge[1 + 0][1 + 1][1 - 1] ? -0.001 : 0 : 0.001;
      maxZ = merge[1 + 0][1 + 0][1 + 1] ? merge[1 + 0][1 + 1][1 + 1] ? +1.001 : 1 : 0.999;
      break;
    case WEST:
      if (merge[1 - 1][1 + 0][1 + 0]) {
        if (fullness <= fullness2) {
          return null;
        } else {
          minX = 0;
        }
      } else {
        minX = 0.001;
      }
      minY = merge[1 + 0][1 - 1][1 + 0] ? merge[1 - 1][1 - 1][1 + 0] ? -0.001 : 0 : 0.001;
      maxY = merge[1 + 0][1 + 1][1 + 0] ? merge[1 - 1][1 + 1][1 + 0] ? +1.001 : 1 : 0.999;
      minZ = merge[1 + 0][1 + 0][1 - 1] ? merge[1 - 1][1 + 0][1 - 1] ? -0.001 : 0 : 0.001;
      maxZ = merge[1 + 0][1 + 0][1 + 1] ? merge[1 - 1][1 + 0][1 + 1] ? +1.001 : 1 : 0.999;
      break;
    default:
      break;
    }

    if (face != EnumFacing.DOWN) {
      if (fullness2 > 0 && face != EnumFacing.UP) {
        minY = maxY * fullness2;
      }
      maxY *= fullness;
    }

    return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
  }

  protected NNList<RenderFace> computeGeometry(@Nonnull SmartTank tank, @Nonnull World world, @Nonnull BlockPos pos) {
    final FluidStack fluid = tank.getFluid();
    if (fluid == null) {
      return NNList.emptyList();
    }

    final float[][][] full = new float[3][3][3];
    final boolean[][][] merge = new boolean[3][3][3];
    merge[1][1][1] = true;
    for (int dx = 0; dx < 3; dx++) {
      for (int dy = 0; dy < 3; dy++) {
        for (int dz = 0; dz < 3; dz++) {
          final BlockPos pos2 = pos.add(dx - 1, dy - 1, dz - 1);
          if (!merge[dx][dy][dz] && world.getBlockState(pos2).getBlock() == block) {
            final TileReservoirBase reservoir = BlockReservoirBase.getAnyTileEntity(world, pos2, TileReservoirBase.class);
            if (reservoir != null) {
              final FluidStack fluid2 = reservoir.getTank().getFluid();
              if (fluid2 != null && fluid2.getFluid() == fluid.getFluid()) {
                full[dx][dy][dz] = reservoir.getTank().getFilledRatio();
                merge[dx][dy][dz] = true;
              }
            }
          }
        }
      }
    }

    final TextureAtlasSprite icon = RenderUtil.getStillTexture(fluid);
    final float minU = icon.getMinU(), maxU = icon.getMaxU(), minV = icon.getMinV(), maxV = icon.getMaxV();
    final int brightness = world.getCombinedLight(pos, fluid.getFluid().getLuminosity(fluid));
    final int l1 = brightness >> 16 & 0xFFFF, l2 = brightness & 0xFFFF;
    final int color = fluid.getFluid().getColor(fluid);
    final float r = ((color >> 16) & 0xFF) / 255f, g = ((color >> 8) & 0xFF) / 255f, b = (color & 0xFF) / 255f, a = ((color >> 24) & 0xFF) / 255f;

    NNList<RenderFace> faces = new NNList<>();

    for (NNIterator<EnumFacing> facings = NNList.FACING.fastIterator(); facings.hasNext();) {
      final EnumFacing dir = facings.next();
      final Vec3i vec = dir.getDirectionVec();
      final float fullness0 = tank.getFilledRatio();
      final float fullness1 = full[vec.getX() + 1][vec.getY() + 1][vec.getZ() + 1];
      final BoundingBox bb = mkFace(merge, dir, fullness0, fullness1, fluid.getFluid().isGaseous(fluid));
      if (bb != null) {
        float minVx = minV, maxVx = maxV;
        if (dir.getAxis() != EnumFacing.Axis.Y) {
          // TODO: This probably needs swapping for fluid.getFluid().isGaseous(fluid)
          minVx = icon.getInterpolatedV((1 - fullness0) * 16);
          maxVx = icon.getInterpolatedV((1 - fullness1) * 16);
        }

        faces.add(new RenderFace(bb, dir, minU, maxU, minVx, maxVx, r, g, b, a, l1, l2));
      }
    }
    return faces;
  }

  private static final class RenderFacesFast implements Callback<ReservoirRenderer.RenderFace> {
    private final @Nonnull BufferBuilder buffer;

    public RenderFacesFast(@Nonnull BufferBuilder buffer) {
      this.buffer = buffer;
    }

    @Override
    public void apply(@Nonnull RenderFace e) {
      for (Vertex v : e.bb.getCornersWithUvForFace(e.face, e.minU, e.maxU, e.minV, e.maxV)) {
        // DefaultVertexFormats.BLOCK
        buffer.pos(v.x(), v.y(), v.z()).color(e.r, e.g, e.b, e.a).tex(v.u(), v.v()).lightmap(e.light1, e.light2).endVertex();
      }
    }
  }

  private static final class RenderFaces implements Callback<ReservoirRenderer.RenderFace> {
    private final @Nonnull BufferBuilder buffer;

    public RenderFaces(@Nonnull BufferBuilder buffer) {
      this.buffer = buffer;
    }

    @Override
    public void apply(@Nonnull RenderFace e) {
      for (Vertex v : e.bb.getCornersWithUvForFace(e.face, e.minU, e.maxU, e.minV, e.maxV)) {
        // DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL
        buffer.pos(v.x(), v.y(), v.z()).tex(v.u(), v.v()).color(e.r, e.g, e.b, e.a).normal(v.nx(), v.ny(), v.nz()).endVertex();
      }
    }
  }

  private static final class RenderFace {
    final @Nonnull BoundingBox bb;
    final @Nonnull EnumFacing face;
    final float minU, maxU, minV, maxV, r, g, b, a;
    final int light1, light2;

    protected RenderFace(@Nonnull BoundingBox bb, @Nonnull EnumFacing face, float minU, float maxU, float minV, float maxV, float r, float g, float b, float a,
        int light1, int light2) {
      this.bb = bb;
      this.face = face;
      this.minU = minU;
      this.maxU = maxU;
      this.minV = minV;
      this.maxV = maxV;
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      this.light1 = light1;
      this.light2 = light2;
    }

  }

}
