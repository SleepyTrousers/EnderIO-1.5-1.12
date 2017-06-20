package crazypants.enderio.machine.capbank.render;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vertex;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static crazypants.enderio.ModObject.blockCapBank;

public class IoDisplay implements IInfoRenderer {

  @Override
  public void render(TileCapBank cb, EnumFacing dir, float partialTick) {
    if (dir.getFrontOffsetY() != 0) {
      return;
    }

    CapBankClientNetwork nw = (CapBankClientNetwork) cb.getNetwork();
    if (nw == null) {
      return;
    }

    CapBankClientNetwork.IOInfo info = nw.getIODisplayInfo(cb.getPos(), dir);
    if (info.isInside()) {
      return;
    }

    int i = cb.getWorld().getCombinedLight(cb.getPos().offset(dir), 0);
    int j = i % 65536;
    int k = i / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    GlStateManager.enablePolygonOffset();
    GlStateManager.doPolygonOffset(-1.0f, -1.0f);

    boolean drawBackground = true;
    if (drawBackground) {
      RenderUtil.bindBlockTexture();

      float scale = 0.85f;
      float offset = (1 - scale) / 2;
      TextureAtlasSprite icon = ((BlockCapBank) blockCapBank.getBlock()).getInfoPanelIcon();
      float minU = icon.getMinU();
      float maxU = icon.getMaxU();
      float minV = icon.getMinV();
      float maxV = icon.getMaxV();

      List<Vertex> vertices = new ArrayList<Vertex>();

      switch (dir) {
      case NORTH: {
        float y0 = offset - (info.height - 1);
        float y1 = 1 - offset;
        float x0 = offset;
        float x1 = info.width - offset;
        float z0 = 0;
        vertices.add(new Vertex(x1, y0, z0, minU, minV, 0f, 0f, -1f));
        vertices.add(new Vertex(x0, y0, z0, maxU, minV, 0f, 0f, -1f));
        vertices.add(new Vertex(x0, y1, z0, maxU, maxV, 0f, 0f, -1f));
        vertices.add(new Vertex(x1, y1, z0, minU, maxV, 0f, 0f, -1f));
        break;
      }

      case SOUTH: {
        float y0 = offset - (info.height - 1);
        float y1 = 1 - offset;
        float x0 = offset - (info.width - 1);
        float x1 = 1 - offset;
        float z1 = 1;
        vertices.add(new Vertex(x0, y0, z1, maxU, minV, 0f, 0f, 1f));
        vertices.add(new Vertex(x1, y0, z1, minU, minV, 0f, 0f, 1f));
        vertices.add(new Vertex(x1, y1, z1, minU, maxV, 0f, 0f, 1f));
        vertices.add(new Vertex(x0, y1, z1, maxU, maxV, 0f, 0f, 1f));

        break;
      }

      case EAST: {
        float y0 = offset - (info.height - 1);
        float y1 = 1 - offset;
        float z0 = offset;
        float z1 = info.width - offset;
        float x1 = 1;
        vertices.add(new Vertex(x1, y1, z0, maxU, maxV, 1f, 0f, 0f));
        vertices.add(new Vertex(x1, y1, z1, minU, maxV, 1f, 0f, 0f));
        vertices.add(new Vertex(x1, y0, z1, minU, minV, 1f, 0f, 0f));
        vertices.add(new Vertex(x1, y0, z0, maxU, minV, 1f, 0f, 0f));

        break;
      }

      case WEST: {
        float y0 = offset - (info.height - 1);
        float y1 = 1 - offset;
        float z0 = offset - (info.width - 1);
        float z1 = 1 - offset;
        float x0 = 0;
        vertices.add(new Vertex(x0, y0, z0, maxU, minV, -1f, 0f, 0f));
        vertices.add(new Vertex(x0, y0, z1, minU, minV, -1f, 0f, 0f));
        vertices.add(new Vertex(x0, y1, z1, minU, maxV, -1f, 0f, 0f));
        vertices.add(new Vertex(x0, y1, z0, maxU, maxV, -1f, 0f, 0f));

        break;
      }

      default:
        throw new AssertionError();
      }

      GlStateManager.color(1, 1, 1);
      RenderUtil.addVerticesToTessellator(vertices, DefaultVertexFormats.POSITION_TEX_NORMAL, true);
      Tessellator.getInstance().draw();
    }

    nw.requestPowerUpdate(cb, 20);

    HeadingText heading1 = HeadingText.STABLE;
    HeadingText heading2 = null;
    String text1;
    String text2 = "";

    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    float size = 0.15f * Math.min(info.width, info.height);
    float scale = size / fr.FONT_HEIGHT;
    float offset;

    if (info.height * 3 >= info.width * 4) {
      heading1 = HeadingText.INPUT;
      heading2 = HeadingText.OUTPUT;
      text1 = getChangeText(nw.getAverageInputPerTick(), fr);
      text2 = getChangeText(nw.getAverageOutputPerTick(), fr);
      offset = -size * 2.5f;
    } else {
      int change = Math.round(nw.getAverageChangePerTick());
      if (change > 0) {
        heading1 = HeadingText.GAIN;
      } else if (change < 0) {
        heading1 = HeadingText.LOSS;
      }
      text1 = getChangeText(change, fr);
      offset = -size;
    }

    EnumFacing right = dir.rotateAround(EnumFacing.Axis.Y);

    GlStateManager.pushMatrix();
    GlStateManager.translate((dir.getFrontOffsetX() * 1.02f) / 2 + 0.5f + right.getFrontOffsetX() * (info.width - 1) * 0.5f,
        1 + size * 0.5f - info.height * 0.5f, (dir.getFrontOffsetZ() * 1.02f) / 2 + 0.5f + right.getFrontOffsetZ() * (info.width - 1) * 0.5f);
    GlStateManager.rotate(-180, 1, 0, 0);
    if (dir == EnumFacing.NORTH) {
      GlStateManager.rotate(-180, 0, 1, 0);
    } else if (dir == EnumFacing.EAST) {
      GlStateManager.rotate(-90, 0, 1, 0);
    } else if (dir == EnumFacing.WEST) {
      GlStateManager.rotate(90, 0, 1, 0);
    }

    GlStateManager.enableLighting();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

    offset = drawText(heading1, text1, offset, scale, size, fr);
    if (heading2 != null) {
      drawText(heading2, text2, offset, scale, size, fr);
    }
    RenderUtil.bindBlockTexture();

    GlStateManager.disableBlend();
    GlStateManager.disablePolygonOffset();
    GlStateManager.popMatrix();
  }

  private float drawText(HeadingText heading, String text, float offset, float scale, float size, FontRenderer fr) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(0, offset, 0);
    GlStateManager.scale(scale, scale, scale);
    fr.drawString(heading.text, -fr.getStringWidth(heading.text) / 2, 0, 0);
    GlStateManager.popMatrix();
    offset += size * 1.5f;

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, offset, 0);
    GlStateManager.scale(scale, scale, scale);
    fr.drawString(text, -fr.getStringWidth(text) / 2, 0, heading.color);
    GlStateManager.popMatrix();
    offset += size * 1.5f;

    return offset;
  }

  protected String getChangeText(float average, FontRenderer fr) {
    int change = Math.round(Math.abs(average));
    String txt = PowerDisplayUtil.formatInteger(change);
    int width = fr.getStringWidth(txt);
    if (width > 38 && change > 1000) {
      change = change / 1000;
      txt = PowerDisplayUtil.formatInteger(change) + "K";
    }
    return txt;
  }

  static enum HeadingText {
    STABLE(ColorUtil.getRGB(0, 0, 0)),
    GAIN(ColorUtil.getRGB(0, 0.25f, 0)),
    LOSS(ColorUtil.getRGB(0.25f, 0, 0)),
    INPUT(ColorUtil.getRGB(0, 0.25f, 0)),
    OUTPUT(ColorUtil.getRGB(0.25f, 0, 0));

    final String text;
    final int color;

    private HeadingText(int color) {
      this.text = EnderIO.lang.localize("capbank.iodisplay.".concat(name().toLowerCase(Locale.US)));
      this.color = color;
    }
  }
}
