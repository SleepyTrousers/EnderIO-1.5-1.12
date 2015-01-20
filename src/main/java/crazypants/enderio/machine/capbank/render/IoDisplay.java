package crazypants.enderio.machine.capbank.render;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class IoDisplay implements IInfoRenderer {

  @Override
  public void render(TileCapBank cb, ForgeDirection dir, double x, double y, double z, float partialTick) {
    if(dir.offsetY != 0) {
      return;
    }

    CapBankClientNetwork nw = (CapBankClientNetwork) cb.getNetwork();
    if(nw == null) {
      return;
    }

    CapBankClientNetwork.IOInfo info = nw.getIODisplayInfo(cb.xCoord, cb.yCoord, cb.zCoord, dir);
    if(info.isInside()) {
      return;
    }

    boolean selfIlum = true;
    int brightness = 0;
    if(!selfIlum) {
      brightness = cb.getWorldObj().getLightBrightnessForSkyBlocks(cb.xCoord + dir.offsetX, cb.yCoord + dir.offsetY, cb.zCoord + dir.offsetZ, 0);
      int l1 = brightness % 65536;
      int l2 = brightness / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
    } else {
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    }

    boolean drawBackground = true;
    if(drawBackground) {
      RenderUtil.bindBlockTexture();

      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();
      if(!selfIlum) {
        tes.setBrightness(brightness);
      }
      tes.setColorOpaque_F(1, 1, 1);

      float scale = 0.85f;
      float offset = (1-scale)/2;
      IIcon icon = EnderIO.blockCapBank.getInfoPanelIcon();
      float minU = icon.getMinU();
      float maxU = icon.getMaxU();
      float minV = icon.getMinV();
      float maxV = icon.getMaxV();

      switch (dir) {
        case NORTH: {
          float y0 = offset - (info.height-1);
          float y1 = 1 - offset;
          float x0 = offset;
          float x1 = info.width - offset;
          float z0 = 0;
          tes.setNormal(0, 0, -1);
          tes.addVertexWithUV(x1, y0, z0, minU, minV);
          tes.addVertexWithUV(x0, y0, z0, maxU, minV);
          tes.addVertexWithUV(x0, y1, z0, maxU, maxV);
          tes.addVertexWithUV(x1, y1, z0, minU, maxV);
          break;
        }

        case SOUTH: {
          float y0 = offset - (info.height-1);
          float y1 = 1 - offset;
          float x0 = offset - (info.width-1);
          float x1 = 1 - offset;
          float z1 = 1;
          tes.setNormal(0, 0, 1);
          tes.addVertexWithUV(x0, y0, z1, maxU, minV);
          tes.addVertexWithUV(x1, y0, z1, minU, minV);
          tes.addVertexWithUV(x1, y1, z1, minU, maxV);
          tes.addVertexWithUV(x0, y1, z1, maxU, maxV);
          break;
        }

        case EAST: {
          float y0 = offset - (info.height-1);
          float y1 = 1 - offset;
          float z0 = offset;
          float z1 = info.width - offset;
          float x1 = 1;
          tes.setNormal(1, 0, 0);
          tes.addVertexWithUV(x1, y1, z0, maxU, maxV);
          tes.addVertexWithUV(x1, y1, z1, minU, maxV);
          tes.addVertexWithUV(x1, y0, z1, minU, minV);
          tes.addVertexWithUV(x1, y0, z0, maxU, minV);
          break;
        }

        case WEST: {
          float y0 = offset - (info.height-1);
          float y1 = 1 - offset;
          float z0 = offset - (info.width-1);
          float z1 = 1 - offset;
          float x0 = 0;
          tes.setNormal(-1, 0, 0);
          tes.addVertexWithUV(x0, y0, z0, maxU, minV);
          tes.addVertexWithUV(x0, y0, z1, minU, minV);
          tes.addVertexWithUV(x0, y1, z1, minU, maxV);
          tes.addVertexWithUV(x0, y1, z0, maxU, maxV);
          break;
        }

        default:
          throw new AssertionError();
      }

      tes.draw();
    }

    nw.requestPowerUpdate(cb, 20);

    int change = Math.round(nw.getAverageChangePerTick());
    HeadingText heading = HeadingText.STABLE;
    if(change > 0) {
      heading = HeadingText.GAIN;
    } else if(change < 0) {
      heading = HeadingText.LOSS;
    }

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    String changeText = getChangeText(change, fr);

    float size = 0.15f * Math.min(info.width, info.height);
    float scale = size / fr.FONT_HEIGHT;

    ForgeDirection right = dir.getRotation(ForgeDirection.UP);

    GL11.glPushMatrix();
    GL11.glTranslatef(
            (dir.offsetX * 1.02f)/2 + 0.5f + right.offsetX*(info.width-1)*0.5f,
            1 + size*0.5f - info.height*0.5f,
            (dir.offsetZ * 1.02f)/2 + 0.5f + right.offsetZ*(info.width-1)*0.5f);
    GL11.glRotatef(-180, 1, 0, 0);
    if(dir == ForgeDirection.NORTH) {
      GL11.glRotatef(-180, 0, 1, 0);
    } else if(dir == ForgeDirection.EAST) {
      GL11.glRotatef(-90, 0, 1, 0);
    } else if(dir == ForgeDirection.WEST) {
      GL11.glRotatef(90, 0, 1, 0);
    }

    GL11.glPushMatrix();
    GL11.glTranslatef(0, -size, 0);
    GL11.glScalef(scale, scale, scale);
    fr.drawString(heading.text, -fr.getStringWidth(heading.text) / 2, 0, 0);
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslatef(0, size / 2, 0);
    GL11.glScalef(scale, scale, scale);
    fr.drawString(changeText, -fr.getStringWidth(changeText) / 2, 0, heading.color);
    GL11.glPopMatrix();

    GL11.glPopMatrix();

  }

  protected String getChangeText(int change, FontRenderer fr) {
    change = Math.abs(change);
    String txt = PowerDisplayUtil.INT_NF.format(change);
    int width = fr.getStringWidth(txt);
    if(width > 38 && change > 1000) {
      change = change / 1000;
      txt = PowerDisplayUtil.INT_NF.format(change) + "K";
    }
    return txt;
  }

  static enum HeadingText {
    STABLE(ColorUtil.getRGB(0, 0, 0)),
    GAIN(ColorUtil.getRGB(0, 0.25f, 0)),
    LOSS(ColorUtil.getRGB(0.25f, 0, 0));

    final String text;
    final int color;

    private HeadingText(int color) {
      this.text = Lang.localize("capbank.iodisplay.".concat(name().toLowerCase(Locale.ENGLISH)));
      this.color = color;
    }
  }
}
