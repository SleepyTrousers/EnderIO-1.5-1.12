package crazypants.enderio.item;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.handlers.ClientHandler;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;
import crazypants.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class YetaWrenchOverlayRenderer {

  private @Nonnull ConduitDisplayMode cachedMode = ConduitDisplayMode.ALL;
  private int displayTickCount;
  private long lastTick;

  public YetaWrenchOverlayRenderer() {
  }

  @SubscribeEvent
  public void renderOverlay(@Nonnull RenderGameOverlayEvent.Post event) {
    ItemStack equippedWrench = getEquippedWrench();
    if (event.getType() == ElementType.ALL && Prep.isValid(equippedWrench)) {
      doRenderOverlay(event, equippedWrench);
    }
  }

  private @Nonnull ItemStack getEquippedWrench() {
    EntityPlayer player = Minecraft.getMinecraft().player;
    ItemStack equipped = player.getHeldItemMainhand();
    if (equipped.getItem() instanceof IConduitControl && ((IConduitControl) equipped.getItem()).showOverlay(equipped, player)) {
      return equipped;
    }
    return Prep.getEmpty();
  }

  private void doRenderOverlay(@Nonnull RenderGameOverlayEvent event, @Nonnull ItemStack equippedWrench) {
    ConduitDisplayMode mode = ConduitDisplayMode.getDisplayMode(equippedWrench);

    if (mode != cachedMode) {
      cachedMode = mode;
      displayTickCount = 20;
      lastTick = ClientHandler.getTicksElapsed();
    }

    ScaledResolution res = event.getResolution();

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

      GL11.glDisable(GL11.GL_TEXTURE_2D); // FIXME
      GL11.glShadeModel(GL11.GL_SMOOTH);

      VertexFormat vf = DefaultVertexFormats.POSITION_COLOR;
      Tessellator tess = Tessellator.getInstance();
      VertexBuffer wr = tess.getBuffer();
      wr.begin(GL11.GL_QUADS, vf);
      wr.pos(x, y, -5).color(0, 0, 0, 0.2f).endVertex();
      ;
      wr.pos(x, y + height, -5).color(0, 0, 0, 0.2f).endVertex();
      ;
      Vector4f color = new Vector4f(0, 0, 0, 1);
      wr.pos(x + size, y + height, -5).color(color.x, color.y, color.z, color.w).endVertex();
      wr.pos(x + size, y, -5).color(color.x, color.y, color.z, color.w).endVertex();
      tess.draw();

      GlStateManager.color(1, 1, 1);
      GL11.glShadeModel(GL11.GL_FLAT);

      y += padding * 2;
      x -= 2;

      if (mode == ConduitDisplayMode.ALL) {
        x -= inset;
      }

      GL11.glEnable(GL11.GL_TEXTURE_2D);

      for (ConduitDisplayMode toRender : renderable) {
        IWidgetIcon widget = mode == ConduitDisplayMode.ALL ? toRender.getWidgetSelected() : toRender.getWidgetUnselected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        if (toRender == mode) {
          widget = toRender.getWidgetSelected();
          widget.getMap().render(widget, x - inset, y, true);
        } else {
          widget.getMap().render(widget, x, y, true);
        }
        y += size + padding;
      }

      break;
    case 2:

      offsetX = res.getScaledWidth() - (modeCount * 8) - 16;
      y = res.getScaledHeight() - 40;

      x = offsetX;
      if (modeCount % 2 == 1) {
        x += 8;
      }

      int count = 0;

      for (ConduitDisplayMode toRender : renderable) {
        IWidgetIcon widget = mode == ConduitDisplayMode.ALL || toRender == mode ? toRender.getWidgetSelected() : toRender.getWidgetUnselected();
        RenderUtil.bindTexture(widget.getMap().getTexture());
        widget.getMap().render(widget, x, y, true);
        x += 16;
        if (count == modeCount / 2 - 1) {
          x = offsetX;
          y += 16;
        }
        count++;
      }
    }
  }
}
