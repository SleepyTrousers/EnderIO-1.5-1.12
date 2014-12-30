package crazypants.enderio.machine.capbank.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.render.BoundingBox;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class IoDisplay implements IInfoRenderer {

  @Override
  public void render(TileCapBank cb, ForgeDirection dir, double x, double y, double z, float partialTick) {

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

      double scale = 0.85;
      BoundingBox bb = BoundingBox.UNIT_CUBE;
      double xScale = dir.offsetX == 0 ? scale : 1;
      double yScale = scale;
      double zScale = dir.offsetZ == 0 ? scale : 1;
      bb = bb.scale(xScale, yScale, zScale);

      IIcon icon = EnderIO.blockCapBank.getInfoPanelIcon();
      List<Vertex> verts = bb.getCornersWithUvForFace(dir, icon.getMinU(), icon.getMaxU(), icon.getMinV(), icon.getMaxV());
      RenderUtil.addVerticesToTesselator(verts);

      tes.draw();
    }

    CapBankClientNetwork nw = null;
    if(cb.getNetwork() != null) {
      nw = (CapBankClientNetwork) cb.getNetwork();
      nw.requestPowerUpdate(cb, 20);
    } else {
      return;
    }

    int change = Math.round(nw.getAverageChangePerTick());
    //change = 0;
    Vector3f col = new Vector3f(0, 0, 0);
    if(change > 0) {
      col = new Vector3f(0, 0.025, 0);
    } else if(change < 0) {
      col = new Vector3f(0.05, 0, 0);
    }

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    String changeText = getChangeText(change, fr);

    String HeadingText = change > 0 ? "In" : change < 0 ? "Out" : "I/O";
    float size = 0.15f;
    float scale = size / fr.FONT_HEIGHT;

    GL11.glPushMatrix();
    GL11.glTranslatef(0.5f, 0.5f + (size / 2), 0.5f);
    GL11.glTranslatef((float) (dir.offsetX * 1.01) / 2, 0, (float) (dir.offsetZ * 1.01) / 2);
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
    fr.drawString(HeadingText, -fr.getStringWidth(HeadingText) / 2, 0, ColorUtil.getRGB(col));
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslatef(0, size / 2, 0);
    GL11.glScalef(scale, scale, scale);
    fr.drawString(changeText, -fr.getStringWidth(changeText) / 2, 0, ColorUtil.getRGB(col));
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

}
