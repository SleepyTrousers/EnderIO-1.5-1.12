package crazypants.enderio.machine.capbank.render;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
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
 
  private static final Map<InfoDisplayType, IInfoRenderer> infoRenderers = new EnumMap<InfoDisplayType, IInfoRenderer>(InfoDisplayType.class);
  static {
    infoRenderers.put(InfoDisplayType.LEVEL_BAR, new FillGauge());
    infoRenderers.put(InfoDisplayType.IO, new IoDisplay());
    infoRenderers.put(InfoDisplayType.NONE, new IInfoRenderer() {
      @Override
      public void render(TileCapBank cb, EnumFacing dir, double x, double y, double z, float partialTick) {
      }
    });
  }

  public CapBankRenderer() {    
  }

  //---- Info Display

  @Override
  public void renderTileEntityAt(TileCapBank te, double x, double y, double z, float partialTick, int b) {
    if (!te.hasDisplayTypes()) {
      return;
    }

    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);

    GlStateManager.disableLighting();
    GlStateManager.enableLighting();

    if (Minecraft.isAmbientOcclusionEnabled()) {
      GlStateManager.shadeModel(GL11.GL_SMOOTH);
    } else {
      GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    for (EnumFacing dir : EnumFacing.VALUES) {
      infoRenderers.get(te.getDisplayType(dir)).render(te, dir, x, y, z, partialTick);
    }

    GL11.glPopMatrix();
    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
  }

}
