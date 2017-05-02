package crazypants.enderio.conduit.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enderio.core.api.client.gui.ITabPanel;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class GuiExternalConnection extends GuiContainerBaseEIO {

  private static int nextButtonId = 1;

  public static int nextButtonId() {
    return nextButtonId++;
  }

  final InventoryPlayer playerInv;
  final IConduitBundle bundle;
  private final EnumFacing dir;

  private final List<IConduit> conduits = new ArrayList<IConduit>();
  private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
  private int activeTab = 0;

  private final ExternalConnectionContainer container;

  public GuiExternalConnection(InventoryPlayer playerInv, IConduitBundle bundle, EnumFacing dir) {
    super(new ExternalConnectionContainer(playerInv, bundle, dir), "externalConduitConnection", "itemFilter");
    container = (ExternalConnectionContainer) inventorySlots;
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;

    ySize = 166 + 29;
    xSize = 206;

    container.setInoutSlotsVisible(false, false);
    container.setInventorySlotsVisible(false);

    List<IConduit> cons = new ArrayList<IConduit>(bundle.getConduits());
    Collections.sort(cons, new Comparator<IConduit>() {

      @Override
      public int compare(IConduit o1, IConduit o2) {
        //NB: using Double.comp instead of Integer.comp as the int version is only from Java 1.7+
        return Double.compare(o1.getTabOrderForConduit(o1), o2.getTabOrderForConduit(o2));

      }
    });

    for (IConduit con : cons) {
      if(con.containsExternalConnection(dir) || con.canConnectToExternal(dir, true)) {
        ITabPanel tab = con.createPanelForConduit(this, con);
        if(tab != null) {
          conduits.add(con);
          tabs.add(tab);
        }
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
    ((ExternalConnectionContainer) inventorySlots).createGhostSlots(getGhostSlots());
    for (int i = 0; i < tabs.size(); i++) {
      if(i == activeTab) {
        tabs.get(i).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
      } else {
        tabs.get(i).deactivate();
      }
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  protected void mouseClicked(int x, int y, int par3) throws IOException {
    super.mouseClicked(x, y, par3);

    int tabFromCoords = getTabFromCoords(x, y);
    if (tabFromCoords >= 0) {
      activeTab = tabFromCoords;
      initGui();
      return;
    }

    x = (x - guiLeft);
    y = (y - guiTop);

    if (activeTab < tabs.size())
    tabs.get(activeTab).mouseClicked(x, y, par3);

  }

  public void setSize(int x, int y) {
    xSize = x;
    ySize = y;
  }

  @Override
  protected void actionPerformed(GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    if (activeTab < tabs.size())
    tabs.get(activeTab).actionPerformed(guiButton);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1, 1, 1, 1);

    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    bindGuiTexture();
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    
    startTabs();
    for (int i = 0; i < tabs.size(); i++) {
      renderStdTab(sx, sy, i, tabs.get(i).getIcon(), i == activeTab);
    }

    if (activeTab < tabs.size())
    tabs.get(activeTab).render(par1, par2, par3);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  public EnumFacing getDir() {
    return dir;
  }

  public ExternalConnectionContainer getContainer() {
    return container;
  }

//  @Override
//  @Optional.Method(modid = "NotEnoughItems")
//  public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
//    if(tabs.size() > 0) {
//      int sx = (width - xSize) / 2;
//      int sy = (height - ySize) / 2;
//      int tabX = sx + xSize - 3;
//      int tabY = sy + tabYOffset;
//
//      return (x+w) >= tabX && x < (tabX + 14) && (y+h) >= tabY && y < (tabY + tabs.size()*TAB_HEIGHT);
//    }
//    return false;
//  }

  @Override
  protected void drawFakeItemStack(int x, int y, ItemStack stack) {
    super.drawFakeItemStack(x, y, stack);
    itemRender.renderItemOverlayIntoGUI(fontRendererObj, stack, x, y, "");    
  }

  public void clearGhostSlots() {
    getGhostSlots().clear();
    ((ExternalConnectionContainer) inventorySlots).createGhostSlots(getGhostSlots());
  }

}
