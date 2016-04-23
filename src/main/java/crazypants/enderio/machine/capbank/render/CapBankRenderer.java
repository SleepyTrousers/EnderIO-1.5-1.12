package crazypants.enderio.machine.capbank.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;

@SideOnly(Side.CLIENT)
public class CapBankRenderer extends TileEntitySpecialRenderer<TileCapBank> {
 
  private Map<InfoDisplayType, IInfoRenderer> infoRenderers;
  private FillGauge fillGaugeRenderer;

  public CapBankRenderer() {    
    fillGaugeRenderer = new FillGauge();
    infoRenderers = new HashMap<InfoDisplayType, IInfoRenderer>();
    infoRenderers.put(InfoDisplayType.LEVEL_BAR, fillGaugeRenderer);
    infoRenderers.put(InfoDisplayType.IO, new IoDisplay());
  }

 

  //---- Info Display

  @Override
  public void renderTileEntityAt(TileCapBank te, double x, double y, double z, float partialTick, int b) {

    TileCapBank cb = te;
    if(!cb.hasDisplayTypes()) {
      return;
    }

    boolean glSetup = false;

    for (EnumFacing dir : EnumFacing.VALUES) {
      InfoDisplayType type = cb.getDisplayType(dir);
      if(type != InfoDisplayType.NONE) {
        IInfoRenderer rend = infoRenderers.get(type);
        if(rend != null) {
          if(!glSetup) {
            glSetup = true;
            // GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(-1.0f, -1.0f);

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GlStateManager.disableLighting();
            GlStateManager.enableLighting();
          }

          rend.render(cb, dir, x, y, z, partialTick);
        }
      }
    }

    if(glSetup) {
      GL11.glPopMatrix();
      GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
      // GL11.glPopAttrib();
    }
  }

}
