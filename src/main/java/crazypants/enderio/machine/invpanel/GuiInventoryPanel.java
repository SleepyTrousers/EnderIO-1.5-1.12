package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.TextFieldEIO;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GhostSlot;
import crazypants.gui.GuiToolTip;
import crazypants.render.RenderUtil;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;
import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiInventoryPanel extends GuiMachineBase<TileInventoryPanel> {

  private static final Rectangle btnScrollUp   = new Rectangle(215,  27,  11,  8);
  private static final Rectangle btnScrollDown = new Rectangle(215, 109,  11,  8);
  private static final Rectangle thumbArea     = new Rectangle(215,  35,  11, 74);

  private static final Rectangle scrollbarArea   = new Rectangle(215,  27,  11, 90);
  private static final Rectangle inventoryArea   = new Rectangle(107,  27, 108, 90);

  private static final int ID_SORT = 9876;

  private static final int GHOST_COLUMNS = 6;
  private static final int GHOST_ROWS    = 5;

  private final TextFieldEIO tfFilter;
  private final IconButtonEIO btnSort;

  private boolean scrollUpPressed;
  private boolean scrollDownPressed;
  private int scrollPos;
  private int scrollMax;
  private long scrollLastTime;

  private final String headerCrafting;
  private final String headerReturn;
  private final String headerInventory;
  private final String infoTextFilter;

  public GuiInventoryPanel(TileInventoryPanel te, Container container) {
    super(te, container);
    redstoneButton.visible = false;
    configB.visible = false;
    scrollLastTime = Minecraft.getSystemTime();

    for(int y = 0; y < GHOST_ROWS; y++) {
      for(int x = 0; x < GHOST_COLUMNS; x++) {
        ghostSlots.add(new InvSlot(108 + x*18, 28 + y*18));
      }
    }

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    tfFilter = new TextFieldEIO(fr, 108, 11, 106, 10);
    tfFilter.setEnableBackgroundDrawing(false);
    btnSort = new IconButtonEIO(this, ID_SORT, 233, 27, getSortOrderIcon());

    textFields.add(tfFilter);

    headerCrafting = Lang.localize("gui.inventorypanel.header.crafting");
    headerReturn = Lang.localize("gui.inventorypanel.header.return");
    headerInventory = Lang.localize("container.inventory", false);
    infoTextFilter = Lang.localize("gui.inventorypanel.info.filter");

    ArrayList<String> list = new ArrayList<String>();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.return.line");
    addToolTip(new GuiToolTip(new Rectangle(6, 72, 5*18, 8), list));

    list = new ArrayList<String>();
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.inventorypanel.tooltip.filterslot.line");
    addToolTip(new GuiToolTip(new Rectangle(InventoryPanelContainer.FILTER_SLOT_X, InventoryPanelContainer.FILTER_SLOT_Y, 16, 16), list) {
      @Override
      public boolean shouldDraw() {
        return !((InventoryPanelContainer) inventorySlots).getSlotFilter().getHasStack() && super.shouldDraw();
      }
    });
  }

  @Override
  public void initGui() {
    super.initGui();
    btnSort.onGuiInit();
  }

  @Override
  public void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if(b.id == ID_SORT) {
      toggleSortOrder();
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/inventorypanel.png");
    int sx = guiLeft;
    int sy = guiTop;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    long time = Minecraft.getSystemTime();
    if((time - scrollLastTime) >= 100) {
      scrollLastTime = time;
      doScroll();
    }

    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    RenderUtil.bindTexture(IconEIO.TEXTURE);
    IconEIO icon;

    icon = scrollUpPressed ? IconEIO.UP_ARROW_ON : IconEIO.UP_ARROW_OFF;
    icon.renderIcon(sx+btnScrollUp.x, sy+btnScrollUp.y);

    icon = scrollDownPressed ? IconEIO.DOWN_ARROW_ON : IconEIO.DOWN_ARROW_OFF;
    icon.renderIcon(sx+btnScrollDown.x, sy+btnScrollDown.y);

    if(scrollMax > 0) {
      icon = IconEIO.VSCROLL_THUMB;
      icon.renderIcon(sx+thumbArea.x, sy+getThumbPosition());
    }

    tes.draw();

    InventoryDatabaseClient db = getDatabase();
    db.setItemFilter(getTileEntity().getItemFilter());
    db.updateFilter(tfFilter.getText());

    if(db.sortItems()) {
      updateGhostSlots();
    }

    int headerColor = 0x404040;
    FontRenderer fr = getFontRenderer();
    fr.drawString(headerCrafting, sx+7, sy+6, headerColor);
    fr.drawString(headerReturn, sx+7, sy+72, headerColor);
    fr.drawString(headerInventory, sx+38, sy+120, headerColor);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    if(!tfFilter.isFocused() && tfFilter.getText().isEmpty()) {
      fr.drawString(infoTextFilter, tfFilter.xPosition, tfFilter.yPosition, 0x707070);
    }
  }

  private int getThumbPosition() {
    return thumbArea.y + (int)(thumbArea.height - IconEIO.VSCROLL_THUMB.height) * scrollPos / scrollMax;
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

  private InventoryDatabaseClient getDatabase() {
    return (InventoryDatabaseClient) getTileEntity().getDatabaseClient();
  }

  private IconEIO getSortOrderIcon() {
    InventoryDatabaseClient db = getDatabase();
    SortOrder order = db.getSortOrder();
    boolean invert = db.isSortOrderInverted();
    switch (order) {
      case NAME:  return invert ? IconEIO.SORT_NAME_UP : IconEIO.SORT_NAME_DOWN;
      case COUNT: return invert ? IconEIO.SORT_SIZE_UP : IconEIO.SORT_SIZE_DOWN;
      case MOD:   return invert ? IconEIO.SORT_MOD_UP  : IconEIO.SORT_MOD_DOWN;
      default:    return null;
    }
  }

  private void toggleSortOrder() {
    InventoryDatabaseClient db = getDatabase();
    SortOrder order = db.getSortOrder();
    if(db.isSortOrderInverted()) {
      SortOrder[] values = SortOrder.values();
      order = values[(order.ordinal()+1) % values.length];
    }
    db.setSortOrder(order, !db.isSortOrderInverted());
    btnSort.setIcon(getSortOrderIcon());
  }

  private void doScroll() {
    scrollMax = Math.max(0, (getDatabase().getNumEntries()+GHOST_COLUMNS-1) / GHOST_COLUMNS - GHOST_ROWS);
    if(scrollUpPressed && scrollPos > 0) {
      scrollPos--;
    }
    if(scrollDownPressed) {
      scrollPos++;
    }
    updateGhostSlots();
  }

  private void updateGhostSlots() {
    if(scrollPos > scrollMax) {
      scrollPos = scrollMax;
    }
    
    InventoryDatabaseClient database = getDatabase();
    int index = scrollPos * GHOST_COLUMNS;
    int count = database.getNumEntries();
    for(int i = 0; i < GHOST_ROWS*GHOST_COLUMNS; i++,index++) {
      InvSlot slot = (InvSlot) ghostSlots.get(i);
      if(index < count) {
        slot.entry = database.getItemEntry(index);
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

    if(scrollMax > 0 && thumbArea.contains(x, y)) {
      int thumbPos = getThumbPosition();
      scrollUpPressed   = y < thumbPos;
      scrollDownPressed = y >= thumbPos + (int)IconEIO.VSCROLL_THUMB.height;
    } else {
      scrollUpPressed   = btnScrollUp.contains(x, y);
      scrollDownPressed = btnScrollDown.contains(x, y);
    }

    if(scrollUpPressed || scrollDownPressed) {
      scrollLastTime = Minecraft.getSystemTime();
      doScroll();
    }
  }

  @Override
  protected void mouseMovedOrUp(int x, int y, int button) {
    super.mouseMovedOrUp(x, y, button);
    scrollUpPressed   = false;
    scrollDownPressed = false;
  }

  @Override
  public void handleMouseInput() {
    super.handleMouseInput();

    int wheel = Mouse.getEventDWheel();
    if(wheel != 0) {
      int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      boolean shift = isShiftKeyDown();

      x -= guiLeft;
      y -= guiTop;

      if(scrollbarArea.contains(x, y) || (shift && inventoryArea.contains(x, y))) {
        scrollUpPressed   = wheel > 0;
        scrollDownPressed = wheel < 0;
        doScroll();
        scrollUpPressed   = false;
        scrollDownPressed = false;

      } else if(!shift && wheel > 0 & (hoverGhostSlot instanceof InvSlot)) {
        InvSlot invSlot = (InvSlot) hoverGhostSlot;
        if(invSlot.stack != null && invSlot.entry != null) {
          ItemStack itemStack = mc.thePlayer.inventory.getItemStack();
          if(itemStack == null || ItemUtil.areStackMergable(itemStack, hoverGhostSlot.stack)) {
            PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(invSlot.entry, -1, 1));
          }
        }
      }
    }
  }

  @Override
  protected void mouseClickMove(int x, int y, int button, long time) {
    super.mouseClickMove(x, y, button, time);
  }

  @Override
  protected void ghostSlotClicked(GhostSlot slot, int x, int y, int button) {
    if(slot instanceof InvSlot) {
      InvSlot invSlot = (InvSlot) slot;
      if(invSlot.entry != null && invSlot.stack != null) {
        int targetSlot;
        int count = Math.min(invSlot.stack.stackSize, invSlot.stack.getMaxStackSize());

        if(button == 0) {
          if(isShiftKeyDown()) {
            InventoryPlayer playerInv = mc.thePlayer.inventory;
            targetSlot = playerInv.getFirstEmptyStack();
            if(targetSlot >= 0) {
              targetSlot = ((InventoryPanelContainer) inventorySlots).getSlotIndex(playerInv, targetSlot);
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

        PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(invSlot.entry, targetSlot, count));
      }
    }
  }

  static class InvSlot extends GhostSlot {
    InventoryDatabaseClient.ItemEntry entry;

    InvSlot(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
