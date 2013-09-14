package crazypants.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.render.IconUtil;
import crazypants.render.RenderUtil;

public class GuiIconRenderer extends Gui {

  public static final int DEFAULT_WIDTH = 24;
  public static final int HWIDTH = DEFAULT_WIDTH / 2;
  public static final int DEFAULT_HEIGHT = 24;
  public static final int HHEIGHT = DEFAULT_HEIGHT / 2;

  protected int hwidth = HWIDTH;
  protected int hheight = HHEIGHT;
  protected int width = DEFAULT_WIDTH;
  protected int height = DEFAULT_HEIGHT;

  protected Icon icon;
  protected ResourceLocation texture;
  
  private int yPosition;
  private int xPosition;
  
  private float alpha = 1.0f;

  
  public GuiIconRenderer(int x, int y, int itemId, int itemMeta) {
    xPosition = x;
    yPosition = y;
    icon = IconUtil.getIconForItem(itemId, itemMeta);
    texture = RenderUtil.ITEM_TEX;
  }
  
  public GuiIconRenderer(int x, int y, Icon icon, ResourceLocation texture) {
    xPosition = x;
    yPosition = y;    
    this.icon = icon;
    this.texture = texture;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    hwidth = width / 2;
    hheight = height / 2;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Icon getIcon() {
    return icon;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  public ResourceLocation getTexture() {
    return texture;
  }

  public void setTexture(ResourceLocation textureName) {
    this.texture = textureName;
  }

  public void draw() {
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    RenderUtil.bindTexture(texture);    
    drawTexturedModelRectFromIcon(xPosition, yPosition, icon, width, height);

    GL11.glPopAttrib();
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  }

}
