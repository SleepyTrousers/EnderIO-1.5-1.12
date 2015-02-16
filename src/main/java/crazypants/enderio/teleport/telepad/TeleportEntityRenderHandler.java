package crazypants.enderio.teleport.telepad;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.render.RenderUtil;

public class TeleportEntityRenderHandler {

  private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  @SubscribeEvent
  public void onEntityRender(RenderLivingEvent.Post event) {
    EntityLivingBase e = event.entity;
    float radius = e.width / 2.0F;
    AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(-radius, 0, -radius, radius, e.height + 0.25, radius);

    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    GL11.glDepthMask(false);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glTranslated(event.x, event.y, event.z);
    bb = bb.expand(0.25, 0, 0.25);
    GL11.glRotatef((e.ticksExisted + RenderUtil.getTimer().renderPartialTicks) * 4, 0, 1, 0);
    if(event.entity.getEntityData().getBoolean(TileTelePad.TELEPORTING_KEY)) {
      RenderGlobal.drawOutlinedBoundingBox(bb, 0xAA55FF);
    }
    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }
}
