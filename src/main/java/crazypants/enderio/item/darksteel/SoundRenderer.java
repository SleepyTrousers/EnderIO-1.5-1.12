package crazypants.enderio.item.darksteel;

import org.lwjgl.opengl.GL11;

import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.IconUtil;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class SoundRenderer extends RenderEntity {

  public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glColor4f(1, 1, 1, 0.5f);

    float scale = ((SoundEntity) entity).lifeSpan / 20f;

    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);
    
    GL11.glPushMatrix();
    GL11.glRotatef(-90, 1, 0, 0);
    
    RenderUtil.bindBlockTexture();
    IIcon tex = IconUtil.whiteTexture;
    double size = 0.25;
    
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.addVertexWithUV(-size, 0, size, tex.getMinU(), tex.getMinV());
    tessellator.addVertexWithUV(-size, 0, -size, tex.getMinU(), tex.getMaxV());
    tessellator.addVertexWithUV(size, 0, -size, tex.getMaxU(), tex.getMaxV());
    tessellator.addVertexWithUV(size, 0, size, tex.getMaxU(), tex.getMinV());

    tessellator.addVertexWithUV(-size, 0, size, tex.getMinU(), tex.getMinV());
    tessellator.addVertexWithUV(size, 0, size, tex.getMaxU(), tex.getMinV());
    tessellator.addVertexWithUV(size, 0, -size, tex.getMaxU(), tex.getMaxV());
    tessellator.addVertexWithUV(-size, 0, -size, tex.getMinU(), tex.getMaxV());
    tessellator.draw();
    
    GL11.glPopMatrix();

    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }

}
