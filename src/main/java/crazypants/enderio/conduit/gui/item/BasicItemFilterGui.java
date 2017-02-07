package crazypants.enderio.conduit.gui.item;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.filter.DamageModeIconHolder;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import net.minecraft.client.gui.GuiButton;

public class BasicItemFilterGui implements IItemFilterGui {
    
  private static final int ID_WHITELIST = GuiExternalConnection.nextButtonId();
  private static final int ID_NBT = GuiExternalConnection.nextButtonId();
  private static final int ID_META = GuiExternalConnection.nextButtonId();
  private static final int ID_ORE_DICT = GuiExternalConnection.nextButtonId();
  private static final int ID_STICKY = GuiExternalConnection.nextButtonId();    
  private static final int ID_DAMAGE = GuiExternalConnection.nextButtonId();
  
  private final GuiContainerBaseEIO gui;
  
  private final ToggleButton useMetaB;
  private final ToggleButton useNbtB;
  private final IconButton whiteListB;
  private final ToggleButton useOreDictB;
  private final ToggleButton stickyB;
  private final CycleButton<DamageModeIconHolder> damageB;
    
  final boolean isAdvanced;
  final boolean isStickyModeAvailable;
  
  private final IItemFilterContainer filterContainer;
  private final ItemFilter filter;
  
  private int buttonIdOffset;
  private int xOffset;
  private int yOffset;

  public BasicItemFilterGui(GuiContainerBaseEIO gui, IItemFilterContainer filterContainer, boolean isStickyModeAvailable) {
    this(gui, filterContainer, isStickyModeAvailable, 32, 68, 0);
  }

  public BasicItemFilterGui(GuiContainerBaseEIO gui, IItemFilterContainer filterContainer, boolean isStickyModeAvailable,
      int xOffset, int yOffset,
      int buttonIdOffset) {
    this.gui = gui;
    this.isStickyModeAvailable = isStickyModeAvailable;
    this.filterContainer = filterContainer;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.buttonIdOffset = buttonIdOffset;

    filter = (ItemFilter) filterContainer.getItemFilter();
    
    isAdvanced = filter.isAdvanced();
    
    int butLeft = xOffset + 92;
    int x = butLeft;
    int y = yOffset + 1;
    whiteListB = new IconButton(gui, ID_WHITELIST + buttonIdOffset, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));

    x += 20;
    useMetaB = new ToggleButton(gui, ID_META + buttonIdOffset, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    useMetaB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchMetaData"));
    useMetaB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreMetaData"));
    useMetaB.setPaintSelectedBorder(false);

    x += 20;
    stickyB = new ToggleButton(gui, ID_STICKY + buttonIdOffset, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled"));
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = butLeft;

    useOreDictB = new ToggleButton(gui, ID_ORE_DICT + buttonIdOffset, x, y, IconEIO.FILTER_ORE_DICT_OFF, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicEnabled"));
    useOreDictB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicDisabled"));
    useOreDictB.setPaintSelectedBorder(false);

    x += 20;
    useNbtB = new ToggleButton(gui, ID_NBT + buttonIdOffset, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
    useNbtB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchNBT"));
    useNbtB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreNBT"));
    useNbtB.setPaintSelectedBorder(false);

    x += 20;
    damageB = new CycleButton<DamageModeIconHolder>(gui, ID_DAMAGE + buttonIdOffset, x, y, DamageModeIconHolder.class);
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
    ItemFilter activeFilter = filter;

    if(isAdvanced) {
      useNbtB.onGuiInit();
      useNbtB.setSelected(activeFilter.isMatchNBT());

      useOreDictB.onGuiInit();
      useOreDictB.setSelected(activeFilter.isUseOreDict());
      
      if(isStickyModeAvailable) {
        stickyB.onGuiInit();
        stickyB.setSelected(activeFilter.isSticky());
      }

      damageB.onGuiInit();
      damageB.setMode(DamageModeIconHolder.getFromMode(activeFilter.getDamageMode()));
    }

    useMetaB.onGuiInit();
    useMetaB.setSelected(activeFilter.isMatchMeta());

    whiteListB.onGuiInit();
    if(activeFilter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.blacklist"));
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));
    }
  }
  
  
  @Override
  public void actionPerformed(GuiButton guiButton) {
    
    if(guiButton.id == ID_META + buttonIdOffset) {
      filter.setMatchMeta(useMetaB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_NBT + buttonIdOffset) {
      filter.setMatchNBT(useNbtB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_STICKY + buttonIdOffset) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_ORE_DICT + buttonIdOffset) {
      filter.setUseOreDict(useOreDictB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_DAMAGE + buttonIdOffset) {
      filter.setDamageMode(damageB.getMode().getMode());
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
    useNbtB.detach();
    useMetaB.detach();
    useOreDictB.detach();
    whiteListB.detach();
    stickyB.detach();
    damageB.detach();
  }
  
  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    GL11.glColor3f(1, 1, 1);
    gui.bindGuiTexture(1);
    gui.drawTexturedModalRect(gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset, 0, 238, 18 * 5, 18);
    if(filter.isAdvanced()) {      
      gui.drawTexturedModalRect(gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset + 20, 0, 238, 18 * 5, 18);
    }
  }
  
}
