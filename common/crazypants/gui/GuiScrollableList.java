package crazypants.gui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScrollableList<T> {

  private final Minecraft mc = Minecraft.getMinecraft();

  protected int originX;
  protected int originY;
  protected int width;
  protected int height;
  protected int minY;
  protected int maxY;
  protected int minX;
  protected int maxX;

  protected final int slotHeight;

  private int scrollUpButtonID;

  private int scrollDownButtonID;

  protected int mouseX;

  protected int mouseY;

  private float initialClickY = -2.0F;

  private float scrollMultiplier;

  private float amountScrolled;

  protected int selectedIndex = -1;

  private long lastClickedTime;

  private boolean showSelectionBox = true;
  
  protected int margin = 4;
  
  protected List<ListSelectionListener<T>> listeners = new CopyOnWriteArrayList<ListSelectionListener<T>>();

  public GuiScrollableList(int width, int height, int originX, int originY, int slotHeight) {
    this.width = width;
    this.height = height;
    this.originX = originX;
    this.originY = originY;
    this.slotHeight = slotHeight;
    
    minY = originY;
    maxY = minY + height;
    minX = originX;
    maxX = minX + width;           
  }
  
  public void onGuiInit(IGuiScreen gui) {
    minY = originY + gui.getGuiTop();
    maxY = minY + height;
    minX = originX + gui.getGuiLeft();
    maxX = minX + width;
  }

  public void addSelectionListener(ListSelectionListener<T> listener) {
    listeners.add(listener);
  }
  
  public void removeSelectionListener(ListSelectionListener<T> listener) {
    listeners.remove(listener);
  }
  
  public T getSelectedElement() {
    return getElementAt(selectedIndex);
  }
  
  public void setSelection(T selection) {
    setSelection(getIndexOf(selection));    
  }
  
  public void setSelection(int index) {
    if(index == selectedIndex) {
      return;
    }
    selectedIndex = index;
    for(ListSelectionListener<T> listener : listeners) {
      listener.selectionChanged(this, selectedIndex);
    }
  }
    
  
  public int getIndexOf(T element) {
    if(element == null) {
      return -1;
    }
    for(int i=0; i < getNumElements(); i++) {
      if(element.equals(getElementAt(i))) {
        return i;
      }
    }
    return -1;
  }
  
  public abstract T getElementAt(int index);
  
  public abstract int getNumElements();
  
  protected abstract void drawElement(int elementIndex, int x, int y, int height, Tessellator tessellator);

  protected boolean elementClicked(int elementIndex, boolean doubleClick) {
    return true;
  }
  
  public void setShowSelectionBox(boolean val) {
    showSelectionBox = val;
  }
  
  protected int getContentHeight() {
    return getNumElements() * slotHeight;
  }

  public void setScrollButtonIds(int scrollUpButtonID, int scrollDownButtonID) {
    this.scrollUpButtonID = scrollUpButtonID;
    this.scrollDownButtonID = scrollDownButtonID;
  }

  private void clampScrollToBounds() {
    int i = getContentOverhang();
    if (i < 0) {
      i *= -1;
    }
    if (amountScrolled < 0.0F) {
      amountScrolled = 0.0F;
    }
    if (amountScrolled > (float) i) {
      amountScrolled = (float) i;
    }
  }

  public int getContentOverhang() {
    return getContentHeight() - (height - margin);
  }

  public void actionPerformed(GuiButton b) {
    if (b.enabled) {
      if (b.id == scrollUpButtonID) {
        amountScrolled -= (float) (slotHeight * 2 / 3);
        initialClickY = -2.0F;
        clampScrollToBounds();
      } else if (b.id == scrollDownButtonID) {
        amountScrolled += (float) (slotHeight * 2 / 3);
        initialClickY = -2.0F;
        clampScrollToBounds();
      }
    }
  }

  /**
   * draws the slot to the screen, pass in mouse's current x and y and partial
   * ticks
   */
  public void drawScreen(int mX, int mY, float partialTick) {
    this.mouseX = mX;
    this.mouseY = mY;
    
    processMouseEvents();

    clampScrollToBounds();

    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_FOG);
    
    ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);   
    int sx = minX * sr.getScaleFactor();
    int sw = width * sr.getScaleFactor();
    int sy = mc.displayHeight - (maxY * sr.getScaleFactor());
    int sh = height * sr.getScaleFactor();
    GL11.glEnable(GL11.GL_SCISSOR_TEST);
    GL11.glScissor(sx, sy, sw, sh);
    
    Tessellator tessellator = Tessellator.instance;
    drawContainerBackground(tessellator);
    
    
    int contentYOffset = this.minY + margin - (int) this.amountScrolled;

    for (int i = 0; i < getNumElements(); ++i) {
      
      int elementY = contentYOffset + i * this.slotHeight;
      int slotHeight = this.slotHeight - margin;

      if (elementY <= maxY && elementY + slotHeight >= minY) {
        
        if (showSelectionBox && i == selectedIndex) {          
          GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
          GL11.glDisable(GL11.GL_TEXTURE_2D);
          tessellator.startDrawingQuads();
          tessellator.setColorOpaque_I(8421504);
          tessellator.addVertexWithUV((double) minX, (double) (elementY + slotHeight + 2), 0.0D, 0.0D, 1.0D);
          tessellator.addVertexWithUV((double) maxX, (double) (elementY + slotHeight + 2), 0.0D, 1.0D, 1.0D);
          tessellator.addVertexWithUV((double) maxX, (double) (elementY - 2), 0.0D, 1.0D, 0.0D);
          tessellator.addVertexWithUV((double) minX, (double) (elementY - 2), 0.0D, 0.0D, 0.0D);
          tessellator.setColorOpaque_I(0);
          tessellator.addVertexWithUV((double) (minX + 1), (double) (elementY + slotHeight + 1), 0.0D, 0.0D, 1.0D);
          tessellator.addVertexWithUV((double) (maxX - 1), (double) (elementY + slotHeight + 1), 0.0D, 1.0D, 1.0D);
          tessellator.addVertexWithUV((double) (maxX - 1), (double) (elementY - 1), 0.0D, 1.0D, 0.0D);
          tessellator.addVertexWithUV((double) (minX + 1), (double) (elementY - 1), 0.0D, 0.0D, 0.0D);
          tessellator.draw();
          GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        drawElement(i, minX, elementY, slotHeight, tessellator);
      }
    }

    GL11.glDisable(GL11.GL_SCISSOR_TEST);
    
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDisable(GL11.GL_ALPHA_TEST);
    GL11.glShadeModel(GL11.GL_SMOOTH);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    
    tessellator.startDrawingQuads();
    tessellator.setColorRGBA_I(0, 0);
    tessellator.addVertexWithUV((double) this.minX, (double) (this.minY + margin), 0.0D, 0.0D, 1.0D);
    tessellator.addVertexWithUV((double) this.maxX, (double) (this.minY + margin), 0.0D, 1.0D, 1.0D);
    tessellator.setColorRGBA_I(0, 255);
    tessellator.addVertexWithUV((double) this.maxX, (double) this.minY, 0.0D, 1.0D, 0.0D);
    tessellator.addVertexWithUV((double) this.minX, (double) this.minY, 0.0D, 0.0D, 0.0D);
    tessellator.draw();
    tessellator.startDrawingQuads();
    tessellator.setColorRGBA_I(0, 255);
    tessellator.addVertexWithUV((double) this.minX, (double) this.maxY, 0.0D, 0.0D, 1.0D);
    tessellator.addVertexWithUV((double) this.maxX, (double) this.maxY, 0.0D, 1.0D, 1.0D);
    tessellator.setColorRGBA_I(0, 0);
    tessellator.addVertexWithUV((double) this.maxX, (double) (this.maxY - margin), 0.0D, 1.0D, 0.0D);
    tessellator.addVertexWithUV((double) this.minX, (double) (this.maxY - margin), 0.0D, 0.0D, 0.0D);
    tessellator.draw();

    renderScrollBar(tessellator);

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glShadeModel(GL11.GL_FLAT);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    GL11.glDisable(GL11.GL_BLEND);
  }

  protected void renderScrollBar(Tessellator tessellator) {

    int contentHeightOverBounds = getContentOverhang();
    if (contentHeightOverBounds > 0) {

      int clear = (maxY - minY) * (maxY - minY) / getContentHeight();

      if (clear < 32) {
        clear = 32;
      }

      if (clear > maxY - minY - 8) {
        clear = maxY - minY - 8;
      }

      int y = (int) this.amountScrolled * (maxY - minY - clear) / contentHeightOverBounds + minY;
      if (y < minY) {
        y = minY;
      }
      
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      int scrollBarMinX = getScrollBarX();
      int scrollBarMaxX = scrollBarMinX + 6;
      tessellator.startDrawingQuads();

      tessellator.setColorRGBA_I(0, 255);
      tessellator.addVertexWithUV(scrollBarMinX, maxY, 0.0D, 0.0D, 1.0D);
      tessellator.addVertexWithUV(scrollBarMaxX, maxY, 0.0D, 1.0D, 1.0D);
      tessellator.addVertexWithUV(scrollBarMaxX, minY, 0.0D, 1.0D, 0.0D);
      tessellator.addVertexWithUV(scrollBarMinX, minY, 0.0D, 0.0D, 0.0D);

      tessellator.setColorRGBA_F(0.3f, 0.3f, 0.3f, 1);
      tessellator.addVertexWithUV(scrollBarMinX, (y + clear), 0.0D, 0.0D, 1.0D);
      tessellator.addVertexWithUV(scrollBarMaxX, (y + clear), 0.0D, 1.0D, 1.0D);
      tessellator.addVertexWithUV(scrollBarMaxX, y, 0.0D, 1.0D, 0.0D);
      tessellator.addVertexWithUV(scrollBarMinX, y, 0.0D, 0.0D, 0.0D);

      tessellator.setColorRGBA_F(0.7f, 0.7f, 0.7f, 1);
      tessellator.addVertexWithUV(scrollBarMinX, (y + clear - 1), 0.0D, 0.0D, 1.0D);
      tessellator.addVertexWithUV((scrollBarMaxX - 1), (y + clear - 1), 0.0D, 1.0D, 1.0D);
      tessellator.addVertexWithUV((scrollBarMaxX - 1), y, 0.0D, 1.0D, 0.0D);
      tessellator.addVertexWithUV(scrollBarMinX, y, 0.0D, 0.0D, 0.0D);

      tessellator.draw();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
  }

  private void processMouseEvents() {
    if (Mouse.isButtonDown(0)) {
      processMouseBown();
    } else {
      while (!mc.gameSettings.touchscreen && Mouse.next()) {

        int mouseWheelDelta = Mouse.getEventDWheel();

        if (mouseWheelDelta != 0) {
          if (mouseWheelDelta > 0) {
            mouseWheelDelta = -1;
          } else if (mouseWheelDelta < 0) {
            mouseWheelDelta = 1;
          }
          amountScrolled += (float) (mouseWheelDelta * slotHeight / 2);
        }
      }
      initialClickY = -1.0F;
    }
  }

  private void processMouseBown() {
    int contentHeightOverBounds;
    if (initialClickY == -1.0F) {

      if (mouseY >= minY && mouseY <= maxY && mouseX >= minX && mouseX <= maxX + 6) {

        boolean clickInBounds = true;

        int y = mouseY - minY + (int) amountScrolled - margin;
        int mouseOverElement = y / slotHeight;

        if (mouseX >= minX && mouseX <= maxX && mouseOverElement >= 0 && y >= 0 && mouseOverElement < getNumElements()) {
          boolean doubleClick = mouseOverElement == selectedIndex && Minecraft.getSystemTime() - lastClickedTime < 250L;
          if(elementClicked(mouseOverElement, doubleClick)) {
            setSelection(mouseOverElement);
          }
          lastClickedTime = Minecraft.getSystemTime();

        } else if (mouseX >= minX && mouseX <= maxX && y < 0) {
          clickInBounds = false;
        }

        int scrollBarMinX = getScrollBarX();
        int scrollBarMaxX = scrollBarMinX + 6;
        if (mouseX >= scrollBarMinX && mouseX <= scrollBarMaxX) {

          scrollMultiplier = -1.0F;
          contentHeightOverBounds = getContentOverhang();

          if (contentHeightOverBounds < 1) {
            contentHeightOverBounds = 1;
          }

          int empty = (int) ((float) ((maxY - minY) * (maxY - minY)) / (float) getContentHeight());
          if (empty < 32) {
            empty = 32;
          }
          if (empty > maxY - minY - 8) {
            empty = maxY - minY - 8;
          }
          scrollMultiplier /= (float) (maxY - minY - empty) / (float) contentHeightOverBounds;

        } else {
          scrollMultiplier = 1.0F;
        }

        if (clickInBounds) {
          initialClickY = (float) mouseY;
        } else {
          initialClickY = -2.0F;
        }

      } else {
        initialClickY = -2.0F;
      }

    } else if (initialClickY >= 0.0F) {
      // Scrolling
      amountScrolled -= ((float) mouseY - initialClickY) * scrollMultiplier;
      initialClickY = (float) mouseY;
    }
  }

  protected int getScrollBarX() {
    return minX + width;
  }

  protected void drawContainerBackground(Tessellator tess) {

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    tess.startDrawingQuads();
    tess.setColorOpaque_I(2105376);
    tess.addVertex((double) minX, (double) maxY, 0.0D);
    tess.addVertex((double) maxX, (double) maxY, 0.0D);
    tess.addVertex((double) maxX, (double) minY, 0.0D);
    tess.addVertex((double) minX, (double) minY, 0.0D);
    tess.draw();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    
  }

}
