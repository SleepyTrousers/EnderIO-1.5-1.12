package crazypants.enderio.machine.transceiver.render;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransceiverRenderer extends TileEntitySpecialRenderer<TileTransceiver> {


  private BoundingBox bb;

  public TransceiverRenderer() {
    float scale = 0.7f;
    bb = BoundingBox.UNIT_CUBE.scale(scale, scale, scale);
  }

  @Override
  public void renderTileEntityAt(TileTransceiver te, double x, double y, double z, float tick, int b) {    
    if(te.isActive()) {
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
      renderPower(te.getWorld(), x, y, z, true);
    }   
  }

  private void renderPower(World world, double x, double y, double z, boolean isActive) {
        
    GlStateManager.pushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    RenderUtil.bindBlockTexture();
    TextureAtlasSprite icon = EnderIO.blockTransceiver.getPortalIcon();

    GlStateManager.enableNormalize();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    if(!isActive) {
      GlStateManager.color(0, 1, 1, 0.5f);
    } else {
      GlStateManager.color(1, 1, 1, 1f);
    }
    RenderUtil.renderBoundingBox(bb, icon);

    GlStateManager.popMatrix();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.disableRescaleNormal();
  }

}
