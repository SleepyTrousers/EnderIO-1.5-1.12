package crazypants.enderio.machines.machine.reservoir;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vertex;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReservoirRenderer extends ManagedTESR<TileReservoir> {

  public ReservoirRenderer(BlockReservoirBase res) {
    super(res);
  }

  @Override
  protected boolean shouldRender(@Nonnull TileReservoir te, @Nonnull IBlockState blockState, int renderPass) {
    return !te.getTank().isEmpty();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileReservoir te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder tes = tessellator.getBuffer();
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
    Set<EnumFacing> mergers = getMergers(te.getWorld(), te.getPos());
    renderTankFluid(te.getTank(), mergers, te.getWorld(), te.getPos());
    tessellator.draw();
  }

  private Set<EnumFacing> getMergers(World world, BlockPos pos) {
    EnumSet<EnumFacing> result = EnumSet.noneOf(EnumFacing.class);
    for (NNIterator<EnumFacing> facings = NNList.FACING.fastIterator(); facings.hasNext();) {
      EnumFacing dir = facings.next();
      BlockPos pos2 = pos.offset(dir);
      if (world.getBlockState(pos2).getBlock() == block) {
        result.add(dir);
      }
    }
    return result;
  }

  private boolean[][][] getMergers9(World world, BlockPos pos) {
    boolean[][][] merge = new boolean[3][3][3];
    for (int dx = 0; dx < 3; dx++) {
      for (int dy = 0; dy < 3; dy++) {
        for (int dz = 0; dz < 3; dz++) {
          BlockPos pos2 = pos.add(dx - 1, dy - 1, dz - 1);
          merge[dx][dy][dz] = world.getBlockState(pos2).getBlock() == block;
        }
      }
    }
    return merge;
  }

  private BoundingBox mkFace(boolean[][][] merge, EnumFacing face, float fullness, float fullness2) {
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

  private void renderTankFluid(SmartTank tank, Set<EnumFacing> mergers, World world, BlockPos pos) {
    final FluidStack fluid = tank.getFluid();
    if (fluid != null) {
      TextureAtlasSprite icon = RenderUtil.getStillTexture(fluid);
      int color = fluid.getFluid().getColor(fluid);
      float fullness = tank.getFilledRatio();

      boolean[][][] merge = getMergers9(world, pos);

      BufferBuilder tes = Tessellator.getInstance().getBuffer();

      float minU = icon.getMinU(), maxU = icon.getMaxU(), minV = icon.getMinV(), maxV = icon.getMaxV();

      for (int i = 0; i <= 1; i++) {
        if (i == 0) {
          GlStateManager.cullFace(CullFace.FRONT);
        } else {
          GlStateManager.cullFace(CullFace.BACK);
        }
        for (NNIterator<EnumFacing> facings = NNList.FACING.fastIterator(); facings.hasNext();) {
          EnumFacing dir = facings.next();
          float fullness2 = 0;
          if (mergers.contains(dir)) {
            BlockPos pos2 = pos.offset(dir);
            TileEntity tileEntity = world.getTileEntity(pos2);
            if (tileEntity instanceof TileReservoir) {
              TileReservoir res2 = (TileReservoir) tileEntity;
              fullness2 = res2.getTank().getFilledRatio();
            }
          }
          BoundingBox bb = mkFace(merge, dir, fullness, fullness2);
          if (bb != null) {
            float minVx = minV, maxVx = maxV;
            if (dir.getAxis() != EnumFacing.Axis.Y) {
              minVx = icon.getInterpolatedV((1 - fullness) * 16);
              maxVx = icon.getInterpolatedV((1 - fullness2) * 16);
            }

            renderFace(tes, bb, dir, minU, maxU, minVx, maxVx, color);
          }
        }
      }
    }
  }

  public static void renderFace(@Nonnull BufferBuilder tes, @Nonnull BoundingBox bb, @Nonnull EnumFacing face, float minU, float maxU, float minV, float maxV,
      int color) {
    float d = 1f; // LightUtil.diffuseLight(face);
    float r = d * ((color >> 16) & 0xFF) / 255f, g = d * ((color >> 8) & 0xFF) / 255f, b = d * (color & 0xFF) / 255f, a = ((color >> 24) & 0xFF) / 255f;

    List<Vertex> corners = bb.getCornersWithUvForFace(face, minU, maxU, minV, maxV);
    for (Vertex v : corners) {
      tes.pos(v.x(), v.y(), v.z()).tex(v.u(), v.v()).color(r, g, b, a).normal(v.nx(), v.ny(), v.nz()).endVertex();
    }
  }

}
