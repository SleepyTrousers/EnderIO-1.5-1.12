package crazypants.enderio.machine.transceiver.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class TransceiverRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private IModel model;

  private BoundingBox bb;

  private boolean adjustForItem = false;

  public TransceiverRenderer() {
    float scale = 0.7f;
    if(Config.useAlternateTesseractModel) {
      model = new TransceiverModelAlt();
      scale = 0.8f;
      adjustForItem = true;
    } else {
      model = new TransceiverModel();
    }
    bb = BoundingBox.UNIT_CUBE.scale(scale, scale, scale);
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick) {

    TileTransceiver cube = (TileTransceiver) te;

    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    
    float f = cube.getWorldObj().getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
    int l = cube.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
    int l1 = l % 65536;
    int l2 = l / 65536;
    Tessellator.instance.setColorOpaque_F(f, f, f);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);

    model.render(cube, x, y, z);
    if(cube.isActive()) {
      renderPower(te.getWorldObj(), x, y, z, true);
    }
    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
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
    if(adjustForItem) {
      switch (type) {
      case ENTITY:
        renderItem(0f, 0f, 0f);
        return;
      case EQUIPPED:
      case EQUIPPED_FIRST_PERSON:
        renderItem(0f, 1f, 1f);
        return;
      case INVENTORY:
        renderItem(0f, 0f, 0f);
        return;
      default:
        renderItem(0f, 0f, 0f);
        return;
      }
    } else {
      renderItem(0, 0, 0);
    }
  }

  private void renderPower(World world, double x, double y, double z, boolean isActive) {

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    RenderUtil.bindBlockTexture();
    IIcon icon = EnderIO.blockHyperCube.getPortalIcon();

    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    if(!isActive) {
      GL11.glColor4f(0, 1, 1, 0.5f);
    } else {
      GL11.glColor4f(1, 1, 1, 1f);
    }
    CubeRenderer.render(bb, icon);
    tessellator.draw();

    GL11.glPopMatrix();

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_BLEND);
  }

  private void renderItem(float x, float y, float z) {
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, z);
    model.render();
    GL11.glPopMatrix();
  }

}
