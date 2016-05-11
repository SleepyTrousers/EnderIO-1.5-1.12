package crazypants.enderio.machine.transceiver.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.transceiver.TileTransceiver;

@SideOnly(Side.CLIENT)
public class TransceiverRenderer extends TileEntitySpecialRenderer<TileTransceiver> {

  private static final float scale = 0.7f;

  public TransceiverRenderer() {
  }

  @Override
  public void renderTileEntityAt(TileTransceiver te, double x, double y, double z, float tick, int b) {
    if (te.isActive()) {
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
      renderPower(te.getWorld(), x, y, z);
    }
  }

  private void renderPower(World world, double x, double y, double z) {
    RenderUtil.bindBlockTexture();
    TextureAtlasSprite icon = EnderIO.blockTransceiver.getPortalIcon();

    float time = Math.abs(50 - (EnderIO.proxy.getTickCount() % 100)) / 50f;
    float localScale = scale + 0.05f - time * 0.1f;
    float alpha = 0.7f + time * 0.25f;

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(localScale, localScale, localScale);

    GlStateManager.pushMatrix();
    GlStateManager.translate((float) x, (float) y, (float) z);
    GlStateManager.enableNormalize();
    GlStateManager.enableBlend();
    GlStateManager.enableLighting();
    GlStateManager.disableLighting();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GlStateManager.enableAlpha();
    GlStateManager.color(1, 1, 1, alpha);
    RenderUtil.renderBoundingBox(bb, icon);
    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.disableRescaleNormal();
  }

}
