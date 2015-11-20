package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.handlers.ClientHandler;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;

public class YetaWrenchOverlayRenderer {
  
  private ConduitDisplayMode cachedMode = ConduitDisplayMode.ALL;
  private int displayTickCount;
  private long lastTick;

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
    
    if (mode != cachedMode) {
      cachedMode = mode;
      displayTickCount = 20;
      lastTick = ClientHandler.getTicksElapsed();
    }
    
    ScaledResolution res = event.resolution;
    Tessellator tess = Tessellator.instance;
    int modeCount = ConduitDisplayMode.registrySize();
    Iterable<ConduitDisplayMode> renderable = ConduitDisplayMode.getRenderableModes();

    switch (Config.yetaWrenchOverlayMode) {
    case 0:
      if (displayTickCount > 0) {

        if (lastTick < ClientHandler.getTicksElapsed()) {
          lastTick++;
          displayTickCount--;
        }

        int x = res.getScaledWidth() / 2 - 8;
        int y = res.getScaledHeight() / 2 - 24;

        // TODO when I've not been up for 16 hrs, clean this up
        IWidgetIcon widget = mode.getWidgetSelected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        widget.getMap().render(widget, x, y, true);
        int size = 12;
        widget = mode.previous().getWidgetSelected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        widget.getMap().render(widget, x - 18 + (16 - size), y + (16 - size), size, size, 0, true);
        widget = mode.next().getWidgetSelected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        widget.getMap().render(widget, x + 18, y + (16 - size), size, size, 0, true);
      } else {

        int x = res.getScaledWidth() - 20;
        int y = res.getScaledHeight() - 20;

        IWidgetIcon widget = mode.getWidgetSelected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        widget.getMap().render(widget, x, y, true);
      }
      break;
    case 1:
      int size = 16;
      int padding = 2;
      
      int inset = (int) (size / 1.5);
      int offsetX = res.getScaledWidth() - inset - 1;
      int offsetY = res.getScaledHeight() - modeCount * (size + padding) - padding;
      
      inset = size - inset;
            
      int x = offsetX + 2;
      int y = offsetY - (padding * 2);
      int height = (modeCount * (size + padding)) + (padding * 3);

      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glShadeModel(GL11.GL_SMOOTH);
      tess.startDrawingQuads();
      tess.setColorRGBA_F(0, 0, 0, 0.2f);
      tess.addVertex(x, y, -5);
      tess.addVertex(x, y + height, -5);
      Vector4f color = new Vector4f(0, 0, 0, 1);
      tess.setColorRGBA_F(color.x, color.y, color.z, color.w);
      tess.addVertex(x + size, y + height, -5);
      tess.addVertex(x + size, y, -5);
      tess.draw();
      tess.setColorOpaque_I(0xFFFFFF);
      GL11.glShadeModel(GL11.GL_FLAT);
      
      y += padding * 2;
      x -= 2;
      
      if (mode == ConduitDisplayMode.ALL) {
        x -= inset;
      }

      GL11.glEnable(GL11.GL_TEXTURE_2D);
      tess.startDrawingQuads();
      for (ConduitDisplayMode toRender : renderable) {
        IWidgetIcon widget = mode == ConduitDisplayMode.ALL ? toRender.getWidgetSelected() : toRender.getWidgetUnselected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        if (toRender == mode) {
          widget = toRender.getWidgetSelected();
          widget.getMap().render(widget, x - inset, y);
        } else {
          widget.getMap().render(widget, x, y);
        }
        y += size + padding;
      }
      tess.draw();
      break;
    case 2:
      
      offsetX = res.getScaledWidth() - (modeCount * 8) - 16;
      y = res.getScaledHeight() - 40;
      
      x = offsetX;
      if (modeCount % 2 == 1) {
        x += 8;
      }
      
      int count = 0;
      tess.startDrawingQuads();
      for (ConduitDisplayMode toRender : renderable) {
        IWidgetIcon widget = mode == ConduitDisplayMode.ALL || toRender == mode ? toRender.getWidgetSelected() : toRender.getWidgetUnselected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        widget.getMap().render(widget, x, y);
        x += 16;
        if (count == modeCount / 2 - 1) {
          x = offsetX;
          y += 16;
        }
        count++;
      }
      tess.draw();
    }
  }
}
