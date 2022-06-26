package crazypants.enderio.conduit.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.TooltipButton;
import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.api.client.render.IWidgetIcon;

import net.minecraft.client.Minecraft;

public class FakeButton extends TooltipButton {

  public static final int DEFAULT_WIDTH = 16;
  public static final int DEFAULT_HEIGHT = 16;

  protected @Nonnull IWidgetIcon icon;

  private int marginY = 0;
  private int marginX = 0;

  public FakeButton(@Nonnull IGuiScreen gui, int x, int y, @Nonnull IWidgetIcon icon) {
    super(gui, -1, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, "");
    this.icon = icon;
  }

  @Override
  public FakeButton setPosition(int x, int y) {
    super.setPosition(x, y);
    return this;
  }

  public FakeButton setIconMargin(int x, int y) {
    marginX = x;
    marginY = y;
    return this;
  }

  public @Nonnull IWidgetIcon getIcon() {
    return icon;
  }

  public void setIcon(@Nonnull IWidgetIcon icon) {
    this.icon = icon;
  }

  public boolean mousePressedButton(@Nonnull Minecraft mc, int mouseX, int mouseY, int button) {
    return false;
  }

  protected boolean checkMousePress(@Nonnull Minecraft mc, int mouseX, int mouseY) {
    return false;
  }

  /**
   * Draws this button to the screen.
   */
  @Override
  public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY) {
    updateTooltip(mc, mouseX, mouseY);
    if (isVisible()) {
      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      icon.getMap().render(icon, xPosition + marginX, yPosition + marginY, width - 2 * marginX, height - 2 * marginY, 0, true);
      GL11.glPopAttrib();
    }
  }

}
