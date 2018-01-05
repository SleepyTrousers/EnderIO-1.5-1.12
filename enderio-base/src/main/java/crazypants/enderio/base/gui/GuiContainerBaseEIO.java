package crazypants.enderio.base.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.network.IRemoteExec;
import crazypants.enderio.util.Prep;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiContainerBaseEIO extends GuiContainerBase implements IRemoteExec.IGui {

  private final @Nonnull NNList<ResourceLocation> guiTextures = new NNList<ResourceLocation>();

  public GuiContainerBaseEIO(@Nonnull Container par1Container, String... guiTexture) {
    super(par1Container);
    for (String string : guiTexture) {
      guiTextures.add(EnderIO.proxy.getGuiTexture(NullHelper.notnull(string, "invalid gui texture name")));
    }
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

  private final @Nonnull List<Rectangle> tabAreas = new ArrayList<Rectangle>();
  private final static @Nonnull Rectangle NO_TAB = new Rectangle(0, 0, 0, 0);

  public List<Rectangle> getBlockingAreas() {
    // return a new object every time so equals() actually checks the contents
    return new ArrayList<Rectangle>(tabAreas);
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
      button.xPosition = result.x;
      button.yPosition = result.y;
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
      GlStateManager.color(.9f, .9f, .9f, 1);
    }
    
    VertexBuffer tes = Tessellator.getInstance().getBuffer();
    RenderUtil.bindTexture(IconEIO.map.getTexture());
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

    renderTabPart(tes, x + bg_x, y, IconEIO.TAB_BG.getX() + bg_x, IconEIO.TAB_BG.getY(), bg_w, IconEIO.TAB_BG.getHeight());
    renderTabPart(tes, x + l_x, y, IconEIO.TAB_FRAME_LEFT.getX() + l_x, IconEIO.TAB_FRAME_LEFT.getY(), l_w, IconEIO.TAB_FRAME_LEFT.getHeight());
    renderTabPart(tes, x + r_x, y, IconEIO.TAB_FRAME_RIGHT.getX() + r_u, IconEIO.TAB_FRAME_RIGHT.getY(), r_w, IconEIO.TAB_FRAME_RIGHT.getHeight());

    if (icon != null) {
      icon.getMap().render(icon, x + w / 2 - 8, y + IconEIO.TAB_BG.getHeight() / 2 - 8, false);
    }
    Tessellator.getInstance().draw();

    if (Prep.isValid(stack)) {
      RenderHelper.enableGUIStandardItemLighting();
      itemRender.renderItemIntoGUI(stack, x + w / 2 - 8, y + IconEIO.TAB_BG.getHeight() / 2 - 8);
      RenderHelper.disableStandardItemLighting();
    }
  
    return new Rectangle(x + bg_x, y - 1, bg_w + 3 + 1, IconEIO.TAB_BG.getHeight() + 2);
  }

  private void renderTabPart(@Nonnull VertexBuffer tes, int x, int y, int u, int v, int w, int h) {
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

}
