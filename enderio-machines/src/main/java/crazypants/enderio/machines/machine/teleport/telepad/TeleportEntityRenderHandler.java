package crazypants.enderio.machines.machine.teleport.telepad;

import org.lwjgl.opengl.GL11;

import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector4i;

import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.TelePadConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ZERO;

@EventBusSubscriber(modid = EnderIOMachines.MODID, value = Side.CLIENT)
public class TeleportEntityRenderHandler {

  private static final Vector4i COL_TOP = new Vector4i(220, 255, 255, 0);
  private static final Vector4i COL_BOT = new Vector4i(175, 255, 255, 100);

  /*
   * gl flags when this is called: GL_ALPHA_TEST GL_COLOR_MATERIAL GL_CULL_FACE GL_DEPTH_TEST GL_DITHER GL_FOG GL_LIGHTING GL_TEXTURE_2D
   */

  @SubscribeEvent
  public static void onEntityRender(RenderLivingEvent.Post<EntityLivingBase> event) {
    EntityLivingBase e = event.getEntity();
    if (e.getEntityData().getBoolean("eio_needs_pop")) {
      GlStateManager.popMatrix();
      e.getEntityData().removeTag("eio_needs_pop");
    }
    if (e.getEntityData().getBoolean(TileTelePad.TELEPORTING_KEY)) {
      final float progress = e.getEntityData().getFloat(TileTelePad.PROGRESS_KEY);
      final float speed = progress * 1.2f;
      final float rot = (e.getEntityData().getFloat("eio_teleportrotation")) + speed;
      e.getEntityData().setFloat("eio_teleportrotation", rot);

      AxisAlignedBB bb = e.getRenderBoundingBox();
      if (NullHelper.untrust(bb) == null || bb.getAverageEdgeLength() < .2) {
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
      GlStateManager.rotate(rot + Minecraft.getMinecraft().getRenderPartialTicks() + e.ticksExisted, 0, 1, 0);

      Tessellator tes = Tessellator.getInstance();
      BufferBuilder vertexBuffer = tes.getBuffer();
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

  @SubscribeEvent
  public static void onEntityRender(RenderLivingEvent.Pre<EntityLivingBase> event) {
    if (TelePadConfig.telepadShrinkEffect.get()) {
      final NBTTagCompound entityData = event.getEntity().getEntityData();
      boolean isTarget = false;
      if (entityData.getBoolean(TileTelePad.TELEPORTING_KEY)) {
        isTarget = true;
        // shrink the entity for longer than the teleport lasts so it doesn't pop big again at the source location
        entityData.setInteger("eio_delay", 7);
      } else if (entityData.hasKey("eio_delay")) {
        int delay = entityData.getInteger("eio_delay") - 1;
        if (delay > 0) {
          entityData.setInteger("eio_delay", delay);
        } else {
          entityData.removeTag("eio_delay");
          entityData.removeTag("eio_teleportrotation");
        }
        isTarget = true;
      }
      if (isTarget) {
        final float progress = entityData.hasKey(TileTelePad.PROGRESS_KEY) ? entityData.getFloat(TileTelePad.PROGRESS_KEY) : 1;
        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX(), event.getY(), event.getZ());
        final float scale = 1 - progress * 0.75f;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());
        entityData.setBoolean("eio_needs_pop", true);
      }
    }
  }
}
