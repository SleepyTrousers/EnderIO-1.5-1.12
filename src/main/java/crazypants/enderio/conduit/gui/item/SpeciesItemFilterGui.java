package crazypants.enderio.conduit.gui.item;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.filter.SpeciesItemFilter;
import crazypants.enderio.conduit.item.filter.SpeciesMode;
import net.minecraft.client.gui.GuiButton;

public class SpeciesItemFilterGui implements IItemFilterGui {

  private static final int ID_WHITELIST = GuiExternalConnection.nextButtonId();
  private static final int ID_SPECIES_MODE = GuiExternalConnection.nextButtonId();
  private static final int ID_STICKY = GuiExternalConnection.nextButtonId();

  private final GuiContainerBaseEIO gui;

  private final IconButton whiteListB;
  private final CycleButton<SpeciesMode.IconHolder> speciesModeB;
  private final ToggleButton stickyB;

  final boolean isStickyModeAvailable;

  private final IItemFilterContainer filterContainer;
  private final SpeciesItemFilter filter;

  private int buttonIdOffset;
  private int xOffset;
  private int yOffset;

  public SpeciesItemFilterGui(GuiContainerBaseEIO gui, IItemFilterContainer filterContainer, boolean isStickyModeAvailable) {
    this(gui, filterContainer, isStickyModeAvailable, 32, 68, 0);
  }

  public SpeciesItemFilterGui(GuiContainerBaseEIO gui, IItemFilterContainer filterContainer, boolean isStickyModeAvailable,
                              int xOffset, int yOffset,
                              int buttonIdOffset) {
    this.gui = gui;
    this.isStickyModeAvailable = isStickyModeAvailable;
    this.filterContainer = filterContainer;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.buttonIdOffset = buttonIdOffset;

    filter = (SpeciesItemFilter) filterContainer.getItemFilter();

    int butLeft = xOffset + 92;
    int x = butLeft;
    int y = yOffset + 1;
    whiteListB = new IconButton(gui, ID_WHITELIST + buttonIdOffset, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));

    x += 20;
    stickyB = new ToggleButton(gui, ID_STICKY + buttonIdOffset, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled"));
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = butLeft;

    speciesModeB = new CycleButton<SpeciesMode.IconHolder>(gui, ID_SPECIES_MODE + buttonIdOffset, x, y, SpeciesMode.IconHolder.class);
  }

  public void createFilterSlots() {
    filter.createGhostSlots(gui.getGhostSlots(), xOffset+1, yOffset+1, new Runnable() {
      @Override
      public void run() {
        sendFilterChange();
      }
    });
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {      
  }
  
  @Override
  public void updateButtons() {
    if(isStickyModeAvailable) {
      stickyB.onGuiInit();
      stickyB.setSelected(filter.isSticky());
    }

    whiteListB.onGuiInit();
    if(filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.blacklist"));
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));
    }

    speciesModeB.onGuiInit();
    speciesModeB.setMode(SpeciesMode.IconHolder.getFromMode(filter.getSpeciesMode()));
  }
  
  
  @Override
  public void actionPerformed(GuiButton guiButton) {
    
    if(guiButton.id == ID_STICKY + buttonIdOffset) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_SPECIES_MODE + buttonIdOffset) {
      filter.setSpeciesMode(speciesModeB.getMode().getMode());
      sendFilterChange();
    } else if(guiButton.id == ID_WHITELIST + buttonIdOffset) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    } 
  }
  
  private void sendFilterChange() {
    updateButtons();
    filterContainer.onFilterChanged();
  }
  
  @Override
  public void deactivate() {
    whiteListB.detach();
    stickyB.detach();
    speciesModeB.detach();
  }
  
  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    GL11.glColor3f(1, 1, 1);
    gui.bindGuiTexture(1);
    gui.drawTexturedModalRect(gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset, 0, 238, 18 * 5, 18);
    gui.drawTexturedModalRect(gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset + 20, 0, 238, 18 * 5, 18);
  }
  
}
