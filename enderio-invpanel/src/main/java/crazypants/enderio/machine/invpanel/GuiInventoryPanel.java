package crazypants.enderio.machine.invpanel;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.gui.widget.VScrollbar;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.integration.jei.JeiAccessor;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiMachineBase;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.machine.invpanel.client.CraftingHelper;
import crazypants.enderio.machine.invpanel.client.DatabaseView;
import crazypants.enderio.machine.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.machine.invpanel.client.ItemEntry;
import crazypants.enderio.machine.invpanel.client.SortOrder;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInventoryPanel extends GuiMachineBase<TileInventoryPanel> {

  private static final Rectangle RECTANGLE_FUEL_TANK = new Rectangle(36, 133, 16, 47);

  private static final Rectangle inventoryArea = new Rectangle(24 + 107, 27, 108, 90);

  private static final Rectangle btnRefill = new Rectangle(24 + 85, 32, 20, 20);

  private static final Rectangle btnReturnArea = new Rectangle(24 + 6, 72, 5 * 18, 8);

  private static final int ID_SORT = 9876;
  private static final int ID_CLEAR = 9877;
  private static final int ID_SYNC = 9878;

  private static final int GHOST_COLUMNS = 6;
  private static final int GHOST_ROWS = 5;

  private final DatabaseView view;

  private final TextFieldEnder tfFilter;
  private String tfFilterExternalValue = null;
  private final IconButton btnSort;
  private final ToggleButton btnSync;
  private final GuiToolTip ttRefill;
  private final VScrollbar scrollbar;
  private final MultiIconButton btnClear;

  private int scrollPos;
  private int ghostSlotTooltipStacksize;

  private final String headerCrafting;
  private final String headerReturn;
  private final String headerStorage;
  private final String headerInventory;
  private final String infoTextFilter;
  private final String infoTextOffline;

  private CraftingHelper craftingHelper;

  private final Rectangle btnAddStoredRecipe = new Rectangle();

  // TODO this class makes heavy use of ghost slots
  public GuiInventoryPanel(TileInventoryPanel te, Container container) {
    super(te, container, "inventorypanel");
    redstoneButton.visible = false;
    configB.visible = false;

    for (int y = 0; y < GHOST_ROWS; y++) {
      for (int x = 0; x < GHOST_COLUMNS; x++) {
        getGhostSlots().add(new InvSlot(24 + 108 + x * 18, 28 + y * 18));
      }
    }

    for (int i = 0; i < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES; i++) {
      getGhostSlots().add(new RecipeSlot(i, 7, 7 + i * 20));
    }

    this.view = new DatabaseView();

    int sortMode = te.getGuiSortMode();
    int sortOrderIdx = sortMode >> 1;
    SortOrder[] orders = SortOrder.values();
    if (sortOrderIdx >= 0 && sortOrderIdx < orders.length) {
      view.setSortOrder(orders[sortOrderIdx], (sortMode & 1) != 0);
    }

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    tfFilter = new TextFieldEnder(fr, 24 + 108, 11, 106, 10);
    tfFilter.setEnableBackgroundDrawing(false);
    tfFilter.setText(te.getGuiFilterString());

    btnSync = new ToggleButton(this, ID_SYNC, 24 + 233, 46, IconEIO.CROSS, IconEIO.TICK);
    btnSync.setSelected(getTileEntity().getGuiSync());
    btnSync.setSelectedToolTip(EnderIO.lang.localize("gui.enabled"));
    btnSync.setUnselectedToolTip(EnderIO.lang.localize("gui.disabled"));
    if (Loader.isModLoaded("NotEnoughItems")) {
      btnSync.setToolTip(EnderIO.lang.localize("gui.inventorypanel.tooltip.sync.nei"));
      if (getTileEntity().getGuiSync()) {
        updateNEI(tfFilter.getText());
      }
    } else if (JeiAccessor.isJeiRuntimeAvailable()) {
      btnSync.setToolTip(EnderIO.lang.localize("gui.inventorypanel.tooltip.sync.jei"));
      btnSync.setSelectedToolTip(EnderIO.lang.localize("gui.enabled"), EnderIO.lang.localize("gui.inventorypanel.tooltip.sync.jei.line1"),
          EnderIO.lang.localize("gui.inventorypanel.tooltip.sync.jei.line2"));
      btnSync.setUnselectedToolTip(EnderIO.lang.localize("gui.disabled"));
      if (getTileEntity().getGuiSync()) {
        if (te.getGuiFilterString() != null && !te.getGuiFilterString().isEmpty()) {
          updateToJEI(te.getGuiFilterString());
        } else {
          updateFromJEI();
        }
      }
    } else {
      btnSync.setToolTip(EnderIO.lang.localize("gui.inventorypanel.tooltip.sync"));
      btnSync.setSelectedToolTip(EnderIO.lang.localize("gui.enabled"));
      btnSync.setUnselectedToolTip(EnderIO.lang.localize("gui.disabled"));
      btnSync.enabled = false;
    }

    btnSort = new IconButton(this, ID_SORT, 24 + 233, 27, getSortOrderIcon()) {
      @Override
      public boolean mousePressed(Minecraft mc1, int x, int y) {
        return mousePressedButton(mc1, x, y, 0);
      }

      @Override
      public boolean mousePressedButton(Minecraft mc1, int x, int y, int button) {
        if (button <= 1 && super.checkMousePress(mc1, x, y)) {
          toggleSortOrder(button == 0);
          return true;
        }
        return false;
      }
    };

    scrollbar = new VScrollbar(this, 24 + 215, 27, 90);
    btnClear = new MultiIconButton(this, ID_CLEAR, 24 + 65, 60, EnderWidget.X_BUT, EnderWidget.X_BUT_PRESSED, EnderWidget.X_BUT_HOVER);

    textFields.add(tfFilter);

    headerCrafting = EnderIO.lang.localize("gui.inventorypanel.header.crafting");
    headerReturn = EnderIO.lang.localize("gui.inventorypanel.header.return");
    headerStorage = EnderIO.lang.localize("gui.inventorypanel.header.storage");
    headerInventory = EnderIO.lang.localizeExact("container.inventory");
    infoTextFilter = EnderIO.lang.localize("gui.inventorypanel.info.filter");
    infoTextOffline = EnderIO.lang.localize("gui.inventorypanel.info.offline");

    ArrayList<String> list = new ArrayList<String>();

    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.return.line");
    addToolTip(new GuiToolTip(btnReturnArea, list) {
      @Override
      public boolean shouldDraw() {
        return super.shouldDraw() && !getTileEntity().isExtractionDisabled();
      }
    });

    list.clear();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.storage.line");
    addToolTip(new GuiToolTip(btnReturnArea, list) {
      @Override
      public boolean shouldDraw() {
        return super.shouldDraw() && getTileEntity().isExtractionDisabled();
      }
    });

    list.clear();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.filterslot.line");
    addToolTip(new GuiToolTip(new Rectangle(InventoryPanelContainer.FILTER_SLOT_X, InventoryPanelContainer.FILTER_SLOT_Y, 16, 16), list) {
      @Override
      public boolean shouldDraw() {
        return !getContainer().getSlotFilter().getHasStack() && super.shouldDraw();
      }
    });

    list.clear();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.refill.line");
    ttRefill = new GuiToolTip(btnRefill, list);
    ttRefill.setIsVisible(false);
    addToolTip(ttRefill);

    list.clear();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.clear.line");
    btnClear.setToolTip(list.toArray(new String[list.size()]));

    if (!Config.inventoryPanelFree) {
      addToolTip(new GuiToolTip(RECTANGLE_FUEL_TANK, "") {
        @Override
        protected void updateText() {
          text.clear();
          text.add(EnderIO.lang.localize("gui.inventorypanel.tooltip.fuelTank"));
          text.add(LangFluid.MB(getTileEntity().fuelTank));
        }
      });
    }

    list.clear();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.addrecipe.line");
    addToolTip(new GuiToolTip(btnAddStoredRecipe, list));
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_FUEL_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().fuelTank.getFluid();
    }
    GhostSlot slot = getGhostSlot(mouseX + getGuiLeft(), mouseY + getGuiTop());
    if (slot instanceof InvSlot || slot instanceof RecipeSlot) {
      return slot.getStack();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  public void syncSettingsChange() {
    int sortMode = (view.getSortOrder().ordinal() << 1);
    if (view.isSortOrderInverted()) {
      sortMode |= 1;
    }
    String filterText;
    if (!btnSync.isSelected() || tfFilterExternalValue == null || !tfFilterExternalValue.equals(tfFilter.getText())) {
      filterText = tfFilter.getText();
    } else {
      filterText = "";
    }
    if (getTileEntity().getGuiSortMode() != sortMode || getTileEntity().getGuiSync() != btnSync.isSelected()
        || !org.apache.commons.lang3.StringUtils.equals(getTileEntity().getGuiFilterString(), filterText)) {
      PacketHandler.INSTANCE.sendToServer(new PacketGuiSettings(getContainer().windowId, sortMode, filterText, btnSync.isSelected()));
      getTileEntity().setGuiParameter(sortMode, tfFilter.getText(), btnSync.isSelected());
    }
  }

  public void setCraftingHelper(CraftingHelper craftingHelper) {
    if (this.craftingHelper != null) {
      this.craftingHelper.remove();
    }
    this.craftingHelper = craftingHelper;
    ttRefill.setIsVisible(craftingHelper != null);
    if (craftingHelper != null) {
      craftingHelper.install();
    }
  }

  public void fillFromStoredRecipe(int index, boolean shift) {
    StoredCraftingRecipe recipe = getTileEntity().getStoredCraftingRecipe(index);
    if (recipe == null) {
      return;
    }
    if (getContainer().clearCraftingGrid()) {
      CraftingHelper helper = CraftingHelper.createFromRecipe(recipe);
      setCraftingHelper(helper);
      helper.refill(this, shift ? 64 : 1);
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    updateSortButton();
    btnSort.onGuiInit();
    btnClear.onGuiInit();
    btnSync.onGuiInit();
    addScrollbar(scrollbar);
    ((InventoryPanelContainer) inventorySlots).createGhostSlots(getGhostSlots());
  }

  @Override
  public void actionPerformed(GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b.id == ID_CLEAR) {
      if (getContainer().clearCraftingGrid()) {
        setCraftingHelper(null);
      }
    } else if (b.id == ID_SYNC) {
      if (Loader.isModLoaded("NotEnoughItems")) {
        updateNEI(((ToggleButton) b).isSelected() ? tfFilter.getText() : "");
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    syncSettingsChange();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = guiLeft;
    int sy = guiTop;

    drawTexturedModalRect(sx + 24, sy, 0, 0, 232, ySize);
    drawTexturedModalRect(sx + 24 + 232, sy, 232, 0, 24, 68);

    if (craftingHelper != null || getContainer().hasCraftingRecipe()) {
      boolean hover = btnRefill.contains(mouseX - sx, mouseY - sy);
      int iconX = hover ? (isShiftKeyDown() ? 48 : 24) : 0;
      drawTexturedModalRect(sx + btnRefill.x - 2, sy + btnRefill.y - 2, iconX, 232, 24, 24);
    }

    TileInventoryPanel te = getTileEntity();

    int y = sy;
    int numStoredRecipes = te.getStoredCraftingRecipes();
    if (numStoredRecipes == 1) {
      drawTexturedModalRect(sx, y, 227, 225, 28, 30);
      y += 30;
    } else if (numStoredRecipes > 1) {
      drawTexturedModalRect(sx, y, 227, 225, 28, 24);
      y += 24;
      for (int i = 1; i < numStoredRecipes - 1; i++) {
        drawTexturedModalRect(sx, y, 198, 229, 28, 20);
        y += 20;
      }
      drawTexturedModalRect(sx, y, 198, 229, 28, 26);
      y += 26;
    }

    if (numStoredRecipes < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES && getContainer().hasNewCraftingRecipe()) {
      y += 2;
      btnAddStoredRecipe.x = 13;
      btnAddStoredRecipe.y = y - sy;
      btnAddStoredRecipe.width = 12;
      btnAddStoredRecipe.height = 14;
      boolean hover = btnAddStoredRecipe.contains(mouseX - sx, mouseY - sy);
      drawTexturedModalRect(sx + 13, y, 182, hover ? 241 : 225, 15, 14);
    } else {
      btnAddStoredRecipe.width = 0;
      btnAddStoredRecipe.height = 0;
    }

    SmartTank fuelTank = te.fuelTank;
    if (!Config.inventoryPanelFree) {
      drawTexturedModalRect(sx + 35, sy + 132, 232, 163, 18, 49);
      if (fuelTank.getFluidAmount() > 0) {
        RenderUtil.renderGuiTank(fuelTank.getFluid(), fuelTank.getCapacity(), fuelTank.getFluidAmount(), sx + 24 + 12, sy + 133, zLevel, 16, 47);
      }
    }

    final EnderWidget returnButton = te.isExtractionDisabled()
        ? btnReturnArea.contains(mouseX - sx, mouseY - sy) ? EnderWidget.STOP_BUT_HOVER : EnderWidget.STOP_BUT
        : btnReturnArea.contains(mouseX - sx, mouseY - sy) ? EnderWidget.RETURN_BUT_HOVER : EnderWidget.RETURN_BUT;
    GlStateManager.color(1, 1, 1, 1);
    EnderWidget.RETURN_BUT.getMap().render(returnButton, sx + 24 + 7, sy + 72, true);

    int headerColor = 0x404040;
    int focusedColor = 0x648494;
    FontRenderer fr = getFontRenderer();
    fr.drawString(headerCrafting, sx + 24 + 7, sy + 6, headerColor);
    fr.drawString(te.isExtractionDisabled() ? headerStorage : headerReturn, sx + 24 + 7 + 10, sy + 72,
        btnReturnArea.contains(mouseX - sx, mouseY - sy) ? focusedColor : headerColor);
    fr.drawString(headerInventory, sx + 24 + 38, sy + 120, headerColor);

    super.drawGuiContainerBackgroundLayer(par1, mouseX, mouseY);

    if (JeiAccessor.isJeiRuntimeAvailable() && btnSync.isSelected()) {
      updateFromJEI();
    }

    view.setDatabase(getDatabase());
    view.setItemFilter(te.getItemFilter());
    view.updateFilter(tfFilter.getText());

    boolean update = view.sortItems();
    scrollbar.setScrollMax(Math.max(0, (view.getNumEntries() + GHOST_COLUMNS - 1) / GHOST_COLUMNS - GHOST_ROWS));
    if (update || scrollPos != scrollbar.getScrollPos()) {
      updateGhostSlots();
    }

    if (te.isActive()) {
      tfFilter.setEnabled(true);
      if (!tfFilter.isFocused() && tfFilter.getText().isEmpty()) {
        fr.drawString(infoTextFilter, tfFilter.x, tfFilter.y, 0x707070);
      }
    } else {
      tfFilter.setEnabled(false);
      setText(tfFilter, "");
      fr.drawString(infoTextOffline, tfFilter.x, tfFilter.y, 0x707070);
    }
  }

  @Override
  protected void onTextFieldChanged(TextFieldEnder tf, String old) {
    if (tf == tfFilter && btnSync.isSelected() && tfFilter.isFocused()) {
      if (Loader.isModLoaded("NotEnoughItems")) {
        updateNEI(tfFilter.getText());
      } else if (JeiAccessor.isJeiRuntimeAvailable()) {
        updateToJEI(tfFilter.getText());
      }
    }
  }

  private void updateNEI(String text) {
    // LayoutManager.searchField.setText(text);
  }

  private void updateToJEI(String text) {
    if (text != null && !text.isEmpty()) {
      JeiAccessor.setFilterText(text);
    } else {
      JeiAccessor.setFilterText("");
    }
  }

  /*
   * A note on the JEI sync:
   * 
   * When the GUI is opened, any text stored in the invPanel will take precedence. If there's no stored text, the text from the JEI field will be used.
   * 
   * When text is entered into the search field, it is synced to JEI. When the field is cleared, that is synced, too.
   * 
   * When text is entered into the JEI field, it is synced to the search field. But when it is cleared, that is not synced. This in on purpose, to give the user
   * a way to quickly look up (or cheat in) something without having to disable syncing.
   * 
   * When the GUI is closed, text will be remembered if it was entered into the search field. If it was entered into the JEI field, it is not remembered. This
   * is important because it allows "locking" an invPanel to a search text and still have JEI sync.
   */

  private void updateFromJEI() {
    final String filterText = JeiAccessor.getFilterText();
    if (!filterText.isEmpty() && !filterText.equals(tfFilter.getText())) {
      tfFilter.setText(filterText);
      tfFilterExternalValue = filterText;
    }
  }

  @Override
  public void drawFakeItemStack(int x, int y, ItemStack stack) {
    FontRenderer font = stack.getItem().getFontRenderer(stack);
    if (font == null) {
      font = fontRenderer;
    }

    boolean smallText = Config.inventoryPanelScaleText;
    String str = null;
    if (stack.getCount() >= 1000) {
      String unit = "k";
      int units = 1000;
      int value = stack.getCount() / units;

      if (smallText) {
        if (value >= units) {
          units *= 1000;
          value /= 1000;
          unit = "m";
        }
        double val = (stack.getCount() % units) / (double) units;
        int bit = (int) Math.floor(val * 10);
        if (bit > 0) {
          unit = "." + bit + unit;
        }
      }

      str = value + unit;
    } else if (stack.getCount() > 1) {
      str = Integer.toString(stack.getCount());
    }
    itemRender.renderItemAndEffectIntoGUI(stack, x, y);
    if (str != null) {
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glPushMatrix();
      GL11.glTranslatef(x + 16, y + 16, 0);

      if (smallText) {
        float scale = 0.666666f;
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        font.drawStringWithShadow(str, -font.getStringWidth(str) - 1, -8, 0xFFFFFF);
        GL11.glPopMatrix();
      } else {
        font.drawStringWithShadow(str, -font.getStringWidth(str), -8, 0xFFFFFF);
      }

      GL11.glPopMatrix();
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    itemRender.renderItemOverlayIntoGUI(font, stack, x, y, "");
  }

  // TODO ghost slot stuff
  // @Override
  // protected void drawGhostSlotTooltip(GhostSlot slot, int mouseX, int mouseY) {
  // ItemStack stack = slot.getStack();
  // if(stack != null) {
  // ghostSlotTooltipStacksize = stack.getCount();
  // try {
  // renderToolTip(stack, mouseX, mouseY);
  // } finally {
  // ghostSlotTooltipStacksize = 0;
  // }
  // }
  // }

  @Override
  public void drawHoveringText(List<String> list, int mouseX, int mouseY, FontRenderer font) {
    if (ghostSlotTooltipStacksize >= 1000) {
      list.add(TextFormatting.WHITE + EnderIO.lang.localize("gui.inventorypanel.tooltip.itemsstored", Integer.toString(ghostSlotTooltipStacksize)));
    }
    super.drawHoveringText(list, mouseX, mouseY, font);
  }

  public InventoryPanelContainer getContainer() {
    return (InventoryPanelContainer) inventorySlots;
  }

  public @Nullable InventoryDatabaseClient getDatabase() {
    return getTileEntity().getDatabaseClient();
  }

  private IconEIO getSortOrderIcon() {
    SortOrder order = view.getSortOrder();
    boolean invert = view.isSortOrderInverted();
    switch (order) {
    case NAME:
      return invert ? IconEIO.SORT_NAME_UP : IconEIO.SORT_NAME_DOWN;
    case COUNT:
      return invert ? IconEIO.SORT_SIZE_UP : IconEIO.SORT_SIZE_DOWN;
    case MOD:
      return invert ? IconEIO.SORT_MOD_UP : IconEIO.SORT_MOD_DOWN;
    default:
      return null;
    }
  }

  void toggleSortOrder(boolean next) {
    SortOrder order = view.getSortOrder();
    SortOrder[] values = SortOrder.values();
    int idx = order.ordinal();
    if (next && view.isSortOrderInverted()) {
      order = values[(idx + 1) % values.length];
    } else if (!next && !view.isSortOrderInverted()) {
      if (idx == 0) {
        idx = values.length;
      }
      order = values[idx - 1];
    }
    view.setSortOrder(order, !view.isSortOrderInverted());
    updateSortButton();
  }

  private void updateSortButton() {
    SortOrder order = view.getSortOrder();
    ArrayList<String> list = new ArrayList<String>();
    SpecialTooltipHandler.addTooltipFromResources(list,
        "enderio.gui.inventorypanel.tooltip.sort." + order.name().toLowerCase(Locale.US) + (view.isSortOrderInverted() ? "_up" : "_down") + ".line");
    btnSort.setIcon(getSortOrderIcon());
    btnSort.setToolTip(list.toArray(new String[list.size()]));
  }

  private void updateGhostSlots() {
    scrollPos = scrollbar.getScrollPos();

    int index = scrollPos * GHOST_COLUMNS;
    int count = view.getNumEntries();
    for (int i = 0; i < GHOST_ROWS * GHOST_COLUMNS; i++, index++) {
      InvSlot slot = (InvSlot) getGhostSlots().get(i);
      if (index < count) {
        slot.entry = view.getItemEntry(index);
      } else {
        slot.entry = null;
      }
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public int getXSize() {
    return 280;
  }

  @Override
  public int getYSize() {
    return 212;
  }

  @Override
  protected void mouseClicked(int x, int y, int button) throws IOException {
    super.mouseClicked(x, y, button);

    x -= guiLeft;
    y -= guiTop;

    if (btnRefill.contains(x, y)) {
      if (getContainer().hasCraftingRecipe()) {
        playClickSound();
        setCraftingHelper(CraftingHelper.createFromSlots(getContainer().getCraftingGridSlots()));
        craftingHelper.refill(this, isShiftKeyDown() ? 64 : 1);
      }
    }

    if (btnAddStoredRecipe.contains(x, y)) {
      TileInventoryPanel te = getTileEntity();
      if (te.getStoredCraftingRecipes() < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES && getContainer().hasNewCraftingRecipe()) {
        StoredCraftingRecipe recipe = new StoredCraftingRecipe();
        if (recipe.loadFromCraftingGrid(getContainer().getCraftingGridSlots())) {
          playClickSound();
          te.addStoredCraftingRecipe(recipe);
        }
      }
    }

    if (btnReturnArea.contains(x, y)) {
      TileInventoryPanel te = getTileEntity();
      playClickSound();
      PacketHandler.INSTANCE.sendToServer(new PacketSetExtractionDisabled(getContainer().windowId, !te.isExtractionDisabled()));
      te.setExtractionDisabled(!te.isExtractionDisabled());
    }
  }

  private void playClickSound() {
    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
  }

  @Override
  protected void mouseWheel(int x, int y, int delta) {
    super.mouseWheel(x, y, delta);

    if (draggingScrollbar == null) {
      x -= guiLeft;
      y -= guiTop;

      boolean shift = isShiftKeyDown();

      if (inventoryArea.contains(x, y)) {
        if (!shift) {
          scrollbar.scrollBy(-Integer.signum(delta));
        } else if (hoverGhostSlot instanceof InvSlot) {
          InvSlot invSlot = (InvSlot) hoverGhostSlot;
          InventoryDatabaseClient db = getDatabase();
          if (invSlot.getStack() != null && invSlot.entry != null && db != null) {
            ItemStack itemStack = mc.player.inventory.getItemStack();
            if (itemStack.isEmpty() || ItemUtil.areStackMergable(itemStack, invSlot.getStack())) {
              PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(db.getGeneration(), invSlot.entry, -1, 1));
            }
          }
        }
      }
    }
  }

  @Override
  protected void ghostSlotClicked(GhostSlot slot, int x, int y, int button) {
    if (slot instanceof InvSlot) {
      InvSlot invSlot = (InvSlot) slot;
      InventoryDatabaseClient db = getDatabase();
      if (invSlot.entry != null && invSlot.getStack() != null && db != null) {
        int targetSlot;
        int count = Math.min(invSlot.getStack().getCount(), invSlot.getStack().getMaxStackSize());

        if (button == 0) {
          if (isShiftKeyDown()) {
            InventoryPlayer playerInv = mc.player.inventory;
            targetSlot = playerInv.getFirstEmptyStack();
            if (targetSlot >= 0) {
              targetSlot = getContainer().getSlotIndex(playerInv, targetSlot);
            }
            if (targetSlot < 0) {
              return;
            }
          } else {
            targetSlot = -1;
          }
        } else if (button == 1) {
          targetSlot = -1;
          if (isCtrlKeyDown()) {
            count = 1;
          } else {
            count = (count + 1) / 2;
          }
        } else {
          return;
        }

        PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(db.getGeneration(), invSlot.entry, targetSlot, count));
      }
    } else if (slot instanceof RecipeSlot) {
      RecipeSlot recipeSlot = (RecipeSlot) slot;
      if (recipeSlot.isVisible()) {
        if (button == 0) {
          fillFromStoredRecipe(recipeSlot.index, isShiftKeyDown());
        } else if (button == 1) {
          getTileEntity().removeStoredCraftingRecipe(recipeSlot.index);
        }
      }
    }
  }

  class InvSlot extends GhostSlot {
    ItemEntry entry;

    InvSlot(int x, int y) {
      this.setX(x);
      this.setY(y);
      this.setGrayOut(false);
      this.setStackSizeLimit(Integer.MAX_VALUE);
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return entry == null ? Prep.getEmpty() : entry.makeItemStack();
    }
  }

  class RecipeSlot extends GhostSlot {
    final int index;

    RecipeSlot(int index, int x, int y) {
      this.index = index;
      this.setX(x);
      this.setY(y);
    }

    @Override
    public @Nonnull ItemStack getStack() {
      TileInventoryPanel te1 = getTileEntity();
      StoredCraftingRecipe recipe = te1.getStoredCraftingRecipe(index);
      return recipe != null ? recipe.getResult(te1) : null;
    }

    @Override
    public boolean isVisible() {
      return index < getTileEntity().getStoredCraftingRecipes();
    }
  }
}
