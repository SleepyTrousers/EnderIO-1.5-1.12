package crazypants.enderio.machines.machine.transceiver.gui;
/*package crazypants.enderio.base.machines.machine.transceiver.gui;

import java.awt.Color;
import java.awt.Point;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.gui.item.BasicItemFilterGui;
import crazypants.enderio.base.conduit.gui.item.IItemFilterContainer;
import crazypants.enderio.base.filter.filters.ItemFilter;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machines.machine.transceiver.PacketItemFilter;
import crazypants.enderio.base.machines.machine.transceiver.TileTransceiver;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class FilterTab implements ITabPanel {

  private final GuiTransceiver parent;
  private final ContainerTransceiver container;
  private final BasicItemFilterGui sendGui;
  private final BasicItemFilterGui recGui;

  private final MultiIconButton sendRecB;

  boolean showSend = true;

  FilterTab(GuiTransceiver parent) {
    this.parent = parent;
    container = parent.getContainer();
    sendGui = new BasicItemFilterGui(parent, new FilterContainer(parent.getTransciever(), true), false, container.getFilterOffset().x,
        container.getFilterOffset().y, 0);
    recGui = new BasicItemFilterGui(parent, new FilterContainer(parent.getTransciever(), false), false, container.getFilterOffset().x,
        container.getFilterOffset().y, 20);

    sendRecB = MultiIconButton.createRightArrowButton(parent, 8888, container.getFilterOffset().x + 79, container.getFilterOffset().y - 20);
    sendRecB.setSize(10, 16);
  }

  @Override
  public void onGuiInit(int x, int y, int width, int height) {
    parent.getContainer().setPlayerInventoryVisible(true);
    sendRecB.onGuiInit();
    updateSendRecieve();
  }

  protected void updateSendRecieve() {
    parent.getGhostSlots().clear();
    if (showSend) {
      sendGui.updateButtons();
      recGui.deactivate();
      sendGui.createFilterSlots();
    } else {
      sendGui.deactivate();
      recGui.updateButtons();
      recGui.createFilterSlots();
    }
  }

  @Override
  public void deactivate() {
    parent.getContainer().setPlayerInventoryVisible(false);
    sendGui.deactivate();
    recGui.deactivate();
    sendRecB.detach();
  }

  @Override
  public IconEIO getIcon() {
    return IconEIO.FILTER;
  }

  @Override
  public void render(float par1, int par2, int par3) {
    int top = parent.getGuiTop();
    int left = parent.getGuiLeft();

    GL11.glColor3f(1, 1, 1);

    // Inventory
    parent.bindGuiTexture();
    Point invRoot = container.getPlayerInventoryOffset();
    parent.drawTexturedModalRect(left + invRoot.x - 1, top + invRoot.y - 1, 24, 180, 162, 76);

    if (showSend) {
      sendGui.renderCustomOptions(0, par1, par2, par3);
    } else {
      recGui.renderCustomOptions(0, par1, par2, par3);
    }

    String txt = EnderIO.lang.localize("gui.machine.sendfilter");
    if (!showSend) {
      txt = EnderIO.lang.localize("gui.machine.receivefilter");
    }
    FontRenderer fr = parent.getFontRenderer();
    int x = left + container.getFilterOffset().x;
    int y = top - fr.FONT_HEIGHT + container.getFilterOffset().y - 7;
    fr.drawStringWithShadow(txt, x, y, ColorUtil.getRGB(Color.WHITE));

    // sendRecB.xPosition = left + container.getFilterOffset().x + fr.getStringWidth(txt) + 10;
    // System.out.println("FilterTab.enclosing_method: " + (fr.getStringWidth(txt) + 10));
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    if (guiButton == sendRecB) {
      showSend = !showSend;
      updateSendRecieve();
    }
    if (showSend) {
      sendGui.actionPerformed(guiButton);
    } else {
      recGui.actionPerformed(guiButton);
    }
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {
    if (showSend) {
      sendGui.mouseClicked(x, y, par3);
    } else {
      recGui.mouseClicked(x, y, par3);
    }
  }

  @Override
  public void keyTyped(char par1, int par2) {
    parent.doDefaultKeyTyped(par1, par2);
  }

  @Override
  public void updateScreen() {
  }

  private static class FilterContainer implements IItemFilterContainer {

    private final TileTransceiver trans;
    private final boolean isSend;

    private FilterContainer(TileTransceiver trans, boolean isSend) {
      this.trans = trans;
      this.isSend = isSend;
    }

    @Override
    public ItemFilter getItemFilter() {
      if (isSend) {
        return trans.getSendItemFilter();
      }
      return trans.getReceiveItemFilter();
    }

    @Override
    public void onFilterChanged() {
      PacketHandler.INSTANCE.sendToServer(new PacketItemFilter(trans, isSend));
    }

  }

}
*/