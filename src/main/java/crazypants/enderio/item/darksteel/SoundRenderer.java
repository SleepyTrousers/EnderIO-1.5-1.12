package crazypants.enderio.item.darksteel;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.gui.IconEIO;
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

    SoundEntity se = ((SoundEntity) entity);
    
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    
    //float alpha = Math.min(1, se.volume) * 0.5f;
    float alpha = 0.5f;
    
    GL11.glColor4f(1, 1, 1, alpha);

    float scale = se.lifeSpan / 20f;

    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);
    
    double size = 0.5 * se.lifeSpan / 20f;
    
    IconEIO.SOUND.renderIcon(-size/2, -size/2, size,size,0,true,true);

    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }

}
