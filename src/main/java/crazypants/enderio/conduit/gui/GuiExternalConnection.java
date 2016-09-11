package crazypants.enderio.conduit.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.common.Optional;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.gas.IGasConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.oc.IOCConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;

public class GuiExternalConnection extends GuiContainerBaseEIO {

  private static final int TAB_HEIGHT = 24;

  private static int nextButtonId = 1;

  public static int nextButtonId() {
    return nextButtonId++;
  }

  private static final Map<Class<? extends IConduit>, Integer> TAB_ORDER = new HashMap<Class<? extends IConduit>, Integer>();
  static {
    TAB_ORDER.put(IItemConduit.class, 0);
    TAB_ORDER.put(ILiquidConduit.class, 1);
    TAB_ORDER.put(IRedstoneConduit.class, 2);
    TAB_ORDER.put(IPowerConduit.class, 3);
    TAB_ORDER.put(IMEConduit.class, 4);
    TAB_ORDER.put(IGasConduit.class, 5);
    TAB_ORDER.put(IOCConduit.class, 6);
  }

  final InventoryPlayer playerInv;
  final IConduitBundle bundle;
  private final ForgeDirection dir;

  private final List<IConduit> conduits = new ArrayList<IConduit>();
  private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
  private int activeTab = 0;

  private int tabYOffset = 4;

  private final ExternalConnectionContainer container;

  public GuiExternalConnection(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
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
        Integer int1 = TAB_ORDER.get(o1.getBaseConduitType());
        if(int1 == null) {
          return 1;
        }
        Integer int2 = TAB_ORDER.get(o2.getBaseConduitType());
        if(int2 == null) {
          return 1;
        }
        //NB: using Double.comp instead of Integer.comp as the int version is only from Java 1.7+
        return Double.compare(int1, int2);

      }
    });

    for (IConduit con : cons) {
      if(con.containsExternalConnection(dir) || con.canConnectToExternal(dir, true)) {
        @SuppressWarnings("LeakingThisInConstructor")
        ITabPanel tab = TabFactory.instance.createPanelForConduit(this, con);
        if(tab != null) {
          conduits.add(con);
          tabs.add(tab);
        }
      }
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
    tabs.get(activeTab).mouseClicked(x, y, par3);

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
        IconEIO.map.render(IconEIO.INACTIVE_TAB, tabX, sy + tabYOffset + (i * 24));
        IWidgetIcon icon = tabs.get(i).getIcon();
        icon.getMap().render(icon, tabX - 1, sy + tabYOffset + (i * TAB_HEIGHT) + 4);
      }
    }

    tes.draw();

    bindGuiTexture();
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    RenderUtil.bindTexture(IconEIO.TEXTURE);
    tes.startDrawingQuads();
    IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX, sy + tabYOffset + (activeTab * TAB_HEIGHT));

    if(tabs.size() > 0) {
      IWidgetIcon icon = tabs.get(activeTab).getIcon();
      icon.getMap().render(icon, tabX - 1, sy + tabYOffset + (activeTab * TAB_HEIGHT) + 4);
      tes.draw();
      tabs.get(activeTab).render(par1, par2, par3);
    } else {
      tes.draw();
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  public ForgeDirection getDir() {
    return dir;
  }

  public ExternalConnectionContainer getContainer() {
    return container;
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
    if(tabs.size() > 0) {
      int sx = (width - xSize) / 2;
      int sy = (height - ySize) / 2;
      int tabX = sx + xSize - 3;
      int tabY = sy + tabYOffset;

      return (x+w) >= tabX && x < (tabX + 14) && (y+h) >= tabY && y < (tabY + tabs.size()*TAB_HEIGHT);
    }
    return false;
  }

  @Override
  protected void drawFakeItemStack(int x, int y, ItemStack stack) {
    super.drawFakeItemStack(x, y, stack);
    itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y, "");
  }

  public void clearGhostSlots() {
    getGhostSlots().clear();
    ((ExternalConnectionContainer) inventorySlots).createGhostSlots(getGhostSlots());
  }

}
