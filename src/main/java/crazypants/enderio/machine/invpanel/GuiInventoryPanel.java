package crazypants.enderio.machine.invpanel;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.gui.widget.VScrollbar;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ItemUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.generator.zombie.NutrientTank;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.machine.invpanel.client.DatabaseView;
import crazypants.enderio.machine.invpanel.client.ICraftingHelper;
import crazypants.enderio.machine.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.machine.invpanel.client.ItemEntry;
import crazypants.enderio.machine.invpanel.client.SortOrder;
import crazypants.enderio.network.PacketHandler;

@SideOnly(Side.CLIENT)
public class GuiInventoryPanel extends GuiMachineBase<TileInventoryPanel> {

  private static final Rectangle inventoryArea = new Rectangle(107, 27, 108, 90);

  private static final Rectangle btnRefill = new Rectangle(85, 32, 20, 20);

  private static final int ID_SORT = 9876;
  private static final int ID_CLEAR = 9877;

  private static final int GHOST_COLUMNS = 6;
  private static final int GHOST_ROWS = 5;

  private final DatabaseView view;
  private final TextFieldEnder tfFilter;
  private final IconButton btnSort;
  private final GuiToolTip ttRefill;
  private final VScrollbar scrollbar;
  private final MultiIconButton btnClear;

  private int scrollPos;

  private final String headerCrafting;
  private final String headerReturn;
  private final String headerInventory;
  private final String infoTextFilter;
  private final String infoTextOffline;

  private ICraftingHelper craftingHelper;

  public GuiInventoryPanel(TileInventoryPanel te, Container container) {
    super(te, container);
    redstoneButton.visible = false;
    configB.visible = false;

    for (int y = 0; y < GHOST_ROWS; y++) {
      for (int x = 0; x < GHOST_COLUMNS; x++) {
        ghostSlots.add(new InvSlot(108 + x * 18, 28 + y * 18));
      }
    }

    this.view = new DatabaseView();

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    tfFilter = new TextFieldEnder(fr, 108, 11, 106, 10);
    tfFilter.setEnableBackgroundDrawing(false);
    btnSort = new IconButton(this, ID_SORT, 233, 27, getSortOrderIcon()) {
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
    scrollbar = new VScrollbar(this, 215, 27, 90);
    btnClear = new MultiIconButton(this, ID_CLEAR, 65, 60, EnderWidget.X_BUT, EnderWidget.X_BUT_PRESSED, EnderWidget.X_BUT_HOVER);

    textFields.add(tfFilter);

    headerCrafting = EnderIO.lang.localize("gui.inventorypanel.header.crafting");
    headerReturn = EnderIO.lang.localize("gui.inventorypanel.header.return");
    headerInventory = EnderIO.lang.localizeExact("container.inventory");
    infoTextFilter = EnderIO.lang.localize("gui.inventorypanel.info.filter");
    infoTextOffline = EnderIO.lang.localize("gui.inventorypanel.info.offline");

    ArrayList<String> list = new ArrayList<String>();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.return.line");
    addToolTip(new GuiToolTip(new Rectangle(6, 72, 5 * 18, 8), list));

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
    ttRefill.setVisible(false);
    addToolTip(ttRefill);

    list.clear();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.clear.line");
    btnClear.setToolTip(list.toArray(new String[list.size()]));

    addToolTip(new GuiToolTip(new Rectangle(12, 132, 15, 47), "") {
      @Override
      protected void updateText() {
        text.clear();
        text.add(EnderIO.lang.localize("gui.inventorypanel.tooltip.fuelTank"));
        text.add(Fluids.toCapactityString(getTileEntity().fuelTank));
      }
    });
  }

  public void setCraftingHelper(ICraftingHelper craftingHelper) {
    this.craftingHelper = craftingHelper;
    ttRefill.setVisible(craftingHelper != null);
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
  protected void fixupGuiPosition() {
    guiLeft = (width - 232) / 2; // account for the tabs
  }

  @Override
  public void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if(b.id == ID_CLEAR) {
      if(getContainer().clearCraftingGrid()) {
        if(craftingHelper != null) {
          craftingHelper.remove();
          craftingHelper = null;
        }
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/inventorypanel.png");
    int sx = guiLeft;
    int sy = guiTop;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    if(craftingHelper != null) {
      boolean hover = btnRefill.contains(mouseX - sx, mouseY - sy);
      int iconX = hover ? (isShiftKeyDown() ? 48 : 24) : 0;
      drawTexturedModalRect(sx + btnRefill.x - 2, sy + btnRefill.y - 2, iconX, 232, 24, 24);
    }

    int headerColor = 0x404040;
    FontRenderer fr = getFontRenderer();
    fr.drawString(headerCrafting, sx + 7, sy + 6, headerColor);
    fr.drawString(headerReturn, sx + 7, sy + 72, headerColor);
    fr.drawString(headerInventory, sx + 38, sy + 120, headerColor);

    TileInventoryPanel te = getTileEntity();
    NutrientTank fuelTank = te.fuelTank;
    if(fuelTank.getFluidAmount() > 0) {
      RenderUtil.renderGuiTank(fuelTank.getFluid(), fuelTank.getCapacity(), fuelTank.getFluidAmount(), sx + 12, sy + 132, zLevel, 16, 47);
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
    if(stack.stackSize > 999) {
      str = (stack.stackSize / 1000) + "k";
    }
    itemRender.renderItemAndEffectIntoGUI(font, mc.renderEngine, stack, x, y);
    itemRender.renderItemOverlayIntoGUI(font, mc.renderEngine, stack, x, y, str);
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
    SpecialTooltipHandler.addTooltipFromResources(list,
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
    return 256;
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

    if(craftingHelper != null && btnRefill.contains(x, y)) {
      mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
      craftingHelper.refill(this, isShiftKeyDown() ? 64 : 1);
    }
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
    }
  }

  class InvSlot extends GhostSlot {
    ItemEntry entry;

    InvSlot(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
