package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.item.EnchantmentFilter;
import crazypants.enderio.base.filter.item.EnchantmentFilter.EnchantmentFilterGhostSlot;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class EnchantmentFilterGui extends AbstractFilterGui {

  private static final int ID_WHITELIST = FilterGuiUtil.nextButtonId();
  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();

  private final IconButton whiteListB;
  private final ToggleButton stickyB;

  private final @Nonnull EnchantmentFilter filter;

  private int xOffset;
  private int yOffset;

  public EnchantmentFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IItemFilter filter) {
    this(playerInv, filterContainer, 13, 34, te, filter);
  }

  public EnchantmentFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, int xOffset, int yOffset, TileEntity te,
      @Nonnull IItemFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn, "soul_filter_normal", "soul_filter_big");
    this.xOffset = xOffset;
    this.yOffset = yOffset;

    filter = (EnchantmentFilter) filterIn;

    int butLeft = xOffset + 98 + 30;
    int x = butLeft;
    int y = yOffset + 1 - 25;

    whiteListB = new IconButton(this, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_ENABLED.get(), Lang.GUI_ITEM_FILTER_STICKY_ENABLED_2.get());
    stickyB.setUnselectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_DISABLED.get());
    stickyB.setPaintSelectedBorder(false);

  }

  public void createFilterSlots() {
    filter.createGhostSlots(getGhostSlotHandler().getGhostSlots(), xOffset + 1, yOffset + 1 - 9, new Runnable() {
      @Override
      public void run() {
        sendFilterChange();
      }
    });
  }

  @Override
  public void initGui() {
    createFilterSlots();
    super.initGui();
  }

  @Override
  public void updateButtons() {
    super.updateButtons();

    stickyB.onGuiInit();
    stickyB.setSelected(filter.isSticky());

    whiteListB.onGuiInit();
    if (filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_BLACKLIST.get());
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
    }

  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_STICKY) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_WHITELIST) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    }
  }

  @Override
  public void bindGuiTexture() {
    super.bindGuiTexture(isBig() ? 1 : 0);
  }

  @Override
  protected @Nonnull ResourceLocation getGuiTexture() {
    return super.getGuiTexture(isBig() ? 1 : 0);
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return (isBig() ? Lang.GUI_ENCH_FILTER_BIG : Lang.GUI_ENCH_FILTER_NORMAL).get();
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    int limit = (isBig() ? 63 : 144) - 2;
    NNList<Enchantment> enchantments = filter.getEnchantments();
    for (GhostSlot slot : getGhostSlotHandler().getGhostSlots()) {
      if (slot instanceof EnchantmentFilterGhostSlot) {
        int slotno = slot.getSlot();
        if (slotno < enchantments.size()) {
          String displayName = EnderIO.lang.localizeExact(enchantments.get(slotno).getName());
          if (fr.getStringWidth(displayName) > limit) {
            while (fr.getStringWidth(displayName + "...") > limit) {
              displayName = displayName.substring(0, displayName.length() - 2);
            }
            displayName = displayName + "...";
          }
          fr.drawString(displayName, getGuiLeft() + slot.getX() + 18 + 1, getGuiTop() + slot.getY() + 4, ColorUtil.getRGB(Color.WHITE));
        }
      }
    }
  }

  private boolean isBig() {
    return filter.getSlotCount() > 5;
  }

}
