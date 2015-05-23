package crazypants.enderio.machine.invpanel;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.MultiIconButtonEIO;
import crazypants.enderio.gui.TextFieldEIO;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.gui.VScrollbarEIO;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.machine.invpanel.client.CraftingHelper;
import crazypants.enderio.machine.invpanel.client.DatabaseView;
import crazypants.enderio.machine.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.machine.invpanel.client.ItemEntry;
import crazypants.enderio.machine.invpanel.client.SortOrder;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.SmartTank;
import crazypants.gui.GhostSlot;
import crazypants.gui.GuiToolTip;
import crazypants.render.RenderUtil;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;

@SideOnly(Side.CLIENT)
public class GuiInventoryPanel extends GuiMachineBase<TileInventoryPanel> {

  private static final Rectangle inventoryArea = new Rectangle(24+107, 27, 108, 90);

  private static final Rectangle btnRefill = new Rectangle(24+85, 32, 20, 20);

  private static final Rectangle btnReturnArea = new Rectangle(24+6, 72, 5 * 18, 8);

  private static final int ID_SORT = 9876;
  private static final int ID_CLEAR = 9877;

  private static final int GHOST_COLUMNS = 6;
  private static final int GHOST_ROWS = 5;

  private final DatabaseView view;
  private final TextFieldEIO tfFilter;
  private final IconButtonEIO btnSort;
  private final GuiToolTip ttRefill;
  private final GuiToolTip ttSetReceipe;
  private final VScrollbarEIO scrollbar;
  private final MultiIconButtonEIO btnClear;

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

  public GuiInventoryPanel(TileInventoryPanel te, Container container) {
    super(te, container);
    redstoneButton.visible = false;
    configB.visible = false;

    for (int y = 0; y < GHOST_ROWS; y++) {
      for (int x = 0; x < GHOST_COLUMNS; x++) {
        ghostSlots.add(new InvSlot(24 + 108 + x * 18, 28 + y * 18));
      }
    }

    for (int i = 0; i < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES; i++) {
      ghostSlots.add(new RecipeSlot(i, 7, 7 + i * 20));
    }

    this.view = new DatabaseView();

    int sortMode = te.getGuiSortMode();
    int sortOrderIdx = sortMode >> 1;
    SortOrder[] orders = SortOrder.values();
    if(sortOrderIdx >= 0 && te.getGuiSortMode() < orders.length) {
      view.setSortOrder(orders[sortOrderIdx], (sortMode & 1) != 0);
    }

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    tfFilter = new TextFieldEIO(fr, 24+108, 11, 106, 10);
    tfFilter.setEnableBackgroundDrawing(false);
    tfFilter.setText(te.getGuiFilterString());
    btnSort = new IconButtonEIO(this, ID_SORT, 24+233, 27, getSortOrderIcon()) {
      @Override
      public boolean mousePressed(Minecraft mc, int x, int y) {
        return mousePressedButton(mc, x, y, 0);
      }

      @Override
      public boolean mousePressedButton(Minecraft mc, int x, int y, int button) {
        if(button <= 1 && super.checkMousePress(mc, x, y)) {
          toggleSortOrder(button == 0);
          return true;
        }
        return false;
      }
    };
    scrollbar = new VScrollbarEIO(this, 24+215, 27, 90);
    btnClear = new MultiIconButtonEIO(this, ID_CLEAR, 24+65, 60, IconEIO.X_BUT, IconEIO.X_BUT_PRESSED, IconEIO.X_BUT_HOVER);

    textFields.add(tfFilter);

    headerCrafting = Lang.localize("gui.inventorypanel.header.crafting");
    headerReturn = Lang.localize("gui.inventorypanel.header.return");
    headerStorage = Lang.localize("gui.inventorypanel.header.storage");
    headerInventory = Lang.localize("container.inventory", false);
    infoTextFilter = Lang.localize("gui.inventorypanel.info.filter");
    infoTextOffline = Lang.localize("gui.inventorypanel.info.offline");

    ArrayList<String> list = new ArrayList<String>();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.return.line");
    addToolTip(new GuiToolTip(btnReturnArea, list) {
      @Override
      public boolean shouldDraw() {
        return super.shouldDraw() && !getTileEntity().isExtractionDisabled();
      }
    });

    list.clear();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.storage.line");
    addToolTip(new GuiToolTip(btnReturnArea, list) {
      @Override
      public boolean shouldDraw() {
        return super.shouldDraw() && getTileEntity().isExtractionDisabled();
      }
    });

    list.clear();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.filterslot.line");
    addToolTip(new GuiToolTip(new Rectangle(InventoryPanelContainer.FILTER_SLOT_X, InventoryPanelContainer.FILTER_SLOT_Y, 16, 16), list) {
      @Override
      public boolean shouldDraw() {
        return !getContainer().getSlotFilter().getHasStack() && super.shouldDraw();
      }
    });

    list.clear();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.refill.line");
    ttRefill = new GuiToolTip(btnRefill, list);
    ttRefill.setVisible(false);
    addToolTip(ttRefill);

    list.clear();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.setrecipe.line");
    ttSetReceipe = new GuiToolTip(btnRefill, list) {
      @Override
      public boolean shouldDraw() {
        return super.shouldDraw() && getContainer().hasCraftingRecipe();
      }
    };
    addToolTip(ttSetReceipe);

    list.clear();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.clear.line");
    btnClear.setToolTip(list.toArray(new String[list.size()]));

    addToolTip(new GuiToolTip(new Rectangle(24+12, 132, 15, 47), "") {
      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.localize("gui.inventorypanel.tooltip.fuelTank"));
        text.add(Fluids.toCapactityString(getTileEntity().fuelTank));
      }
    });

    list.clear();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.addrecipe.line");
    addToolTip(new GuiToolTip(btnAddStoredRecipe, list));
  }

  @Override
  public void onGuiClosed() {
    int sortMode = (view.getSortOrder().ordinal() << 1);
    if(view.isSortOrderInverted()) {
      sortMode |= 1;
    }
    getTileEntity().setGuiParameter(sortMode, tfFilter.getText());
    super.onGuiClosed();
  }

  public void setCraftingHelper(CraftingHelper craftingHelper) {
    if(this.craftingHelper != null) {
      this.craftingHelper.remove();
    }
    this.craftingHelper = craftingHelper;
    ttRefill.setVisible(craftingHelper != null);
    ttSetReceipe.setVisible(craftingHelper == null);
    if(craftingHelper != null) {
      craftingHelper.install();
    }
  }

  public void fillFromStoredRecipe(int index, boolean shift) {
    StoredCraftingRecipe recipe = getTileEntity().getStoredCraftingRecipe(index);
    if(recipe == null) {
      return;
    }
    if(getContainer().clearCraftingGrid()) {
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
    addScrollbar(scrollbar);
  }

  @Override
  public void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if(b.id == ID_CLEAR) {
      if(getContainer().clearCraftingGrid()) {
        setCraftingHelper(null);
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/inventorypanel.png");
    int sx = guiLeft;
    int sy = guiTop;

    drawTexturedModalRect(sx+24, sy, 0, 0, 256, ySize);

    if(craftingHelper != null) {
      boolean hover = btnRefill.contains(mouseX - sx, mouseY - sy);
      int iconX = hover ? (isShiftKeyDown() ? 48 : 24) : 0;
      drawTexturedModalRect(sx + btnRefill.x - 2, sy + btnRefill.y - 2, iconX, 232, 24, 24);
    } else if(getContainer().hasCraftingRecipe()) {
      boolean hover = btnRefill.contains(mouseX - sx, mouseY - sy);
      int iconX = hover ? 96 : 72;
      drawTexturedModalRect(sx + btnRefill.x - 2, sy + btnRefill.y - 2, iconX, 232, 24, 24);
    }

    TileInventoryPanel te = getTileEntity();

    int y = sy;
    int numStoredRecipes = te.getStoredCraftingRecipes();
    if(numStoredRecipes == 1) {
      drawTexturedModalRect(sx, y, 227, 225, 28, 30); y += 30;
    } else if(numStoredRecipes > 1) {
      drawTexturedModalRect(sx, y, 227, 225, 28, 24); y += 24;
      for(int i = 1; i < numStoredRecipes - 1; i++) {
        drawTexturedModalRect(sx, y, 198, 229, 28, 20); y += 20;
      }
      drawTexturedModalRect(sx, y, 198, 229, 28, 26); y += 26;
    }

    if(numStoredRecipes < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES && getContainer().hasNewCraftingRecipe()) {
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

    int headerColor = 0x404040;
    int focusedColor = 0x648494;
    FontRenderer fr = getFontRenderer();
    fr.drawString(headerCrafting, sx + 24 + 7, sy + 6, headerColor);
    fr.drawString(te.isExtractionDisabled() ? headerStorage : headerReturn, sx + 24 + 7, sy + 72,
            btnReturnArea.contains(mouseX - sx, mouseY - sy) ? focusedColor : headerColor);
    fr.drawString(headerInventory, sx + 24 + 38, sy + 120, headerColor);

    SmartTank fuelTank = te.fuelTank;
    if(fuelTank.getFluidAmount() > 0) {
      RenderUtil.renderGuiTank(fuelTank.getFluid(), fuelTank.getCapacity(), fuelTank.getFluidAmount(), sx + 24 + 12, sy + 132, zLevel, 16, 47);
    }

    super.drawGuiContainerBackgroundLayer(par1, mouseX, mouseY);

    view.setDatabase(getDatabase());
    view.setItemFilter(te.getItemFilter());
    view.updateFilter(tfFilter.getText());

    boolean update = view.sortItems();
    scrollbar.setScrollMax(Math.max(0, (view.getNumEntries() + GHOST_COLUMNS - 1) / GHOST_COLUMNS - GHOST_ROWS));
    if(update || scrollPos != scrollbar.getScrollPos()) {
      updateGhostSlots();
    }

    if(te.isActive()) {
      tfFilter.setEnabled(true);
      if(!tfFilter.isFocused() && tfFilter.getText().isEmpty()) {
        fr.drawString(infoTextFilter, tfFilter.xPosition, tfFilter.yPosition, 0x707070);
      }
    } else {
      tfFilter.setEnabled(false);
      tfFilter.setText("");
      fr.drawString(infoTextOffline, tfFilter.xPosition, tfFilter.yPosition, 0x707070);
    }
  }

  @Override
  protected void drawFakeItemStack(int x, int y, ItemStack stack) {
    FontRenderer font = stack.getItem().getFontRenderer(stack);
    if(font == null) {
      font = fontRendererObj;
    }
    String str = null;
    if(stack.stackSize >= 1000) {
      int value = stack.stackSize / 1000;
      String unit = "k";
      if(value >= 1000) {
        value /= 1000;
        unit = "m";
      }
      str = value + unit;
    } else if(stack.stackSize > 1) {
      str = Integer.toString(stack.stackSize);
    }
    itemRender.renderItemAndEffectIntoGUI(font, mc.renderEngine, stack, x, y);
    if(str != null) {
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glPushMatrix();
      GL11.glTranslatef(x + 16, y + 16, 0);
      GL11.glScalef(0.8f, 0.8f, 1.0f);
      font.drawStringWithShadow(str, 1 - font.getStringWidth(str), -8, 0xFFFFFF);
      GL11.glPopMatrix();
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    itemRender.renderItemOverlayIntoGUI(font, mc.renderEngine, stack, x, y, "");
  }

  @Override
  protected void drawGhostSlotTooltip(GhostSlot slot, int mouseX, int mouseY) {
    ItemStack stack = slot.getStack();
    if(stack != null) {
      ghostSlotTooltipStacksize = stack.stackSize;
      try {
        renderToolTip(stack, mouseX, mouseY);
      } finally {
        ghostSlotTooltipStacksize = 0;
      }
    }
  }

  @Override
  public void drawHoveringText(List list, int mouseX, int mouseY, FontRenderer font) {
    if(ghostSlotTooltipStacksize >= 1000) {
      list.add(EnumChatFormatting.WHITE + Lang.localize("gui.inventorypanel.tooltip.itemsstored", Integer.toString(ghostSlotTooltipStacksize)));
    }
    super.drawHoveringText(list, mouseX, mouseY, font);
  }

  public InventoryPanelContainer getContainer() {
    return (InventoryPanelContainer) inventorySlots;
  }

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
    if(next && view.isSortOrderInverted()) {
      order = values[(idx + 1) % values.length];
    } else if(!next && !view.isSortOrderInverted()) {
      if(idx == 0) {
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
    TooltipAddera.addTooltipFromResources(list,
        "enderio.gui.inventorypanel.tooltip.sort." + order.name().toLowerCase(Locale.ENGLISH) + (view.isSortOrderInverted() ? "_up" : "_down") + ".line");
    btnSort.setIcon(getSortOrderIcon());
    btnSort.setToolTip(list.toArray(new String[list.size()]));
  }

  private void updateGhostSlots() {
    scrollPos = scrollbar.getScrollPos();

    int index = scrollPos * GHOST_COLUMNS;
    int count = view.getNumEntries();
    for (int i = 0; i < GHOST_ROWS * GHOST_COLUMNS; i++, index++) {
      InvSlot slot = (InvSlot) ghostSlots.get(i);
      if(index < count) {
        slot.entry = view.getItemEntry(index);
        slot.stack = slot.entry.makeItemStack();
      } else {
        slot.entry = null;
        slot.stack = null;
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
  protected void mouseClicked(int x, int y, int button) {
    super.mouseClicked(x, y, button);

    x -= guiLeft;
    y -= guiTop;

    if(btnRefill.contains(x, y)) {
      if(craftingHelper != null) {
        playClickSound();
        craftingHelper.refill(this, isShiftKeyDown() ? 64 : 1);
      } else if(getContainer().hasCraftingRecipe()) {
        playClickSound();
        setCraftingHelper(CraftingHelper.createFromSlots(getContainer().getCraftingGridSlots()));
      }
    }

    if(btnAddStoredRecipe.contains(x, y)) {
      TileInventoryPanel te = getTileEntity();
      if(te.getStoredCraftingRecipes() < TileInventoryPanel.MAX_STORED_CRAFTING_RECIPES && getContainer().hasNewCraftingRecipe()) {
        StoredCraftingRecipe recipe = new StoredCraftingRecipe();
        if(recipe.loadFromCraftingGrid(getContainer().getCraftingGridSlots())) {
          playClickSound();
          te.addStoredCraftingRecipe(recipe);
        }
      }
    }

    if(btnReturnArea.contains(x, y)) {
      TileInventoryPanel te = getTileEntity();
      playClickSound();
      te.setExtractionDisabled(!te.isExtractionDisabled());
    }
  }

  private void playClickSound() {
    mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
  }

  @Override
  protected void mouseWheel(int x, int y, int delta) {
    super.mouseWheel(x, y, delta);

    if(draggingScrollbar == null) {
      x -= guiLeft;
      y -= guiTop;

      boolean shift = isShiftKeyDown();

      if(inventoryArea.contains(x, y)) {
        if(!shift) {
          scrollbar.scrollBy(-Integer.signum(delta));
        } else if(hoverGhostSlot instanceof InvSlot) {
          InvSlot invSlot = (InvSlot) hoverGhostSlot;
          InventoryDatabaseClient db = getDatabase();
          if(invSlot.stack != null && invSlot.entry != null && db != null) {
            ItemStack itemStack = mc.thePlayer.inventory.getItemStack();
            if(itemStack == null || ItemUtil.areStackMergable(itemStack, hoverGhostSlot.stack)) {
              PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(db.getGeneration(), invSlot.entry, -1, 1));
            }
          }
        }
      }
    }
  }

  @Override
  protected void ghostSlotClicked(GhostSlot slot, int x, int y, int button) {
    if(slot instanceof InvSlot) {
      InvSlot invSlot = (InvSlot) slot;
      InventoryDatabaseClient db = getDatabase();
      if(invSlot.entry != null && invSlot.stack != null && db != null) {
        int targetSlot;
        int count = Math.min(invSlot.stack.stackSize, invSlot.stack.getMaxStackSize());

        if(button == 0) {
          if(isShiftKeyDown()) {
            InventoryPlayer playerInv = mc.thePlayer.inventory;
            targetSlot = playerInv.getFirstEmptyStack();
            if(targetSlot >= 0) {
              targetSlot = getContainer().getSlotIndex(playerInv, targetSlot);
            }
            if(targetSlot < 0) {
              return;
            }
          } else {
            targetSlot = -1;
          }
        } else if(button == 1) {
          targetSlot = -1;
          if(isCtrlKeyDown()) {
            count = 1;
          } else {
            count = (count + 1) / 2;
          }
        } else {
          return;
        }

        PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(db.getGeneration(), invSlot.entry, targetSlot, count));
      }
    } else if(slot instanceof RecipeSlot) {
      RecipeSlot recipeSlot = (RecipeSlot) slot;
      if(recipeSlot.isVisible()) {
        if(button == 0) {
          fillFromStoredRecipe(recipeSlot.index, isShiftKeyDown());
        } else if(button == 1) {
          getTileEntity().removeStoredCraftingRecipe(recipeSlot.index);
        }
      }
    }
  }

  class InvSlot extends GhostSlot {
    ItemEntry entry;

    InvSlot(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  class RecipeSlot extends GhostSlot {
    final int index;

    RecipeSlot(int index, int x, int y) {
      this.index = index;
      this.x = x;
      this.y = y;
    }

    @Override
    public ItemStack getStack() {
      TileInventoryPanel te = getTileEntity();
      StoredCraftingRecipe recipe = te.getStoredCraftingRecipe(index);
      return recipe != null ? recipe.getResult(te) : null;
    }

    @Override
    public boolean isVisible() {
      return index < getTileEntity().getStoredCraftingRecipes();
    }
  }
}
