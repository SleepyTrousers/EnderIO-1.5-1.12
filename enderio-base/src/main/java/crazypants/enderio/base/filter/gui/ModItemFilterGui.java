package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.ModItemFilter;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ModItemFilterGui extends AbstractFilterGui {

  private static final int MOD_NAME_COLOR = ColorUtil.getRGB(Color.white);

  private final @Nonnull ModItemFilter filter;

  private final Rectangle[] inputBounds;

  private final IconButton[] deleteButs;

  private final IconButton whiteListB;

  private final int inputOffsetX;
  private final int tfWidth;

  private final @Nonnull GuiToolTip stackInsertTooltip1;
  private final @Nonnull GuiToolTip stackInsertTooltip2;
  private final @Nonnull GuiToolTip stackInsertTooltip3;

  public ModItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IItemFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn, "mod_item_filter");

    filter = (ModItemFilter) filterIn;
    inputOffsetX = getGuiLeft() + 20;
    tfWidth = 96;

    inputBounds = new Rectangle[] { new Rectangle(inputOffsetX, 46, 16, 16), new Rectangle(inputOffsetX, 68, 16, 16), new Rectangle(inputOffsetX, 90, 16, 16) };

    stackInsertTooltip1 = new GuiToolTip(inputBounds[0], Lang.GUI_MOD_ITEM_FILTER_SLOT.get());
    stackInsertTooltip2 = new GuiToolTip(inputBounds[1], Lang.GUI_MOD_ITEM_FILTER_SLOT.get());
    stackInsertTooltip3 = new GuiToolTip(inputBounds[2], Lang.GUI_MOD_ITEM_FILTER_SLOT.get());

    deleteButs = new IconButton[inputBounds.length];
    for (int i = 0; i < deleteButs.length; i++) {
      Rectangle r = inputBounds[i];
      IconButton but = new IconButton(this, FilterGuiUtil.nextButtonId(), r.x + 19, r.y, IconEIO.MINUS);
      but.setToolTip(Lang.GUI_MOD_ITEM_FILTER_DELETE.get());
      deleteButs[i] = but;
    }

    whiteListB = new IconButton(this, -1, inputOffsetX + 19, 24, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
  }

  public void createFilterSlots() {
    filter.createGhostSlots(getGhostSlotHandler().getGhostSlots(), inputOffsetX, 46, this::sendFilterChange);
  }

  @Override
  public void initGui() {
    createFilterSlots();
    super.initGui();
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    for (IconButton but : deleteButs) {
      but.onGuiInit();
    }

    whiteListB.onGuiInit();
    if (filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_BLACKLIST.get());
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
    }

    addToolTip(stackInsertTooltip1);
    addToolTip(stackInsertTooltip2);
    addToolTip(stackInsertTooltip3);
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    for (int i = 0; i < deleteButs.length; i++) {
      IconButton but = deleteButs[i];
      if (but.id == guiButton.id) {
        setMod(i, ItemStack.EMPTY);
        return;
      }
    }
    if (guiButton == whiteListB) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    }
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    for (int i = 0; i < inputBounds.length; i++) {
      String mod = filter.getModAt(i);
      if (mod != null) {
        Rectangle r = inputBounds[i];
        mod = fr.trimStringToWidth(mod, tfWidth - 6);
        fr.drawStringWithShadow(mod, getGuiLeft() + r.x + 41, getGuiTop() + r.y + 4, MOD_NAME_COLOR);
      }
    }
  }

  private void setMod(int i, @Nonnull ItemStack st) {
    filter.setMod(i, st);
    sendFilterChange();
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return Lang.GUI_MOD_ITEM_FILTER.get();
  }

}
