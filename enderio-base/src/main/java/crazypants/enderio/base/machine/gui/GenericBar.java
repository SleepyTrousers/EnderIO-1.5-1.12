package crazypants.enderio.base.machine.gui;

import java.awt.Rectangle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.IDrawingElement;
import com.enderio.core.client.gui.widget.GuiToolTip;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class GenericBar implements IDrawingElement {

  private final @Nonnull GuiContainerBaseEIO owner;
  private final int x, y, width, height, color;
  private final @Nullable GuiToolTip tooltip;

  public GenericBar(@Nonnull GuiContainerBaseEIO owner, int x, int y, int width, int height, int color, @Nullable GuiToolTip tooltip) {
    this.owner = owner;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.color = color;
    this.tooltip = tooltip;
  }

  public GenericBar(@Nonnull GuiContainerBaseEIO owner, Rectangle r, int color, @Nullable GuiToolTip tooltip) {
    this(owner, r.x, r.y, r.width, r.height, color, tooltip);

  }

  @Override
  public @Nullable GuiToolTip getTooltip() {
    return tooltip;
  }

  @Override
  public void drawGuiContainerBackgroundLayer(float partialTicks, int par2, int par3) {
    final int drawX = owner.getGuiLeft() + x;
    final int barWidth = width;
    final int barHeight = getLevelScaled(height);
    final int yOffset = (y + height) - barHeight;
    final int drawY = owner.getGuiTop() + yOffset;

    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    owner.drawGradientRect(drawX, drawY, barWidth, barHeight, getColor(), getColor());

    GlStateManager.disableBlend();
  }

  protected int getColor() {
    return color;
  }

  protected float getLevel() {
    return 0f;
  }

  protected int getLevelScaled(int scale) {
    return (int) MathHelper.clamp(getLevel() * scale, 0, scale);
  }

}
