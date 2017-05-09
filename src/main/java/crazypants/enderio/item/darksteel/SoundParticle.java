package crazypants.enderio.item.darksteel;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.item.darksteel.SoundDetector.SoundSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SoundParticle extends Particle {

  private final SoundSource ss;

  public SoundParticle(World worldIn, SoundSource ss) {
    super(worldIn, ss.pos.x, ss.pos.y, ss.pos.z, 0.0D, 0.0D, 0.0D);
    this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
    this.motionX = this.motionY = this.motionZ = 0.0D;
    this.particleGravity = 0.0F;
    this.particleMaxAge = Config.darkSteelSoundLocatorLifespan;
    this.ss = ss;
  }

  // layer 3: we can use our own texture but also have to set up GL ourself and start/end drawing
  @Override
  public int getFXLayer() {
    return 3;
  }

  @Override
  public void renderParticle(VertexBuffer worldRendererIn, Entity player, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {

    final IconEIO icon = IconEIO.SOUND;
    double minU = (double) icon.getX() / IconEIO.map.getSize();
    double maxU = (double) (icon.getX() + icon.getWidth()) / IconEIO.map.getSize();
    double minV = (double) icon.getY() / IconEIO.map.getSize();
    double maxV = (double) (icon.getY() + icon.getHeight()) / IconEIO.map.getSize();

    float ageScale = (particleAge + partialTicks) / particleMaxAge;
    ageScale = (1 - ageScale * ageScale) * 1.03f;
    if (ageScale > 1) {
      ageScale = 2 - ageScale;
      ageScale = ageScale * ageScale;
    }
    float volumeScale = MathHelper.clamp(ss.volume / 3f + .5f, .5f, 1f);
    float scale = 0.5F * ageScale * volumeScale;


    float alpha = MathHelper.clamp((ss.volume + 1) / 3f, .6f, .4f);
    float brightness = 1.0f;

    float x1 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
    float y1 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
    float z1 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

    GlStateManager.pushMatrix();
    GlStateManager.enableLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    Minecraft.getMinecraft().getTextureManager().bindTexture(IconEIO.TEXTURE);

    worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    worldRendererIn.pos(x1 - rotX * scale - rotXY * scale, y1 - rotZ * scale, z1 - rotYZ * scale - rotXZ * scale).tex(maxU, maxV)
        .color(brightness, brightness, brightness, alpha).endVertex();
    worldRendererIn.pos(x1 - rotX * scale + rotXY * scale, y1 + rotZ * scale, z1 - rotYZ * scale + rotXZ * scale).tex(maxU, minV)
        .color(brightness, brightness, brightness, alpha).endVertex();
    worldRendererIn.pos(x1 + rotX * scale + rotXY * scale, y1 + rotZ * scale, z1 + rotYZ * scale + rotXZ * scale).tex(minU, minV)
        .color(brightness, brightness, brightness, alpha).endVertex();
    worldRendererIn.pos(x1 + rotX * scale - rotXY * scale, y1 - rotZ * scale, z1 + rotYZ * scale - rotXZ * scale).tex(minU, maxV)
        .color(brightness, brightness, brightness, alpha).endVertex();
    Tessellator.getInstance().draw();

    GlStateManager.disableBlend();
    GlStateManager.enableDepth();
    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
  }

}
