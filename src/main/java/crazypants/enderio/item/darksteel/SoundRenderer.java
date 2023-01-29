package crazypants.enderio.item.darksteel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.gui.IconEIO;

@SideOnly(Side.CLIENT)
public class SoundRenderer extends RenderEntity {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {

        SoundEntity se = ((SoundEntity) entity);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

        float alpha = 0.5f;

        GL11.glColor4f(1, 1, 1, alpha);

        float scale = se.lifeSpan / 20f;

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);

        double size = 0.5 * se.lifeSpan / 20f;

        IconEIO.map.render(IconEIO.SOUND, -size / 2, -size / 2, size, size, 0, true, true);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
