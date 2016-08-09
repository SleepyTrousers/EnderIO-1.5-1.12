package crazypants.enderio.teleport.telepad;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4i;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ZERO;

public class TeleportEntityRenderHandler {

  private static final Vector4i COL_TOP = new Vector4i(220, 255, 255, 0);
  private static final Vector4i COL_BOT = new Vector4i(175, 255, 255, 100);

  /*
   * gl flags when this is called: GL_ALPHA_TEST GL_COLOR_MATERIAL GL_CULL_FACE GL_DEPTH_TEST GL_DITHER GL_FOG GL_LIGHTING GL_TEXTURE_2D
   */

  @SubscribeEvent
  public void onEntityRender(RenderLivingEvent.Post<EntityLivingBase> event) {
    EntityLivingBase e = event.getEntity();
    if (e.getEntityData().getBoolean(TileTelePad.TELEPORTING_KEY)) {
      final Timer timer = RenderUtil.getTimer();
      final float progress = e.getEntityData().getFloat(TileTelePad.PROGRESS_KEY);
      final float speed = progress * 1.2f;
      final float rot = (e.getEntityData().getFloat("teleportrotation")) + speed;
      e.getEntityData().setFloat("teleportrotation", rot);

      AxisAlignedBB bb = e.getRenderBoundingBox();
      if (bb == null || bb.getAverageEdgeLength() < .2) {
        float radius = e.width / 2.0F;
        bb = new AxisAlignedBB(-radius, 0, -radius, radius, e.height, radius).offset(e.posX, e.posY, e.posZ);
      }
      bb = bb.setMaxY(bb.maxY + 1.25 - progress / 2).expand(0.5 - progress / 5, 0, 0.5 - progress / 5);

      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GlStateManager.shadeModel(GL11.GL_SMOOTH);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ZERO, GL_ONE);
      GlStateManager.disableAlpha();
      GlStateManager.disableCull();
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      GlStateManager.translate(event.getX(), event.getY(), event.getZ());
      GlStateManager.rotate(rot + (timer == null ? 0 : timer.renderPartialTicks) + e.ticksExisted, 0, 1, 0);

      Tessellator tes = Tessellator.getInstance();
      VertexBuffer vertexBuffer = tes.getBuffer();
      vertexBuffer.setTranslation(-e.posX, -e.posY, -e.posZ);

      vertexBuffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);

      vertexBuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(COL_TOP.x, COL_TOP.y, COL_TOP.z, COL_TOP.w).endVertex();
      vertexBuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(COL_BOT.x, COL_BOT.y, COL_BOT.z, COL_BOT.w).endVertex();

      vertexBuffer.pos(bb.minX, bb.maxY, bb.maxZ).color(COL_TOP.x, COL_TOP.y, COL_TOP.z, COL_TOP.w).endVertex();
      vertexBuffer.pos(bb.minX, bb.minY, bb.maxZ).color(COL_BOT.x, COL_BOT.y, COL_BOT.z, COL_BOT.w).endVertex();

      vertexBuffer.pos(bb.minX, bb.maxY, bb.minZ).color(COL_TOP.x, COL_TOP.y, COL_TOP.z, COL_TOP.w).endVertex();
      vertexBuffer.pos(bb.minX, bb.minY, bb.minZ).color(COL_BOT.x, COL_BOT.y, COL_BOT.z, COL_BOT.w).endVertex();

      vertexBuffer.pos(bb.maxX, bb.maxY, bb.minZ).color(COL_TOP.x, COL_TOP.y, COL_TOP.z, COL_TOP.w).endVertex();
      vertexBuffer.pos(bb.maxX, bb.minY, bb.minZ).color(COL_BOT.x, COL_BOT.y, COL_BOT.z, COL_BOT.w).endVertex();

      vertexBuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(COL_TOP.x, COL_TOP.y, COL_TOP.z, COL_TOP.w).endVertex();
      vertexBuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(COL_BOT.x, COL_BOT.y, COL_BOT.z, COL_BOT.w).endVertex();
      
      tes.draw();

      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableCull();
      GlStateManager.enableLighting();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
    }
  }

}
