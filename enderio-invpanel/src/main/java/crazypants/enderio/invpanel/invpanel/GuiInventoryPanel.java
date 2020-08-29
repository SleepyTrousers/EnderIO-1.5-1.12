package crazypants.enderio.invpanel.invpanel;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.GuiContainerBase;
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
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.block.skull.BlockEndermanSkull;
import crazypants.enderio.base.block.skull.SkullType;
import crazypants.enderio.base.gui.BlockSceneRenderer;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RecipeTooltipFontRenderer;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.integration.jei.JeiAccessor;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiMachineBase;
import crazypants.enderio.invpanel.client.CraftingHelper;
import crazypants.enderio.invpanel.client.DatabaseView;
import crazypants.enderio.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.invpanel.client.ItemEntry;
import crazypants.enderio.invpanel.client.SortOrder;
import crazypants.enderio.invpanel.config.InvpanelConfig;
import crazypants.enderio.invpanel.network.PacketFetchItem;
import crazypants.enderio.invpanel.network.PacketGuiSettings;
import crazypants.enderio.invpanel.network.PacketHandler;
import crazypants.enderio.invpanel.network.PacketSetExtractionDisabled;
import crazypants.enderio.invpanel.util.StoredCraftingRecipe;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.gui.RecipeTooltipFontRenderer.RECIPE;
import static crazypants.enderio.base.gui.RecipeTooltipFontRenderer.RECIPE_END;
import static crazypants.enderio.base.gui.RecipeTooltipFontRenderer.RECIPE_ID;

@SideOnly(Side.CLIENT)
public class GuiInventoryPanel extends GuiMachineBase<TileInventoryPanel> {

  private static final @Nonnull Rectangle RECTANGLE_FUEL_TANK = new Rectangle(12, 149, 16, 47);

  private static final @Nonnull Rectangle inventoryArea = new Rectangle(107, 27, 108, 90);

  private static final @Nonnull Rectangle btnRefill = new Rectangle(85, 32, 20, 20);

  private static final @Nonnull Rectangle btnReturnArea = new Rectangle(6, 72, 5 * 18, 8);

  private static final int ID_SORT = 9876;
  private static final int ID_CLEAR = 9877;
  private static final int ID_SYNC = 9878;

  private static final int GHOST_COLUMNS = 6;
  private static final int GHOST_ROWS = 6;

  private final @Nonnull DatabaseView view;

  private final @Nonnull TextFieldEnder tfFilter;
  private String tfFilterExternalValue = null;
  private final @Nonnull IconButton btnSort;
  private final @Nonnull ToggleButton btnSync;
  private final @Nonnull GuiToolTip ttRefill;
  private final @Nonnull VScrollbar scrollbar;
  private final @Nonnull MultiIconButton btnClear;

  private final @Nonnull ItemStackButton[] repButtons = new ItemStackButton[TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES];

  private int scrollPos;
  private int ghostSlotTooltipStacksize;

  private final @Nonnull String headerCrafting;
  private final @Nonnull String headerReturn;
  private final @Nonnull String headerStorage;
  private final @Nonnull String headerInventory;
  private final @Nonnull String infoTextFilter;
  private final @Nonnull String infoTextOffline;
  private final @Nonnull String infoTextNoConnection;

  private CraftingHelper craftingHelper;

  private final @Nonnull RecipeTooltipFontRenderer rtfr;

  private final @Nonnull BlockSceneRenderer bsr_active = new BlockSceneRenderer(new NNList<>( //
      Pair.of(new BlockPos(0, 0, 0), //
          ModObject.blockEndermanSkull.getBlock().getDefaultState().withProperty(BlockEndermanSkull.VARIANT, SkullType.REANIMATED_TORMENTED))));
  private final @Nonnull BlockSceneRenderer bsr_offline = new BlockSceneRenderer(new NNList<>( //
      Pair.of(new BlockPos(0, 0, 0), //
          ModObject.blockEndermanSkull.getBlock().getDefaultState().withProperty(BlockEndermanSkull.VARIANT, SkullType.TORMENTED))));

  private final @Nonnull Rectangle btnAddStoredRecipe = new Rectangle();

  public GuiInventoryPanel(@Nonnull TileInventoryPanel te, @Nonnull Container container) {
    super(te, container, "inv_panel", "inv_panel_extended");
    redstoneButton.visible = false;
    configB.visible = false;

    rtfr = new RecipeTooltipFontRenderer(this);

    this.ghostSlotHandler = new GhostSlotHandler() {
      @Override
      protected void ghostSlotClicked(@Nonnull GuiContainerBase gui, @Nonnull GhostSlot slot, int x, int y, int button) {
        if (slot instanceof InvSlot) {
          GuiInventoryPanel.this.ghostSlotClicked(((InvSlot) slot), x, y, button);
        } else {
          super.ghostSlotClicked(gui, slot, x, y, button);
        }
      }

      // @Override
      protected void drawGhostSlotToolTip_OLD(@Nonnull GuiContainerBase gui, int mouseX, int mouseY) {
        // TODO: 1.12 This needs to go into the ghost slot
        GhostSlot slot = this.getGhostSlotAt(gui, mouseX, mouseY);
        if (slot != null) {
          ItemStack stack = slot.getStack();
          if (!stack.isEmpty()) {
            ghostSlotTooltipStacksize = stack.getCount();
            try {
              renderToolTip(stack, mouseX, mouseY);
            } finally {
              ghostSlotTooltipStacksize = 0;
            }
          }
        }
      }
    };

    for (int y = 0; y < GHOST_ROWS; y++) {
      for (int x = 0; x < GHOST_COLUMNS; x++) {
        getGhostSlotHandler().add(new InvSlot(108 + x * 18, 28 + y * 18));
      }
    }

    for (int i = 0; i < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES; i++) {
      int index = i;
      TileInventoryPanel te1 = getTileEntity();
      StoredCraftingRecipe recipe = te1.getStoredCraftingRecipe(index);
      repButtons[index] = new ItemStackButton(this, 22 + index, -35, 18 + index * 20, IconEIO.QUESTION, null);
      addToolTip(new GuiToolTip(repButtons[index].getBounds(), EnderIO.lang.localize("gui.inventorypanel.tooltip.recipe.store")) {
        @Override
        public boolean shouldDraw() {
          return super.shouldDraw() && !repButtons[index].hasItemStack() && repButtons[index].enabled;
        }
      });
      if (recipe != null) {
        ItemStack icon = recipe.getResult(te1);
        repButtons[index].setStack(icon);

        rtfr.registerRecipe("invprecipe" + index, recipe.getIngredients());
        addToolTip(new GuiToolTip(repButtons[index].getBounds(), "") {
          @Override
          protected void updateText() {
            text.clear();
            text.add(TextFormatting.YELLOW + repButtons[index].itemStackName());
            text.add(RECIPE_ID + "invprecipe" + index + RECIPE_END + TextFormatting.GRAY + EnderIO.lang.localize("gui.inventorypanel.tooltip.recipe.load"));
            text.add(RECIPE + TextFormatting.GRAY + EnderIO.lang.localize("gui.inventorypanel.tooltip.recipe.loadstacks"));
            text.add(RECIPE + TextFormatting.GRAY + EnderIO.lang.localize("gui.inventorypanel.tooltip.recipe.delete"));
            text.add(RECIPE);
            text.add(RECIPE);
            text.add(RECIPE);
          }

          @Override
          public boolean shouldDraw() {
            return super.shouldDraw() && repButtons[index].hasItemStack() && repButtons[index].enabled;
          }
        });
      }
    }

    this.view = new DatabaseView();

    int sortMode = te.getGuiSortMode();
    int sortOrderIdx = sortMode >> 1;
    SortOrder[] orders = SortOrder.values();
    if (sortOrderIdx >= 0 && sortOrderIdx < orders.length) {
      view.setSortOrder(orders[sortOrderIdx], (sortMode & 1) != 0);
    }

    tfFilter = new TextFieldEnder(getFontRenderer(), 108, 11, 106, 10);
    tfFilter.setEnableBackgroundDrawing(false);
    tfFilter.setText(te.getGuiFilterString());

    btnSync = new ToggleButton(this, ID_SYNC, 233, 46, IconEIO.CROSS, IconEIO.TICK);
    btnSync.setSelected(getTileEntity().getGuiSync());
    btnSync.setSelectedToolTip(EnderIO.lang.localize("gui.enabled"));
    btnSync.setUnselectedToolTip(EnderIO.lang.localize("gui.disabled"));

    if (JeiAccessor.isJeiRuntimeAvailable()) {
      btnSync.setToolTip(EnderIO.lang.localize("gui.inventorypanel.tooltip.sync.jei"));
      btnSync.setSelectedToolTip(EnderIO.lang.localize("gui.enabled"), EnderIO.lang.localize("gui.inventorypanel.tooltip.sync.jei.line1"),
          EnderIO.lang.localize("gui.inventorypanel.tooltip.sync.jei.line2"));
      btnSync.setUnselectedToolTip(EnderIO.lang.localize("gui.disabled"));
      if (getTileEntity().getGuiSync()) {
        if (!te.getGuiFilterString().isEmpty()) {
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

    btnSort = new IconButton(this, ID_SORT, 233, 27, getSortOrderIcon()) {
      @Override
      public boolean mousePressed(@Nonnull Minecraft mc1, int xIn, int yIn) {
        return mousePressedButton(mc1, xIn, yIn, 0);
      }

      @Override
      public boolean mousePressedButton(@Nonnull Minecraft mc1, int xIn, int yIn, int button) {
        if (button <= 1 && super.checkMousePress(mc1, xIn, yIn)) {
          toggleSortOrder(button == 0);
          return true;
        }
        return false;
      }
    };

    scrollbar = new VScrollbar(this, 215, 27, 108);
    btnClear = new MultiIconButton(this, ID_CLEAR, 65, 60, EnderWidget.X_BUT, EnderWidget.X_BUT_PRESSED, EnderWidget.X_BUT_HOVER);

    textFields.add(tfFilter);

    headerCrafting = EnderIO.lang.localize("gui.inventorypanel.header.crafting");
    headerReturn = EnderIO.lang.localize("gui.inventorypanel.header.return");
    headerStorage = EnderIO.lang.localize("gui.inventorypanel.header.storage");
    headerInventory = EnderIO.lang.localizeExact("container.inventory");
    infoTextFilter = EnderIO.lang.localize("gui.inventorypanel.info.filter");
    infoTextOffline = EnderIO.lang.localize("gui.inventorypanel.info.offline");
    infoTextNoConnection = EnderIO.lang.localize("gui.inventorypanel.info.noconnection");

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

    if (!InvpanelConfig.inventoryPanelFree.get()) {
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
    GhostSlot slot = getGhostSlotHandler().getGhostSlotAt(this, mouseX + getGuiLeft(), mouseY + getGuiTop());
    if (slot instanceof InvSlot) {
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
    for (ItemStackButton button : repButtons) {
      if (button != null) {
        button.onGuiInit();
      }
    }
    ((InventoryPanelContainer) inventorySlots).createGhostSlots(getGhostSlotHandler());
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b.id == ID_CLEAR) {
      if (getContainer().clearCraftingGrid()) {
        setCraftingHelper(null);
      }
    }

    if (b instanceof ItemStackButton) {
      ItemStackButton recipeClicked = (ItemStackButton) b;
      if (recipeClicked.hasItemStack()) {
        if (isCtrlKeyDown() && recipeClicked.isRightClick()) {
          getTileEntity().removeStoredCraftingRecipe(recipeClicked.id - 22);
          recipeClicked.clearStack();
        } else if (!recipeClicked.isRightClick()) {
          fillFromStoredRecipe(recipeClicked.id - 22, isShiftKeyDown());
        }
      } else if (!isCtrlKeyDown()) {
        TileInventoryPanel te = getTileEntity();
        if (te.getStoredCraftingRecipes() < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES && getContainer().hasNewCraftingRecipe()) {
          StoredCraftingRecipe recipe = new StoredCraftingRecipe();
          if (recipe.loadFromCraftingGrid(getContainer().getCraftingGridSlots())) {
            playClickSound();
            te.addStoredCraftingRecipe(recipe);
            recipeClicked.setStack(recipe.getResult(te));
          }
        }
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    syncSettingsChange();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture(0);
    int sx = guiLeft;
    int sy = guiTop;

    drawTexturedModalRect(sx, sy, 0, 0, 232, 232);
    drawTexturedModalRect(sx + 232, sy, 232, 0, 24, 68);

    if (craftingHelper != null || getContainer().hasCraftingRecipe()) {
      boolean hover = btnRefill.contains(mouseX - sx, mouseY - sy);
      int iconX = hover ? (isShiftKeyDown() ? 48 : 24) : 0;
      bindGuiTexture(1);
      drawTexturedModalRect(sx + btnRefill.x - 2, sy + btnRefill.y - 2, iconX, 232, 24, 24);
      bindGuiTexture(0);
    }

    TileInventoryPanel te = getTileEntity();

    int numStoredRecipes = te.getStoredCraftingRecipes();
    bindGuiTexture(1);
    drawTexturedModalRect(sx - 42, (this.height - 208) / 2, 0, 0, 42, 208);
    bindGuiTexture(0);

    for (int i = 0; i < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES; ++i) {
      if (repButtons[i] != null && !repButtons[i].hasItemStack()) {
        repButtons[i].setEnabled(false);
      }
    }

    if (numStoredRecipes < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES && getContainer().hasNewCraftingRecipe()) {
      if (repButtons[numStoredRecipes] != null) {
        repButtons[numStoredRecipes].setEnabled(true);
      }
    }

    SmartTank fuelTank = te.fuelTank;
    if (!InvpanelConfig.inventoryPanelFree.get()) {
      drawTexturedModalRect(sx + 11, sy + 147, 232, 163, 18, 49);
      if (fuelTank.getFluidAmount() > 0) {
        RenderUtil.renderGuiTank(fuelTank.getFluid(), fuelTank.getCapacity(), fuelTank.getFluidAmount(), sx + 12, sy + 148, zLevel, 16, 47);
      }
    }

    final EnderWidget returnButton = te.isExtractionDisabled()
        ? btnReturnArea.contains(mouseX - sx, mouseY - sy) ? EnderWidget.STOP_BUT_HOVER : EnderWidget.STOP_BUT
        : btnReturnArea.contains(mouseX - sx, mouseY - sy) ? EnderWidget.RETURN_BUT_HOVER : EnderWidget.RETURN_BUT;
    GlStateManager.color(1, 1, 1, 1);
    EnderWidget.RETURN_BUT.getMap().render(returnButton, sx + 7, sy + 72, true);

    int headerColor = 0x404040;
    int focusedColor = 0x648494;
    FontRenderer fr = getFontRenderer();
    fr.drawString(headerCrafting, sx + 7, sy + 6, headerColor);
    fr.drawString(te.isExtractionDisabled() ? headerStorage : headerReturn, sx + 7 + 10, sy + 72,
        btnReturnArea.contains(mouseX - sx, mouseY - sy) ? focusedColor : headerColor);
    fr.drawString(headerInventory, sx + 38, sy + 138, headerColor);

    super.drawGuiContainerBackgroundLayer(par1, mouseX, mouseY);

    if (JeiAccessor.isJeiRuntimeAvailable() && btnSync.isSelected()) {
      updateFromJEI();
    }


    view.setDatabase(getDatabase());

    // TODO: Filter
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
    } else if (!te.isActive() && te.hasConnection()) {
      tfFilter.setEnabled(false);
      setText(tfFilter, "");
      fr.drawString(infoTextOffline, tfFilter.x, tfFilter.y, 0x707070);
    } else {
      tfFilter.setEnabled(false);
      setText(tfFilter, "");
      fr.drawString(infoTextNoConnection, tfFilter.x, tfFilter.y, 0x707070);
    }

    if (!InvpanelConfig.inventoryPanelFree.get()) {
      GlStateManager.pushMatrix();
      BlockEndermanSkull.guiRender = true;
      (te.isActive() && te.hasConnection() ? bsr_active : bsr_offline).drawScreen(sx - 4, sy + 132 + 49, 48, 48);
      BlockEndermanSkull.guiRender = false;
      GlStateManager.popMatrix();
    }

  }

  @Override
  protected void onTextFieldChanged(@Nonnull TextFieldEnder tf, @Nonnull String old) {
    if (tf == tfFilter && btnSync.isSelected() && tfFilter.isFocused()) {
      if (JeiAccessor.isJeiRuntimeAvailable()) {
        updateToJEI(tfFilter.getText());
      }
    }
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
  public void drawFakeItemStack(int x, int y, @Nonnull ItemStack stack) {
    FontRenderer font = stack.getItem().getFontRenderer(stack);
    if (font == null) {
      font = fontRenderer;
    }

    boolean smallText = InvpanelConfig.inventoryPanelScaleText.get();
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
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.disableBlend();
      GlStateManager.pushMatrix();
      GlStateManager.translate(x + 16, y + 16, 0);

      if (smallText) {
        float scale = 0.666666f;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        font.drawStringWithShadow(str, -font.getStringWidth(str) - 1, -8, 0xFFFFFF);
        GlStateManager.popMatrix();
      } else {
        font.drawStringWithShadow(str, -font.getStringWidth(str), -8, 0xFFFFFF);
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
    }
    itemRender.renderItemOverlayIntoGUI(font, stack, x, y, "");
  }

  @Override
  public void drawHoveringText(@Nonnull List<String> list, int mouseX, int mouseY, @Nonnull FontRenderer font) {
    if (ghostSlotTooltipStacksize >= 1000) {
      list.add(TextFormatting.WHITE + EnderIO.lang.localize("gui.inventorypanel.tooltip.itemsstored", Integer.toString(ghostSlotTooltipStacksize)));
    }
    super.drawHoveringText(list, mouseX, mouseY, font);
  }

  @Nonnull
  public InventoryPanelContainer getContainer() {
    return (InventoryPanelContainer) inventorySlots;
  }

  @Nullable
  public InventoryDatabaseClient getDatabase() {
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
      InvSlot slot = (InvSlot) getGhostSlotHandler().getGhostSlots().get(i);
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
    return 230;
  }

  @Override
  public int getYSize() {
    return 232;
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
        GhostSlot hovered = getGhostSlotHandler().getGhostSlotAt(this, x, y);
        if (!shift) {
          scrollbar.scrollBy(-Integer.signum(delta));
        } else if (hovered instanceof InvSlot) {
          InvSlot invSlot = (InvSlot) hovered;
          InventoryDatabaseClient db = getDatabase();
          if (!invSlot.getStack().isEmpty() && invSlot.entry != null && db != null) {
            ItemStack itemStack = mc.player.inventory.getItemStack();
            if (itemStack.isEmpty() || ItemUtil.areStackMergable(itemStack, invSlot.getStack())) {
              PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(db.getGeneration(), invSlot.entry, -1, 1));
            }

          }
        }
      }
    }
  }

  protected void ghostSlotClicked(@Nonnull InvSlot invSlot, int x, int y, int button) {
    InventoryDatabaseClient db = getDatabase();
    if (invSlot.entry != null && !invSlot.getStack().isEmpty() && db != null) {
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
    @Nonnull
    public ItemStack getStack() {
      return entry == null ? Prep.getEmpty() : entry.makeItemStack();
    }
  }

  public class ItemStackButton extends IconButton {
    private ItemStack stack; // TODO: Check if we really need nulls here
    private boolean rightClick = false;
    private final IWidgetIcon backupIcon;

    public ItemStackButton(@Nonnull IGuiScreen gui, int id, int x, int y, @Nullable IWidgetIcon icon, @Nullable ItemStack itm) {
      super(gui, id, x, y, icon);
      this.backupIcon = icon;
      this.stack = itm;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {

      if (hasItemStack()) {
        this.icon = null;
        GlStateManager.enableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(0.7, 0.7, 0.7);
        itemRender.zLevel = this.id + 1;

        itemRender.renderItemAndEffectIntoGUI(this.stack, 3, 3);
        itemRender.renderItemOverlayIntoGUI(fontRenderer, this.stack, 3, 3, "");
        GlStateManager.popMatrix();

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        RenderHelper.enableGUIStandardItemLighting();

      } else {
        this.icon = this.backupIcon;
      }
      super.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    private boolean hasItemStack() {
      return this.stack != null && !this.stack.isEmpty();
    }

    private String itemStackName() {
      return this.hasItemStack() ? this.stack.getDisplayName() : "";
    }

    public boolean isRightClick() {
      return rightClick;
    }

    public void setStack(ItemStack stack) {
      this.stack = stack;
    }

    public void clearStack() {
      this.stack = null;
    }

    @Override
    public boolean mousePressedButton(@Nonnull Minecraft mc, int mouseX, int mouseY, int button) {
      if (button == 1 && super.checkMousePress(mc, mouseX, mouseY)) {
        this.rightClick = true;
        return true;
      } else {
        this.rightClick = false;
        return false;
      }

    }
  }

  @Nonnull
  @Override
  public FontRenderer getFontRenderer() {
    return rtfr;
  }

  @Override
  public List<Rectangle> getBlockingAreas() {
    List<Rectangle> rectangles = super.getBlockingAreas();
    rectangles.add(new Rectangle(guiLeft - 42, (this.height - 208) / 2, 42, 208));
    rectangles.add(new Rectangle(guiLeft + 232, guiTop, 24, 68));
    return rectangles;
  }

  @Override
  public Rectangle getDocumentationButtonArea() {
    return new Rectangle(guiLeft - 42, guiTop + 3, 25, 25);
  }

  @Override
  public Rectangle getDocumentationArea() {
    return super.getDocumentationArea();
  }
}
