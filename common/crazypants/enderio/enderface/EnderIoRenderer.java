package crazypants.enderio.enderface;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.teleport.TravelEntitySpecialRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector3d;

public class EnderIoRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private TravelEntitySpecialRenderer selectionRenderer = new TravelEntitySpecialRenderer() {
    protected void renderBlock() {
    }

    @Override
    public Icon getSelectedIcon() {
      return EnderIO.blockEnderIo.selectedOverlayIcon;
    }

    @Override
    public Icon getHighlightIcon() {
      return EnderIO.blockEnderIo.highlightOverlayIcon;
    }

  };

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
    EntityLivingBase entityPlayer = tileEntityRenderer.entityLivingPlayer;

    Vector3d playerEye = new Vector3d(entityPlayer.posX, entityPlayer.posY + 1.62 - entityPlayer.yOffset, entityPlayer.posZ);
    Vector3d blockOrigin = new Vector3d(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5);
    Matrix4d lookMat = VecmathUtil.createMatrixAsLookAt(blockOrigin, playerEye, RenderUtil.UP_V);
    lookMat.setTranslation(new Vector3d());
    lookMat.invert();

    int brightness = RenderUtil.setTesselatorBrightness(tileEntityRenderer.worldObj, te.xCoord, te.yCoord, te.zCoord);
    render(x, y, z, lookMat, brightness);

    selectionRenderer.renderTileEntityAt(te, x, y, z, f);
  }

  public void render(double x, double y, double z, Matrix4d lookMat, int brightness) {

    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

    Icon tex = Item.eyeOfEnder.getIconFromDamage(0);
    RenderUtil.bindItemTexture();
    float minU = tex.getMinU();
    float maxU = tex.getMaxU();
    float minV = tex.getMinV();
    float maxV = tex.getMaxV();

    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glColor3f(1, 1, 1);
    renderBillboard(lookMat, minU, maxU, minV, maxV, 0.8, brightness);

    // Glint
    float maxUV = 32;
    GL11.glDepthFunc(GL11.GL_EQUAL);
    GL11.glDisable(GL11.GL_LIGHTING);
    RenderUtil.bindGlintTexture();
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
    float blendFactor = 1F;
    GL11.glColor4f(0.5F * blendFactor, 0.25F * blendFactor, 0.8F * blendFactor, 1.0F);

    GL11.glMatrixMode(GL11.GL_TEXTURE);
    GL11.glPushMatrix();
    float scale = 0.125F;
    GL11.glScalef(scale, scale, scale);
    float tans = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
    GL11.glTranslatef(tans, 0.0F, 0.0F);
    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
    renderBillboard(lookMat, 0, maxUV, 0, maxUV, 0.8, brightness);
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glScalef(scale, scale, scale);
    tans = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
    GL11.glTranslatef(-tans, 0.0F, 0.0F);
    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
    renderBillboard(lookMat, 0, maxUV, 0, maxUV, 0.8, brightness);
    GL11.glPopMatrix();

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glDepthFunc(GL11.GL_LEQUAL);

    GL11.glColor4f(1, 1, 1, 1.0F);
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);

    RenderUtil.bindBlockTexture();
    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.setBrightness(brightness);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    CubeRenderer.render(BoundingBox.UNIT_CUBE, EnderIO.blockEnderIo.frameIcon);
    Tessellator.instance.draw();
    GL11.glPopMatrix();

    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glDisable(GL11.GL_BLEND);

  }

  private void renderBillboard(Matrix4d lookMat, float minU, float maxU, float minV, float maxV, double size, int brightness) {

    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    tes.setBrightness(brightness);

    double s = size / 2;
    Vector3d v = new Vector3d();
    v.set(-s, s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, minU, maxV);
    v.set(s, s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, maxU, maxV);
    v.set(s, -s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, maxU, minV);
    v.set(-s, -s, 0);
    lookMat.transform(v);
    tes.addVertexWithUV(v.x, v.y, v.z, minU, minV);
    tes.draw();
  }

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    Matrix4d lookMat = new Matrix4d();
    lookMat.setIdentity();

    lookMat = VecmathUtil.createMatrixAsLookAt(RenderUtil.ZERO_V, new Vector3d(1, 0, 0), RenderUtil.UP_V);

    render(0, 0, 0, lookMat, 0xF000F0);

  }

}
