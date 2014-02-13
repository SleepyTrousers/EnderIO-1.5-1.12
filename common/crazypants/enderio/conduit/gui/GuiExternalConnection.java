package crazypants.enderio.conduit.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.gui.IconEIO;
import crazypants.gui.GuiContainerBase;
import crazypants.render.RenderUtil;

public class GuiExternalConnection extends GuiContainerBase {

  private static final int TAB_HEIGHT = 24;

  InventoryPlayer playerInv;
  IConduitBundle bundle;
  ForgeDirection dir;

  private List<IConduit> conduits = new ArrayList<IConduit>();
  private List<ISettingsPanel> tabs = new ArrayList<ISettingsPanel>();
  private int activeTab = 0;

  private int tabYOffset = 4;

  ExternalConnectionContainer container;

  public GuiExternalConnection(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
    super(new ExternalConnectionContainer(playerInv, bundle, dir));
    container = (ExternalConnectionContainer) inventorySlots;
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;
    container.setInputSlotsVisible(false);
    container.setOutputSlotsVisible(false);
    container.setInventorySlotsVisible(false);
    for (IConduit con : bundle.getConduits()) {
      if(con.containsExternalConnection(dir) || con.canConnectToExternal(dir, true)) {
        ISettingsPanel tab = TabFactory.instance.createPanelForConduit(this, con);
        if(tab != null) {
          conduits.add(con);
          tabs.add(tab);
        }
      }
    }

    ySize = 166 + 29;
  }

  @Override
  public void initGui() {
    super.initGui();
    buttonList.clear();
    for (int i = 0; i < tabs.size(); i++) {
      if(i == activeTab) {
        tabs.get(i).onGuiInit(guiLeft + 10, guiTop + 10, xSize - 20, ySize - 20);
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
  protected void mouseClicked(int x, int y, int par3) {
    super.mouseClicked(x, y, par3);

    int tabLeftX = xSize;
    int tabRightX = tabLeftX + 22;

    int minY = tabYOffset;
    int maxY = minY + (conduits.size() * TAB_HEIGHT);

    x = (x - guiLeft);
    y = (y - guiTop);

    if(x > tabLeftX && x < tabRightX + 24) {
      if(y > minY && y < maxY) {
        activeTab = (y - minY) / 24;
        initGui();
        return;
      }
    }

  }

  public void setSize(int x, int y) {
    xSize = x;
    ySize = y;
  }

  @Override
  protected void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    tabs.get(activeTab).actionPerformed(guiButton);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    int tabX = sx + xSize - 3;

    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    for (int i = 0; i < tabs.size(); i++) {
      if(i != activeTab) {
        RenderUtil.bindTexture(IconEIO.TEXTURE);
        IconEIO.INACTIVE_TAB.renderIcon(tabX, sy + tabYOffset + (i * 24));
        IconEIO icon = tabs.get(i).getIcon();
        icon.renderIcon(tabX + 4, sy + tabYOffset + (i * TAB_HEIGHT) + 7, 10, 10, 0, false);
      }
    }

    tes.draw();

    RenderUtil.bindTexture("enderio:textures/gui/externalConduitConnection.png");
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    RenderUtil.bindTexture(IconEIO.TEXTURE);
    tes.startDrawingQuads();
    IconEIO.ACTIVE_TAB.renderIcon(tabX, sy + tabYOffset + (activeTab * TAB_HEIGHT));

    if(tabs.size() > 0) {
      IconEIO icon = tabs.get(activeTab).getIcon();
      icon.renderIcon(tabX + 4, sy + tabYOffset + (activeTab * TAB_HEIGHT) + 7, 10, 10, 0, false);
      tes.draw();
      tabs.get(activeTab).render(par1, par2, par3);
    } else {
      tes.draw();
    }

  }

}
