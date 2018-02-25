package crazypants.enderio.conduit.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IExternalConnectionContainer;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.conduit.TileConduitBundle;
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

  final InventoryPlayer playerInv;
  final IConduitBundle bundle;
  private final @Nonnull EnumFacing dir;

  private final List<IConduit> conduits = new ArrayList<IConduit>();
  private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
  private int activeTab = 0;

  private final IExternalConnectionContainer container;

  public GuiExternalConnection(@Nonnull InventoryPlayer playerInv, @Nonnull IConduitBundle bundle, @Nonnull EnumFacing dir) {
    super(new ExternalConnectionContainer(playerInv, dir, (TileConduitBundle) bundle.getEntity()), "item_filter");
    container = (ExternalConnectionContainer) inventorySlots;
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;

    ySize = 194;
    xSize = 206;

    List<IConduit> cons = new ArrayList<IConduit>(bundle.getConduits());
    Collections.sort(cons, new Comparator<IConduit>() {

      @Override
      public int compare(@Nullable IConduit o1, @Nullable IConduit o2) {
        return Integer.compare(o1.getGuiPanelTabOrder(), o2.getGuiPanelTabOrder());

      }
    });

    for (IConduit con : cons) {
      if (con.containsExternalConnection(dir) || con.canConnectToExternal(dir, true)) {
        ITabPanel tab = con.createGuiPanel(this, con);
        if (tab != null) {
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
    ((ExternalConnectionContainer) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
    for (int i = 0; i < tabs.size(); i++) {
      if (i == activeTab) {
        tabs.get(i).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
      } else {
        tabs.get(i).deactivate();
      }
    }
  }

  private @Nullable ITabPanel getActiveTab() {
    if (activeTab < tabs.size() && activeTab >= 0) {
      return tabs.get(activeTab);
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

    ITabPanel tab = getActiveTab();
    if (tab != null)
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
    return tabs.get(activeTab).getTexture();
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
    GlStateManager.color(1, 1, 1, 1);

    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    ITabPanel tab = getActiveTab();

    if (tab != null) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(tab.getTexture());
      drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    }

    startTabs();
    for (int i = 0; i < tabs.size(); i++) {
      renderStdTab(sx, sy, i, tabs.get(i).getIcon(), i == activeTab);
    }

    if (activeTab < tabs.size())
      tabs.get(activeTab).render(par1, par2, par3);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  @Nonnull
  public EnumFacing getDir() {
    return dir;
  }

  public IExternalConnectionContainer getContainer() {
    return container;
  }

  // @Override
  // @Optional.Method(modid = "NotEnoughItems")
  // public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
  // if(tabs.size() > 0) {
  // int sx = (width - xSize) / 2;
  // int sy = (height - ySize) / 2;
  // int tabX = sx + xSize - 3;
  // int tabY = sy + tabYOffset;
  //
  // return (x+w) >= tabX && x < (tabX + 14) && (y+h) >= tabY && y < (tabY + tabs.size()*TAB_HEIGHT);
  // }
  // return false;
  // }

  @Override
  public void drawFakeItemStack(int x, int y, @Nonnull ItemStack stack) {
    super.drawFakeItemStack(x, y, stack);
    itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, "");
  }

}
