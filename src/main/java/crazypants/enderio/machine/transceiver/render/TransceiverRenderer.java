package crazypants.enderio.machine.transceiver.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransceiverRenderer extends TileEntitySpecialRenderer<TileTransceiver> {

  private IModelTrans model;

  private BoundingBox bb;

  public TransceiverRenderer() {
    float scale = 0.7f;
    if(Config.useAlternateTesseractModel) {
      model = new TransceiverModelAlt();
      scale = 0.8f;
    } else {
      model = new TransceiverModel();
    }
    bb = BoundingBox.UNIT_CUBE.scale(scale, scale, scale);
  }

  @Override
  public void renderTileEntityAt(TileTransceiver te, double x, double y, double z, float tick, int b) {

    TileTransceiver trans = te;

    GL11.glEnable(GL12.GL_RESCALE_NORMAL);

    World world = te.getWorld();
    float f = world.getLightBrightness(te.getPos());    
    int l = world.getLightFor(EnumSkyBlock.SKY, te.getPos());
    int l1 = l % 65536;
    int l2 = l / 65536;
    GlStateManager.color(f, f, f);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

    model.render(trans, x, y, z);
    if(trans.isActive()) {
      renderPower(te.getWorld(), x, y, z, true);
    }
    GL11.glDisable(GL12.GL_RESCALE_NORMAL);

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    GL11.glColor3f(1, 1, 1);
    GL11.glDisable(GL11.GL_LIGHTING);
//    Tessellator.instance.startDrawingQuads();
//    Tessellator.instance.setColorOpaque_F(f, f, f);
//
//
//    RenderUtil.bindBlockTexture();
//    CustomRenderBlocks rb = new CustomRenderBlocks(te.getWorldObj());
//    double scale = 0.88;
//    BoundingBox pushPullBounds = BoundingBox.UNIT_CUBE.scale(scale, scale, scale);
//    BoundingBox disabledBounds = BoundingBox.UNIT_CUBE.scale(1.01, 1.01, 1.01);
//
//    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
//      IoMode mode = trans.getIoMode(dir);
//      if(mode != null) {
//        if(mode == IoMode.DISABLED) {
//          rb.setRenderBounds(disabledBounds.minX, disabledBounds.minY, disabledBounds.minZ,
//              disabledBounds.maxX, disabledBounds.maxY, disabledBounds.maxZ);
//        } else {
//          rb.setRenderBounds(pushPullBounds.minX, pushPullBounds.minY, pushPullBounds.minZ,
//              pushPullBounds.maxX, pushPullBounds.maxY, pushPullBounds.maxZ);
//        }
//        IIcon icon = EnderIO.blockTransceiver.getOverlayIconForMode(trans, dir, mode);
//        if(icon != null) {
//          rb.doDefaultRenderFace(dir, EnderIO.blockTransceiver, 0, 0, 0, icon);
//        }
//      }
//    }
//
//    Tessellator.instance.draw();
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glPopMatrix();

  }

  private void renderPower(World world, double x, double y, double z, boolean isActive) {

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    RenderUtil.bindBlockTexture();
    TextureAtlasSprite icon = EnderIO.blockTransceiver.getPortalIcon();

    
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    if(!isActive) {
      GL11.glColor4f(0, 1, 1, 0.5f);
    } else {
      GL11.glColor4f(1, 1, 1, 1f);
    }
    RenderUtil.renderBoundingBox(bb, icon);
    

    GL11.glPopMatrix();

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_BLEND);
  }

}
