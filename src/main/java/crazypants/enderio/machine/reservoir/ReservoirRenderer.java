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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import crazypants.enderio.tool.SmartTank;

@SideOnly(Side.CLIENT)
public class ReservoirRenderer extends TileEntitySpecialRenderer<TileReservoir>  {

  public static final TextureSupplier switchIcon = TextureRegistry.registerTexture("blocks/reservoirSwitch");

  private final BlockReservoir block;

  public ReservoirRenderer(BlockReservoir res) {
    block = res;
  }

  @Override
  public void renderTileEntityAt(TileReservoir tileentity, double x, double y, double z, float f, int b) {

    TileReservoir res = tileentity;
    float fullness = res.getFilledRatio();

    if (res.tank.getFluidAmount() > 0 || res.isAutoEject()) {
      GlStateManager.pushMatrix();
      GlStateManager.pushAttrib();
      GlStateManager.enableCull();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

      GlStateManager.translate(x, y, z);
      RenderUtil.bindBlockTexture();

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer tes = tessellator.getWorldRenderer();
      tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

      if (res.tank.getFluidAmount() > 0) {
        renderTankFluid(res.tank, x, y, z, getMergers(tileentity.getWorld(), tileentity.getPos()), tileentity.getWorld(), tileentity.getPos());
      }

      if (res.isAutoEject()) {
        float val = RenderUtil.claculateTotalBrightnessForLocation(tileentity.getWorld(), tileentity.getPos());

        BoundingBox bb = BoundingBox.UNIT_CUBE;

        GlStateManager.color(val, val, val, 1);
        for (EnumFacing dir : EnumFacing.VALUES) {
          drawSwitch(dir, bb);
        }
      }

      tessellator.draw();
      GlStateManager.popAttrib();
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
    if (tank == null || tank.getFluid() == null || tank.getFluidAmount() == 0) {
      return;
    }
    TextureAtlasSprite icon = RenderUtil.getStillTexture(tank.getFluid());
    if (icon != null) {
      float fullness = tank.getFilledRatio();

      boolean[][][] merge = getMergers9(world, pos);

      // BoundingBox cube = new BoundingBox(//
      // mergers.contains(EnumFacing.WEST) ? -0.001 : 0.001,//
      // mergers.contains(EnumFacing.DOWN) ? -0.001 : 0.001,//
      // mergers.contains(EnumFacing.NORTH) ? -0.001 : 0.001,//
      // mergers.contains(EnumFacing.EAST) ? 1.001 : 0.999,//
      // (mergers.contains(EnumFacing.UP) ? 1.001 : 0.999) * fullness,//
      // mergers.contains(EnumFacing.SOUTH) ? 1.001 : 0.999//
      // );

      WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();

      float minU = icon.getMinU();
      float maxU = icon.getMaxU();
      float minV = icon.getMinV();
      float maxV = icon.getMaxV();

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
          renderFace(tes, bb, dir, minU, maxU, minVx, maxVx);
        }
      }

      // for (EnumFacing dir : EnumFacing.Plane.VERTICAL) {
      // if (!mergers.contains(dir)) {
      // renderFace(tes, cube, dir, minU, maxU, minV, maxV);
      // } else {
      // BlockPos pos2 = pos.offset(dir);
      // TileEntity tileEntity = world.getTileEntity(pos2);
      // if (tileEntity instanceof TileReservoir) {
      // TileReservoir res2 = (TileReservoir) tileEntity;
      // if (dir == EnumFacing.DOWN ? !res2.tank.isFull() : (fullness < 1f || res2.tank.getFluidAmount() == 0)) {
      // renderFace(tes, cube, dir, minU, maxU, minV, maxV);
      // }
      // } else {
      // renderFace(tes, cube, dir, minU, maxU, minV, maxV);
      // }
      // }
      // }
      //
      // for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
      // if (!mergers.contains(dir)) {
      // renderFace(tes, cube, dir, minU, maxU, icon.getInterpolatedV((1 - fullness) * 16), maxV);
      // } else {
      // BlockPos pos2 = pos.offset(dir);
      // TileEntity tileEntity = world.getTileEntity(pos2);
      // if (tileEntity instanceof TileReservoir) {
      // TileReservoir res2 = (TileReservoir) tileEntity;
      // float fullness2 = res2.tank.getFilledRatio();
      // if (fullness2 < fullness) {
      // BoundingBox cube2 = new BoundingBox(cube.minX, fullness2, cube.minZ, cube.maxX, cube.maxY, cube.maxZ);
      // renderFace(tes, cube, dir, minU, maxU, icon.getInterpolatedV((1 - fullness) * 16), icon.getInterpolatedV((1 - fullness2) * 16));
      // }
      // } else {
      // renderFace(tes, cube, dir, minU, maxU, icon.getInterpolatedV((1 - fullness) * 16), maxV);
      // }
      // }
      // }

    }
  }

  public static void renderFace(WorldRenderer tes, BoundingBox bb, EnumFacing face, float minU, float maxU, float minV, float maxV) {
    List<Vertex> corners = bb.getCornersWithUvForFace(face, minU, maxU, minV, maxV);
    for (Vertex v : corners) {
      tes.pos(v.x(), v.y(), v.z()).tex(v.u(), v.v()).endVertex();
    }
  }

  private Vector3d forward = new Vector3d();
  private Vector3d left = new Vector3d();
  private Vector3d up = new Vector3d();
  private Vector3d offset = new Vector3d();

  private void drawSwitch(EnumFacing dir, BoundingBox bb) {

    Vector3d cent = bb.getCenter();
    offset.set(cent);

    boolean isUp = dir.getFrontOffsetY() != 0;
    forward.set(ForgeDirectionOffsets.forDir(dir));
    forward.scale(0.5);
    forward.x *= bb.sizeX();
    forward.y *= bb.sizeY();
    forward.z *= bb.sizeZ();

    offset.add(forward);

    if (dir.getFrontOffsetY() == 0) {
      offset.y += bb.sizeY() * 0.25;
    }
    if (dir.getFrontOffsetX() == 0) {
      offset.x -= (isUp ? dir.getFrontOffsetY() : dir.getFrontOffsetZ()) * bb.sizeX() * 0.25;
    }
    if (dir.getFrontOffsetZ() == 0) {
      offset.z += (isUp ? -dir.getFrontOffsetY() : dir.getFrontOffsetX()) * bb.sizeZ() * 0.25;
    }

    left.set(isUp ? -dir.getFrontOffsetY() : -dir.getFrontOffsetZ(), 0, dir.getFrontOffsetX());

    if (isUp) {
      up.set(0, 0, -1);
    } else {
      up.set(0, 1, 0);
    }

    forward.scale(0.5);
    left.scale(0.125);
    up.scale(0.125);

    TextureAtlasSprite icon = switchIcon.get(TextureAtlasSprite.class);
    if (icon != null) {

      WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();
      tes.pos(offset.x + left.x - up.x, offset.y + left.y - up.y, offset.z + left.z - up.z).tex(icon.getMinU(), icon.getMaxV()).endVertex();
      tes.pos(offset.x - left.x - up.x, offset.y - left.y - up.y, offset.z - left.z - up.z).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
      tes.pos(offset.x - left.x + up.x, offset.y - left.y + up.y, offset.z - left.z + up.z).tex(icon.getMaxU(), icon.getMinV()).endVertex();
      tes.pos(offset.x + left.x + up.x, offset.y + left.y + up.y, offset.z + left.z + up.z).tex(icon.getMinU(), icon.getMinV()).endVertex();
    }

  }

}
