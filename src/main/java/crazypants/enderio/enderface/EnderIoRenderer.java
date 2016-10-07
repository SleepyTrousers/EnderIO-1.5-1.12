package crazypants.enderio.enderface;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Matrix4d;

import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockEnderIo;

@SideOnly(Side.CLIENT)
public class EnderIoRenderer extends ManagedTESR<TileEnderIO> {

  public EnderIoRenderer() {
    super(blockEnderIo.getBlock());
  }

  private TravelEntitySpecialRenderer<TileEnderIO> selectionRenderer = new TravelEntitySpecialRenderer<TileEnderIO>(blockEnderIo.getBlock()) {

  };

  @Override
  protected boolean shouldRender(@Nonnull TileEnderIO te, @Nonnull IBlockState blockState, int renderPass) {
    return renderPass == 0 || selectionRenderer.shouldRender(te, blockState, renderPass);
  }

  @Override
  protected void renderTileEntity(@Nonnull TileEnderIO te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {

    if (MinecraftForgeClient.getRenderPass() == 1) {
      selectionRenderer.renderTileEntity(te, blockState, partialTicks, destroyStage);
      return;
    }

    int brightness = 255;
    EntityLivingBase entityPlayer = Minecraft.getMinecraft().thePlayer;
    Matrix4d lookMat = RenderUtil.createBillboardMatrix(te, entityPlayer);

    GL11.glTranslated(0.5, 0.5, 0.5);

    TextureAtlasSprite tex = BlockEnderIO.enderEyeTex.get(TextureAtlasSprite.class);
    float minU = tex.getMinU();
    float maxU = tex.getMaxU();
    float minV = tex.getMinV();
    float maxV = tex.getMaxV();

    RenderUtil.renderBillboard(lookMat, minU, maxU, minV, maxV, 0.8, brightness);

    // Glint
    RenderUtil.bindGlintTexture();

    GlStateManager.depthFunc(GL11.GL_EQUAL);
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
    
    float maxUV = 32;    
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
    
    GlStateManager.depthFunc(GL11.GL_LEQUAL);
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GlStateManager.disableBlend();
  }

}
