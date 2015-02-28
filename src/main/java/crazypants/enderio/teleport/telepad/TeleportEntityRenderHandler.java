package crazypants.enderio.teleport.telepad;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.render.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

public class TeleportEntityRenderHandler {

  @SubscribeEvent
  public void onEntityRender(RenderLivingEvent.Post event) {
    
    EntityLivingBase e = event.entity;
    
    if(e.getEntityData().getBoolean(TileTelePad.TELEPORTING_KEY)) {

      float radius = e.width / 2.0F;
      AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(-radius, 0, -radius, radius, e.height + 0.25, radius);

      glPushMatrix();
      glPushAttrib(GL_ALL_ATTRIB_BITS);
      glDisable(GL_TEXTURE_2D);
      glShadeModel(GL_SMOOTH);
      glEnable(GL_BLEND);
      OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE, GL_ZERO, GL_ONE);
      glDisable(GL_ALPHA_TEST);
      glEnable(GL_CULL_FACE);
      glDisable(GL_LIGHTING);
      glDepthMask(false);
      glTranslated(event.x, event.y, event.z);
      bb = bb.expand(0.5, 0, 0.5);
      float speed = (1 - (e.getEntityData().getFloat(TileTelePad.PROGRESS_KEY) * 0.7f)) * 100;
      float rot = ((e.ticksExisted + RenderUtil.getTimer().renderPartialTicks) % speed) * (360 / speed);
      glRotatef(rot, 0, 1, 0);

      double yMax = bb.maxY + 1;

      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();

      colorBot(tes);
      tes.addVertex(bb.minX, bb.minY, bb.minZ);
      tes.addVertex(bb.minX, bb.minY, bb.maxZ);
      colorTop(tes);
      tes.addVertex(bb.minX, yMax, bb.maxZ);
      tes.addVertex(bb.minX, yMax, bb.minZ);
      colorBot(tes);
      tes.addVertex(bb.minX, bb.minY, bb.maxZ);
      tes.addVertex(bb.minX, bb.minY, bb.minZ);
      colorTop(tes);
      tes.addVertex(bb.minX, yMax, bb.minZ);
      tes.addVertex(bb.minX, yMax, bb.maxZ);
      
      colorBot(tes);
      tes.addVertex(bb.maxX, bb.minY, bb.minZ);
      tes.addVertex(bb.maxX, bb.minY, bb.maxZ);
      colorTop(tes);
      tes.addVertex(bb.maxX, yMax, bb.maxZ);
      tes.addVertex(bb.maxX, yMax, bb.minZ);
      colorBot(tes);
      tes.addVertex(bb.maxX, bb.minY, bb.maxZ);
      tes.addVertex(bb.maxX, bb.minY, bb.minZ);
      colorTop(tes);
      tes.addVertex(bb.maxX, yMax, bb.minZ);
      tes.addVertex(bb.maxX, yMax, bb.maxZ);
      
      colorBot(tes);
      tes.addVertex(bb.minX, bb.minY, bb.minZ);
      tes.addVertex(bb.maxX, bb.minY, bb.minZ);
      colorTop(tes);
      tes.addVertex(bb.maxX, yMax, bb.minZ);
      tes.addVertex(bb.minX, yMax, bb.minZ);
      colorBot(tes);
      tes.addVertex(bb.maxX, bb.minY, bb.minZ);
      tes.addVertex(bb.minX, bb.minY, bb.minZ);
      colorTop(tes);
      tes.addVertex(bb.minX, yMax, bb.minZ);
      tes.addVertex(bb.maxX, yMax, bb.minZ);
      
      colorBot(tes);
      tes.addVertex(bb.minX, bb.minY, bb.maxZ);
      tes.addVertex(bb.maxX, bb.minY, bb.maxZ);
      colorTop(tes);
      tes.addVertex(bb.maxX, yMax, bb.maxZ);
      tes.addVertex(bb.minX, yMax, bb.maxZ);
      colorBot(tes);
      tes.addVertex(bb.maxX, bb.minY, bb.maxZ);
      tes.addVertex(bb.minX, bb.minY, bb.maxZ);
      colorTop(tes);
      tes.addVertex(bb.minX, yMax, bb.maxZ);
      tes.addVertex(bb.maxX, yMax, bb.maxZ);

      tes.draw();

      GL11.glPopAttrib();
      GL11.glPopMatrix();
    }
  }

  private void colorBot(Tessellator tes) {
    tes.setColorRGBA(100, 100, 255, 100);
  }

  private void colorTop(Tessellator tes) {
    tes.setColorRGBA(150, 150, 255, 0);
  }
}
