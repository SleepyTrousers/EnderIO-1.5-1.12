package crazypants.enderio.base.filter.gui;

import com.enderio.core.client.gui.button.ToggleButton;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.filters.PowerItemFilter;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import net.minecraft.client.gui.GuiButton;

import javax.annotation.Nonnull;

public class PowerItemFilterGui implements IItemFilterGui {

  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();

  private static final int ID_MORE = FilterGuiUtil.nextButtonId();
  private static final int ID_LEVEL = FilterGuiUtil.nextButtonId();

  private final IItemFilterContainer filterContainer;
  private final GuiContainerBaseEIO gui;

  private final ToggleButton stickyB;
  private final boolean isStickModeAvailable;

  private final GuiButton modeB;
  private final GuiButton levelB;

  private final PowerItemFilter filter;

  public PowerItemFilterGui(@Nonnull GuiContainerBaseEIO gui, @Nonnull IItemFilterContainer filterContainer, boolean isStickyModeAvailable) {
    this.gui = gui;
    this.filterContainer = filterContainer;
    this.isStickModeAvailable = isStickyModeAvailable;

    filter = (PowerItemFilter) filterContainer.getItemFilter();

    int butLeft = 37;
    int x = butLeft;
    int y = 68;

    x += 20;
    stickyB = new ToggleButton(gui, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    String[] lines = EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled");
    stickyB.setSelectedToolTip(lines);
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    modeB = new GuiButton(ID_MORE, 0, 0, 40, 20, "");
    levelB = new GuiButton(ID_LEVEL, 0, 0, 40, 20, "");
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {
  }

  @Override
  public void updateButtons() {
    if(isStickModeAvailable) {
      stickyB.onGuiInit();
      stickyB.setSelected(filter.isSticky());
    }

    int x0 = gui.getGuiLeft() + 80;
    int y0 = gui.getGuiTop() + 65;
    int x1 = x0 + 45;

    modeB.xPosition = x0;
    modeB.yPosition = y0;

    levelB.xPosition = x1;
    levelB.yPosition = y0;

    switch(filter.getMode()) {
      case LESS:       modeB.displayString = "<";  break;
      case LESS_EQUAL: modeB.displayString = "<="; break;
      case EQUAL:      modeB.displayString = "=";  break;
      case MORE_EQUAL: modeB.displayString = ">="; break;
      case MORE:       modeB.displayString = ">";  break;
      default:         modeB.displayString = "??"; break;
    }

    levelB.displayString = String.format("%d%%", filter.getLevel() * 100 / PowerItemFilter.MAX_LEVEL);

    gui.addButton(modeB);
    gui.addButton(levelB);
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    if(guiButton.id == ID_STICKY) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_MORE) {
      filter.setMode(filter.getMode().next());
      sendFilterChange();
    } else if(guiButton.id == ID_LEVEL) {
      filter.setLevel((filter.getLevel() + 1) % (PowerItemFilter.MAX_LEVEL + 1));
      sendFilterChange();
    }
  }

  private void sendFilterChange() {
    updateButtons();
    filterContainer.onFilterChanged();
    //PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
  }

  @Override
  public void deactivate() {
    stickyB.detach();
    gui.removeButton(modeB);
    gui.removeButton(levelB);
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
//    GL11.glColor3f(1, 1, 1);
//    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
//    gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 68, 0, 238, 18 * 5, 18);
//    if(filter.isAdvanced()) {
//      gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 86, 0, 238, 18 * 5, 18);
//    }
  }
}
