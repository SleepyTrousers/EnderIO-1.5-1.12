package crazypants.enderio.base.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.ILocalizable;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.IntegrationConfig;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.integration.jei.JeiAccessor;
import crazypants.enderio.base.machine.interfaces.INotifier;
import crazypants.enderio.base.network.IRemoteExec;
import crazypants.enderio.base.scheduler.Celeb;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import crazypants.enderio.util.Prep;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiContainerBaseEIO<O> extends GuiContainerBase implements IRemoteExec.IGui {

  private final @Nonnull NNList<ResourceLocation> guiTextures = new NNList<ResourceLocation>();
  private final @Nonnull O owner;

  /**
   * Constructor.
   * 
   * @param owner
   *          Any kind of object that the GUI wants to name as "owner". For TE-based GUIs it is recommended to use the TE here. Otherwise, the container is a
   *          good choice.
   * @param par1Container
   *          The container, see {@link GuiContainer#inventorySlots}.
   * @param guiTexture
   *          A list of texture names (relative to Ender IO's GUI texture base) that can later be selected using their index.
   */
  public GuiContainerBaseEIO(@Nonnull O owner, @Nonnull Container par1Container, String... guiTexture) {
    super(par1Container);
    this.owner = owner;
    for (String string : guiTexture) {
      guiTextures.add(EnderIO.proxy.getGuiTexture(NullHelper.notnull(string, "invalid gui texture name")));
    }
  }

  public @Nonnull O getOwner() {
    return owner;
  }

  public void bindGuiTexture() {
    bindGuiTexture(0);
  }

  public void bindGuiTexture(int id) {
    RenderUtil.bindTexture(getGuiTexture(id));
  }

  protected @Nonnull ResourceLocation getGuiTexture(int id) {
    return guiTextures.size() > id ? guiTextures.get(id) : new ResourceLocation(EnderIO.DOMAIN, "texture_missing");
  }

  @Override
  protected @Nonnull ResourceLocation getGuiTexture() {
    return getGuiTexture(0);
  }

  protected boolean showRecipeButton() {
    return JeiAccessor.isJeiRuntimeAvailable();
  }

  private final @Nonnull List<Rectangle> tabAreas = new ArrayList<Rectangle>();
  private final static @Nonnull Rectangle NO_TAB = new Rectangle(0, 0, 0, 0);

  /**
   * See {@link mezz.jei.api.gui.IAdvancedGuiHandler#getGuiExtraAreas(net.minecraft.client.gui.inventory.GuiContainer)}
   * 
   */
  public List<Rectangle> getBlockingAreas() {
    // return a new object every time so equals() actually checks the contents
    return new ArrayList<Rectangle>(tabAreas);
  }

  /**
   * See {@link com.brandon3055.projectintelligence.api.IPageSupplier}
   * 
   */
  public @Nonnull Set<String> getDocumentationPages() {
    return Collections.singleton(getDocumentationPage());
  }

  // Project Intelligence
  protected @Nonnull String getDocumentationPage() {
    return EnderIO.DOMAIN + ":" + getClass().getSimpleName().replaceAll("([A-Z])", "_$0").replaceFirst("^_", "").toLowerCase(Locale.ENGLISH)
        .replaceFirst("_gui$", "").replaceFirst("^gui_", "");
  }

  /**
   * See {@link com.brandon3055.projectintelligence.api.IGuiDocHandler#getCollapsedArea(net.minecraft.client.gui.GuiScreen)}
   *
   */
  public Rectangle getDocumentationButtonArea() {
    return new Rectangle(guiLeft - 25, guiTop + 3, 25, 25);
  }

  /**
   * See {@link com.brandon3055.projectintelligence.api.IGuiDocHandler#getExpandedArea(net.minecraft.client.gui.GuiScreen)}
   *
   */
  public Rectangle getDocumentationArea() {
    return IntegrationConfig.rectangleWithPIMargins(0, 0, guiLeft, height);
  }

  /**
   * See {@link mezz.jei.api.gui.IAdvancedGuiHandler#getIngredientUnderMouse}
   * 
   */
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    return null;
  }

  public void startTabs() {
    tabAreas.clear();
  }

  public int getTabFromCoords(int x, int y) {
    for (int i = 0; i < tabAreas.size(); i++) {
      if (tabAreas.get(i).contains(x, y)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  protected void mouseClicked(int x, int y, int button) throws IOException {
    if (button == 0) {
      int tabFromCoords = getTabFromCoords(x, y);
      if (tabFromCoords >= 0 && doSwitchTab(tabFromCoords)) {
        SoundHelper.playSound(mc.world, mc.player, SoundRegistry.TAB_SWITCH, 1, 1);
        return;
      }
    }
    super.mouseClicked(x, y, button);
  }

  protected boolean doSwitchTab(int tab) {
    return false;
  }

  public @Nonnull Rectangle renderStdTab(int sx, int sy, int tabNo, boolean isActive) {
    return renderStdTab(sx, sy, tabNo, Prep.getEmpty(), null, null, isActive);
  }

  public @Nonnull Rectangle renderStdTab(int sx, int sy, int tabNo, @Nonnull ItemStack stack, boolean isActive) {
    return renderStdTab(sx, sy, tabNo, stack, null, null, isActive);
  }

  public @Nonnull Rectangle renderStdTab(int sx, int sy, int tabNo, @Nullable IWidgetIcon icon, boolean isActive) {
    return renderStdTab(sx, sy, tabNo, Prep.getEmpty(), icon, null, isActive);
  }

  public @Nonnull Rectangle renderStdTab(int sx, int sy, int tabNo, @Nullable GuiButton button, boolean isActive) {
    return renderStdTab(sx, sy, tabNo, Prep.getEmpty(), null, button, isActive);
  }

  public @Nonnull Rectangle renderStdTab(int sx, int sy, int tabNo, @Nonnull ItemStack stack, @Nullable GuiButton button, boolean isActive) {
    return renderStdTab(sx, sy, tabNo, stack, null, button, isActive);
  }

  public @Nonnull Rectangle renderStdTab(int sx, int sy, int tabNo, @Nullable IWidgetIcon icon, @Nullable GuiButton button, boolean isActive) {
    return renderStdTab(sx, sy, tabNo, Prep.getEmpty(), icon, button, isActive);
  }

  public @Nonnull Rectangle renderStdTab(int sx, int sy, int tabNo, @Nonnull ItemStack stack, @Nullable IWidgetIcon icon, @Nullable GuiButton button,
      boolean isActive) {
    int tabX = sx + xSize + -3;
    int tabY = sy + 4 + 24 * tabNo;

    Rectangle result = renderTab(tabX, tabY, 24, stack, icon, isActive);

    while (tabAreas.size() <= tabNo) {
      tabAreas.add(NO_TAB);
    }
    tabAreas.set(tabNo, result);

    if (button != null) {
      button.x = result.x;
      button.y = result.y;
      button.width = result.width;
      button.height = result.height;
      button.enabled = !isActive;
    }

    GlStateManager.color(1, 1, 1, 1);
    return result;
  }

  public @Nonnull Rectangle renderTab(int x, int y, int w, @Nonnull ItemStack stack, @Nullable IWidgetIcon icon, boolean isActive) {
    int bg_x = isActive ? 0 : 3;
    int bg_w = w - 3 - bg_x;
    int l_x = isActive ? 0 : 3;
    int l_w = w - 3 - l_x;
    int r_x = 3;
    int r_w = w - r_x;
    int r_u = IconEIO.TAB_FRAME_LEFT.width - r_w;

    if (isActive) {
      GlStateManager.color(1, 1, 1, 1);
    } else {
      GlStateManager.color(.8f, .8f, .8f, 1);
    }
    GlStateManager.disableLighting();

    BufferBuilder tes = Tessellator.getInstance().getBuffer();
    RenderUtil.bindTexture(IconEIO.map.getTexture());
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

    renderTabPart(tes, x + bg_x, y, IconEIO.TAB_BG.getX() + bg_x, IconEIO.TAB_BG.getY(), bg_w, IconEIO.TAB_BG.getHeight());
    renderTabPart(tes, x + l_x, y, IconEIO.TAB_FRAME_LEFT.getX() + l_x, IconEIO.TAB_FRAME_LEFT.getY(), l_w, IconEIO.TAB_FRAME_LEFT.getHeight());
    renderTabPart(tes, x + r_x, y, IconEIO.TAB_FRAME_RIGHT.getX() + r_u, IconEIO.TAB_FRAME_RIGHT.getY(), r_w, IconEIO.TAB_FRAME_RIGHT.getHeight());

    if (icon != null && icon.getMap() == IconEIO.map) {
      icon.getMap().render(icon, x + w / 2 - 8, y + IconEIO.TAB_BG.getHeight() / 2 - 8, false);
    }
    Tessellator.getInstance().draw();
    if (icon != null && icon.getMap() != IconEIO.map) {
      icon.getMap().render(icon, x + w / 2 - 8, y + IconEIO.TAB_BG.getHeight() / 2 - 8, true);
    }

    if (Prep.isValid(stack)) {
      RenderHelper.enableGUIStandardItemLighting();
      itemRender.renderItemIntoGUI(stack, x + w / 2 - 8, y + IconEIO.TAB_BG.getHeight() / 2 - 8);
      RenderHelper.disableStandardItemLighting();
    }

    return new Rectangle(x + bg_x, y - 1, bg_w + 3 + 1, IconEIO.TAB_BG.getHeight() + 2);
  }

  private void renderTabPart(@Nonnull BufferBuilder tes, int x, int y, int u, int v, int w, int h) {
    double minU = (double) u / IconEIO.map.getSize();
    double maxU = (double) (u + w) / IconEIO.map.getSize();
    double minV = (double) v / IconEIO.map.getSize();
    double maxV = (double) (v + h) / IconEIO.map.getSize();

    tes.pos(x, y + h, 0).tex(minU, maxV).endVertex();
    tes.pos(x + w, y + h, 0).tex(maxU, maxV).endVertex();
    tes.pos(x + w, y + 0, 0).tex(maxU, minV).endVertex();
    tes.pos(x, y + 0, 0).tex(minU, minV).endVertex();
  }

  @Override
  public void setGuiID(int id) {
    if (inventorySlots instanceof IRemoteExec.IContainer) {
      ((IRemoteExec.IContainer) inventorySlots).setGuiID(id);
    }
  }

  @Override
  public int getGuiID() {
    if (inventorySlots instanceof IRemoteExec.IContainer) {
      return ((IRemoteExec.IContainer) inventorySlots).getGuiID();
    }
    return -1;
  }

  // expose for GenericBar
  @Override
  public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
    super.drawGradientRect(left, top, right, bottom, startColor, endColor);
  }

  static long[] LAYERSEED = null;

  @Override
  public void drawWorldBackground(int tint) {
    if (Celeb.SPACE.isOn() && NullHelper.untrust(mc.world) != null && PersonalConfig.celebrateSpaceDay.get()) {
      drawRect(0, 0, width, height, 0xDF000000);

      long tickCount = EnderIO.proxy.getTickCount();
      Random rand = new Random();

      if (LAYERSEED == null) {
        long[] tmp = new long[10];
        for (int i = 0; i < tmp.length; i++) {
          tmp[i] = rand.nextLong();
        }
        LAYERSEED = tmp;
      }

      for (int layer = 1; layer < 10; layer++) {
        for (int star = 0; star < width - 1; star++) {
          long seed = LAYERSEED[layer] + star + (tickCount / layer);
          rand.setSeed(seed);
          int y = rand.nextInt(height * 10 * layer / 3);
          int r = rand.nextInt(64);
          int g = rand.nextInt(64);
          int b = rand.nextInt(32);
          int color = ((0xFF - r) << 16) | ((0xFF - g) << 8) | (0xFF - b);
          if (y < height) {
            drawRect(star, y, star + 1, y + 1, 0xA0000000 | color);
            drawRect(star + 1, y, star + 2, y + 1, 0x20000000 | color);
          }
        }
      }
      return;
    }

    super.drawWorldBackground(tint);

    if (PersonalConfig.GUIBrandingEnabled.get()) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, PersonalConfig.GUIBrandingAlpha.get());
      RenderUtil.bindTexture(PersonalConfig.GUIBrandingTexture.get());
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);

      int size = Math.min(width, height) / PersonalConfig.GUIBrandingTiles.get();

      drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, size, size);

      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
    }

    if (getOwner() instanceof INotifier) {
      int x = width / 2;
      int y = 4;
      for (ILocalizable notification : ((INotifier) getOwner()).getNotification()) {
        String s = EnderIO.lang.localizeExact(notification.getUnlocalizedName());
        int stringWidth = fontRenderer.getStringWidth(s);
        int xPos = x - stringWidth / 2;
        drawGradientRect(xPos - 1, y - 1, xPos + stringWidth + 1, y + fontRenderer.FONT_HEIGHT, 0xFFFF0000, 0xFFAF0000);
        fontRenderer.drawString(s, xPos, y, 0xFFFFFF, true);
        y += fontRenderer.FONT_HEIGHT + 2;
      }
    }
  }

  public float setZLevel(float level) {
    float old = zLevel;
    zLevel = level;
    return old;
  }

}
