package crazypants.enderio.enderface;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Matrix4d;

import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnderIoRenderer extends TileEntitySpecialRenderer<TileEntity> {

  private TravelEntitySpecialRenderer selectionRenderer = new TravelEntitySpecialRenderer() {

    @Override
    public TextureAtlasSprite getSelectedIcon() {
      return BlockEnderIO.selectedOverlayIcon.get(TextureAtlasSprite.class);
    }

    @Override
    public TextureAtlasSprite getHighlightIcon() {
      return BlockEnderIO.highlightOverlayIcon.get(TextureAtlasSprite.class);
    }

  };

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f, int breakingStage) {
    
    Matrix4d lookMat = null;
    int brightness = 255;
    if(te != null) {
      EntityLivingBase entityPlayer = Minecraft.getMinecraft().thePlayer;
      lookMat = RenderUtil.createBillboardMatrix(te, entityPlayer);
      //TODO: 1.9
      //brightness = RenderUtil.getTesselatorBrightness(entityPlayer.worldObj, te.getPos());
    } else {
      lookMat = new Matrix4d();
      lookMat.setIdentity();
    }
        
    render(x, y, z, lookMat, brightness);

    if(te != null) {
      selectionRenderer.renderTileEntityAt(te, x, y, z, f, breakingStage);
    }
  }

  public void render(double x, double y, double z, Matrix4d lookMat, int brightness) {
   
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

    TextureAtlasSprite tex = BlockEnderIO.enderEyeTex.get(TextureAtlasSprite.class);
    float minU = tex.getMinU();
    float maxU = tex.getMaxU();
    float minV = tex.getMinV();
    float maxV = tex.getMaxV();

    RenderUtil.bindBlockTexture();
    GL11.glColor3f(1, 1, 1);
    RenderUtil.renderBillboard(lookMat, minU, maxU, minV, maxV, 0.8, brightness);

    // Glint
    RenderUtil.bindGlintTexture();
    
    float maxUV = 32;
    GlStateManager.depthFunc(GL11.GL_EQUAL);        
    GlStateManager.enableLighting();
    GlStateManager.disableLighting();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);    
    float blendFactor = 1F;
    GlStateManager.color(0.5F * blendFactor, 0.25F * blendFactor, 0.8F * blendFactor, 1.0F);
    
    GlStateManager.matrixMode(GL11.GL_TEXTURE);
    GL11.glPushMatrix();
    float scale = 0.125F;
    GL11.glScalef(scale, scale, scale);
    float tans = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
    GL11.glTranslatef(tans, 0.0F, 0.0F);
    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
    RenderUtil.renderBillboard(lookMat, 0, maxUV, 0, maxUV, 0.8, brightness);
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glScalef(scale, scale, scale);
    tans = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
    GL11.glTranslatef(-tans, 0.0F, 0.0F);
    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
    RenderUtil.renderBillboard(lookMat, 0, maxUV, 0, maxUV, 0.8, brightness);
    GL11.glPopMatrix();

    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    GlStateManager.disableBlend();
    GlStateManager.enableLighting();    
    GlStateManager.depthFunc(GL11.GL_LEQUAL);

    GL11.glColor4f(1, 1, 1, 1.0F);
    GlStateManager.popMatrix();

  }

}
