package crazypants.enderio.machine.reservoir;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import crazypants.enderio.render.state.GlState;
import crazypants.enderio.tool.SmartTank;
@SideOnly(Side.CLIENT)
public class ReservoirRenderer extends TileEntitySpecialRenderer<TileReservoir>  {

  private static final GlState state = GlState.create("color", 1.0f, 1.0f, 1.0f, 1.0f, "blend", true, GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
      "lighting", false, "colormask", true, true, true, true, "depth", true, true, GL11.GL_LEQUAL, "cullface", true, GL11.GL_BACK, "alpha", true,
      GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE, "normalize", false);

  public static final TextureSupplier switchIcon = TextureRegistry.registerTexture("blocks/reservoirSwitch");

  private final BlockReservoir block;

  public ReservoirRenderer(BlockReservoir res) {
    block = res;
  }

  @Override
  public void renderTileEntityAt(TileReservoir tileentity, double x, double y, double z, float f, int b) {
    if (tileentity != null && tileentity.tank.getFluidAmount() > 0) {
      Minecraft.getMinecraft().entityRenderer.disableLightmap();
      GlStateManager.pushMatrix();

      state.apply();

      GlStateManager.translate(x, y, z);
      RenderUtil.bindBlockTexture();

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer tes = tessellator.getWorldRenderer();
      tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

      Set<EnumFacing> mergers = getMergers(tileentity.getWorld(), tileentity.getPos());

      renderTankFluid(tileentity.tank, x, y, z, mergers, tileentity.getWorld(), tileentity.getPos());

      tessellator.draw();
      GlState.CLEAN_TESR_STATE.apply_filtered(state);
      GlStateManager.popMatrix();
      Minecraft.getMinecraft().entityRenderer.enableLightmap();
    }
  }

  private Set<EnumFacing> getMergers(World world, BlockPos pos) {
    EnumSet<EnumFacing> result = EnumSet.noneOf(EnumFacing.class);
    for (EnumFacing dir : EnumFacing.VALUES) {
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
      if (fullness2 > 0) {
        minY = maxY * fullness2;
      }
      maxY *= fullness;
    }

    return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
  }

  private void renderTankFluid(SmartTank tank, double x, double y, double z, Set<EnumFacing> mergers, World world, BlockPos pos) {
    TextureAtlasSprite icon = RenderUtil.getStillTexture(tank.getFluid());
    if (icon != null) {
      int color = tank.getFluid().getFluid().getColor(tank.getFluid());
      float fullness = tank.getFilledRatio();

      boolean[][][] merge = getMergers9(world, pos);

      WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();

      float minU = icon.getMinU(), maxU = icon.getMaxU(), minV = icon.getMinV(), maxV = icon.getMaxV();

      for (EnumFacing dir : EnumFacing.VALUES) {
        float fullness2 = 0;
        if (mergers.contains(dir)) {
          BlockPos pos2 = pos.offset(dir);
          TileEntity tileEntity = world.getTileEntity(pos2);
          if (tileEntity instanceof TileReservoir) {
            TileReservoir res2 = (TileReservoir) tileEntity;
            fullness2 = res2.tank.getFilledRatio();
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

  public static void renderFace(WorldRenderer tes, BoundingBox bb, EnumFacing face, float minU, float maxU, float minV, float maxV, int color) {
    float d = LightUtil.diffuseLight(face);
    float r = d * ((color >> 16) & 0xFF) / 255f, g = d * ((color >> 8) & 0xFF) / 255f, b = d * (color & 0xFF) / 255f, a = ((color >> 24) & 0xFF) / 255f;

    List<Vertex> corners = bb.getCornersWithUvForFace(face, minU, maxU, minV, maxV);
    for (Vertex v : corners) {
      tes.pos(v.x(), v.y(), v.z()).tex(v.u(), v.v()).color(r, g, b, a).endVertex();
    }
  }

}
