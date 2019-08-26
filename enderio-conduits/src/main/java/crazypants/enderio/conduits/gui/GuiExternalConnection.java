package crazypants.enderio.conduits.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IExternalConnectionContainer;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiExternalConnection extends GuiContainerBaseEIO implements IGuiExternalConnection {

  private static int nextButtonId = 1;

  public static int nextButtonId() {
    return nextButtonId++;
  }

  final @Nonnull InventoryPlayer playerInv;
  final @Nonnull IConduitBundle bundle;
  private final @Nonnull EnumFacing dir;

  private final @Nonnull List<ITabPanel> tabs = new ArrayList<ITabPanel>();
  private int activeTab = 0;

  private final IExternalConnectionContainer container;

  public GuiExternalConnection(@Nonnull InventoryPlayer playerInv, @Nonnull IConduitBundle bundle, @Nonnull EnumFacing dir) {
    super(new ExternalConnectionContainer(playerInv, dir, (TileConduitBundle) bundle.getEntity()).init(), "item_filter");
    container = (ExternalConnectionContainer) inventorySlots;
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;

    ySize = 194;
    xSize = 206;

    List<IClientConduit> cons = new ArrayList<>(bundle.getClientConduits());
    Collections.sort(cons, new Comparator<IClientConduit>() {

      @Override
      public int compare(@Nullable IClientConduit o1, @Nullable IClientConduit o2) {
        return Integer.compare(o1 != null ? o1.getGuiPanelTabOrder() : 0, o2 != null ? o2.getGuiPanelTabOrder() : 0);

      }
    });

    /**
     * Note: We actually would need to rebuild this list each tick as other players in MP could add conduits to the bundle. We don't, so we can keep our sanity.
     * 
     * If conduits are added, they won't show up until the GUI is re-opened. If conduits are removed the GUI will close if their tab is selected.
     */

    for (IClientConduit con : cons) {
      if (con.containsExternalConnection(dir) || con.canConnectToExternal(dir, true)) {
        tabs.add(con.createGuiPanel(this, con));
      }
    }
    if (tabs.isEmpty()) {
      Minecraft.getMinecraft().player.closeScreen();
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    buttonList.clear();
    for (int i = 0; i < tabs.size(); i++) {
      if (i != activeTab) {
        tabs.get(i).deactivate();
      }
    }
    if (activeTab < tabs.size()) {
      tabs.get(activeTab).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
    }
  }

  private @Nullable ITabPanel getActiveTab() {
    if (activeTab < tabs.size() && activeTab >= 0) {
      final ITabPanel tab = tabs.get(activeTab);
      if (tab != null) {
        for (IClientConduit con : bundle.getClientConduits()) {
          if (con.updateGuiPanel(tab)) {
            return tab;
          }
        }
      }
    }
    return null;
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  protected void mouseClicked(int x, int y, int par3) throws IOException {
    super.mouseClicked(x, y, par3);

    x = (x - guiLeft);
    y = (y - guiTop);

    if (activeTab < tabs.size())
      tabs.get(activeTab).mouseClicked(x, y, par3);
  }

  @Override
  protected boolean doSwitchTab(int tab) {
    if (tab != activeTab) {
      activeTab = tab;
      initGui();
      return true;
    }
    return super.doSwitchTab(tab);
  }

  @Override
  protected @Nonnull ResourceLocation getGuiTexture() {
    return activeTab < tabs.size() ? tabs.get(activeTab).getTexture() : super.getGuiTexture();
  }

  public void setSize(int x, int y) {
    xSize = x;
    ySize = y;
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    if (activeTab < tabs.size())
      tabs.get(activeTab).actionPerformed(guiButton);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    final ITabPanel tab = getActiveTab();
    if (tab == null) {
      Minecraft.getMinecraft().player.closeScreen();
      return;
    }

    GlStateManager.color(1, 1, 1, 1);

    final int sx = (width - xSize) / 2;
    final int sy = (height - ySize) / 2;

    Minecraft.getMinecraft().getTextureManager().bindTexture(tab.getTexture());
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    startTabs();
    for (int i = 0; i < tabs.size(); i++) {
      renderStdTab(sx, sy, i, tabs.get(i).getIcon(), i == activeTab);
    }

    tab.render(par1, par2, par3);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  @Nonnull
  public EnumFacing getDir() {
    return dir;
  }

  @Override
  public IExternalConnectionContainer getContainer() {
    return container;
  }

  @Override
  public void drawFakeItemStack(int x, int y, @Nonnull ItemStack stack) {
    super.drawFakeItemStack(x, y, stack);
    itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, "");
  }

}
