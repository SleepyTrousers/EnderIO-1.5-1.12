package crazypants.enderio.base.render.itemoverlay;

import javax.annotation.Nonnull;

import crazypants.enderio.base.material.glass.FusedQuartzType;
import crazypants.enderio.util.FuncUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FusedQuartzOverlayRenderHelper {

  @SideOnly(Side.CLIENT)
  public static void doItemOverlayIntoGUI(@Nonnull FusedQuartzType type, int xPosition, int yPosition) {
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    GlStateManager.disableBlend();
    GlStateManager.enableBlend();

    FuncUtil.doIf(type.getIcon0(), icon -> icon.getMap().render(icon, xPosition, yPosition, true));
    FuncUtil.doIf(type.getIcon1(), icon -> icon.getMap().render(icon, xPosition, yPosition, true));
    FuncUtil.doIf(type.getIcon2(), icon -> icon.getMap().render(icon, xPosition, yPosition, true));

    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    GlStateManager.enableBlend();
  }

}
