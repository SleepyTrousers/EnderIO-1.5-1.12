package crazypants.enderio.item;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;

public class YetaWrenchOverlayRenderer {

  public YetaWrenchOverlayRenderer() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void renderOverlay(RenderGameOverlayEvent event) {
    ItemStack equippedWrench = getEquippedWrench();
    if(equippedWrench != null && event.type == ElementType.ALL) {
      doRenderOverlay(event, equippedWrench);
    }
  }

  private ItemStack getEquippedWrench() {
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    ItemStack equipped = player.getCurrentEquippedItem();
    if (equipped != null && equipped.getItem() instanceof IConduitControl) {
      return ((IConduitControl) equipped.getItem()).showOverlay(equipped, player) ? equipped : null;
    }
    return null;
  }

  private void doRenderOverlay(RenderGameOverlayEvent event, ItemStack equippedWrench) {
    ConduitDisplayMode mode = ConduitDisplayMode.getDisplayMode(equippedWrench);
    ScaledResolution res = event.resolution;

    int modeCount = ConduitDisplayMode.registrySize();
    Iterable<ConduitDisplayMode> renderable = ConduitDisplayMode.getRenderableModes();

    int offSize = 16, onSize = 24;
    int padding = 2;
    
    int offsetX = res.getScaledWidth() - onSize;
    int offsetY = res.getScaledHeight() / 2 - (modeCount * ((offSize / 2) + padding) - padding);
    
    Tessellator tess = Tessellator.instance;
    
    int x = offsetX + (offSize / 3);
    int y = offsetY - (padding * 2);
    int height = (modeCount * (offSize + padding)) + (padding * 3);
    if (mode != ConduitDisplayMode.ALL && mode != ConduitDisplayMode.NONE) {
      height += 8;
    }
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    tess.startDrawingQuads();
    Vector4f color = RenderUtil.DEFAULT_TEXT_BG_COL;
    tess.setColorRGBA_F(color.x, color.y, color.z, color.w);
    tess.addVertex(x, y, -5);
    tess.addVertex(x, y + height, -5);
    tess.addVertex(x + offSize, y + height, -5);
    tess.addVertex(x + offSize, y, -5);
    tess.draw();
    
    y += padding * 2;
    x -= offSize / 3;

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    tess.startDrawingQuads();
    for (ConduitDisplayMode toRender : renderable) {
      IWidgetIcon widget = mode == ConduitDisplayMode.ALL ? toRender.getWidgetSelected() : toRender.getWidgetUnselected();
      RenderUtil.bindTexture(widget.getMap().getTexture());
      if (toRender == mode) {
        widget = toRender.getWidgetSelected();
        widget.getMap().render(widget, x - (onSize - offSize), y, onSize, onSize, 0, false);
        y += onSize + padding;
      } else {
        widget.getMap().render(widget, x, y);
        y += offSize + padding;
      }
    }
    tess.draw();
  }
}
