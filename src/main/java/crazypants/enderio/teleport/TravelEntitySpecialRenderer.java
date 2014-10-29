package crazypants.enderio.teleport;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vector4f;

public class TravelEntitySpecialRenderer extends TileEntitySpecialRenderer {

  private final Vector4f selectedColor;
  private final Vector4f highlightColor;

  public TravelEntitySpecialRenderer() {
    this(new Vector4f(1, 0.25f, 0, 0.5f), new Vector4f(1, 1, 1, 0.25f));
  }

  public TravelEntitySpecialRenderer(Vector4f selectedColor, Vector4f highlightColor) {
    this.selectedColor = selectedColor;
    this.highlightColor = highlightColor;
  }

  @Override
  public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

    if(!TravelController.instance.showTargets()) {
      return;
    }

    ITravelAccessable ta = (ITravelAccessable) tileentity;
    if(!ta.canSeeBlock(Minecraft.getMinecraft().thePlayer)) {
      return;
    }

    Vector3d eye = Util.getEyePositionEio(Minecraft.getMinecraft().thePlayer);
    Vector3d loc = new Vector3d(tileentity.xCoord + 0.5, tileentity.yCoord + 0.5, tileentity.zCoord + 0.5);
    double maxDistance = TravelController.instance.isTravelItemActive(Minecraft.getMinecraft().thePlayer) ? TravelSource.STAFF.maxDistanceTravelledSq
        : TravelSource.BLOCK.maxDistanceTravelledSq;
    if(eye.distanceSquared(loc) > maxDistance) {
      return;
    }

    double sf = TravelController.instance.getScaleForCandidate(loc);

    BlockCoord bc = new BlockCoord(tileentity);
    TravelController.instance.addCandidate(bc);

    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    RenderUtil.bindBlockTexture();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);

    GL11.glEnable(GL12.GL_RESCALE_NORMAL);

    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glColor3f(1, 1, 1);

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);


    Tessellator.instance.startDrawingQuads();
    renderBlock(sf);
    Tessellator.instance.draw();

    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.setBrightness(15 << 20 | 15 << 4);
    if(TravelController.instance.isBlockSelected(bc)) {
      Tessellator.instance.setColorRGBA_F(selectedColor.x, selectedColor.y, selectedColor.z, selectedColor.w);
      CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(sf + 0.05, sf + 0.05, sf + 0.05), getSelectedIcon());
    } else {
      Tessellator.instance.setColorRGBA_F(highlightColor.x, highlightColor.y, highlightColor.z, highlightColor.w);
      CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(sf + 0.05, sf + 0.05, sf + 0.05), getHighlightIcon());
    }
    Tessellator.instance.draw();
    GL11.glPopMatrix();

    
    renderLabel(tileentity, x, y, z, ta, sf);

    GL11.glPopAttrib();
    GL11.glPopAttrib();

    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

  }

  private void renderLabel(TileEntity tileentity, double x, double y, double z, ITravelAccessable ta, double sf) {
    float globalScale = (float) sf;
    ItemStack itemLabel = ta.getItemLabel();
    if(itemLabel != null && itemLabel.getItem() != null) {

      boolean isBlock = false;
      Block block = Block.getBlockFromItem(itemLabel.getItem());
      if(block != null && block != Blocks.air) {
        isBlock = true;
      }

      float alpha = 0.5f;
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR);
      float col = 0.5f;
      GL14.glBlendColor(col, col, col, col);
      GL11.glColor4f(1, 1, 1, 1);

      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
        if(!isBlock) {
          GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
          GL11.glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);
        }

        {
          GL11.glPushMatrix();
          GL11.glScalef(globalScale, globalScale, globalScale);

          {
            GL11.glPushMatrix();
            if(isBlock) {
              GL11.glTranslatef(0f, -0.25f, 0);
            } else {
              GL11.glTranslatef(0f, -0.5f, 0);
            }
            GL11.glScalef(2, 2, 2);
            EntityItem ei = new EntityItem(tileentity.getWorldObj(), x, y, z, itemLabel);
            ei.age = 0;
            ei.hoverStart = 0;
            RenderManager.instance.getEntityRenderObject(ei).doRender(ei, 0, 0, 0, 0, 0);
            GL11.glPopMatrix();
          }

          GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
      }

      String toRender = ta.getLabel();
      if(toRender != null && toRender.trim().length() > 0) {
        Vector4f bgCol = RenderUtil.DEFAULT_TEXT_BG_COL;
        if(TravelController.instance.isBlockSelected(new BlockCoord(tileentity))) {
          bgCol = new Vector4f(selectedColor.x, selectedColor.y, selectedColor.z, selectedColor.w);
        }
        
        {
          GL11.glPushMatrix();
          GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
          {
            GL11.glPushMatrix();
            GL11.glScalef(globalScale, globalScale, globalScale);
            Vector3f pos = new Vector3f(0, 1.2f, 0);
            float size = 0.5f;
            RenderUtil.drawBillboardedText(pos, toRender, size, bgCol);
            GL11.glPopMatrix();
          }
          GL11.glPopMatrix();
        }
      }
    }
  }

  protected void renderBlock(double sf) {
    Tessellator.instance.setColorRGBA_F(1, 1, 1, 0.75f);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(sf, sf, sf), EnderIO.blockTravelPlatform.getIcon(0, 0));
  }

  public Vector4f getSelectedColor() {
    return selectedColor;
  }

  public IIcon getSelectedIcon() {
    return EnderIO.blockTravelPlatform.selectedOverlayIcon;
  }

  public Vector4f getHighlightColor() {
    return highlightColor;
  }

  public IIcon getHighlightIcon() {
    return EnderIO.blockTravelPlatform.highlightOverlayIcon;
  }

}
