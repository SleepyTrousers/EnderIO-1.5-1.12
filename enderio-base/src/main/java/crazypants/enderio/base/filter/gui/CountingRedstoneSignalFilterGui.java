package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.redstone.CountingOutputSignalFilter;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class CountingRedstoneSignalFilterGui extends AbstractFilterGui {

  private static final int ID_COUNT_UP = FilterGuiUtil.nextButtonId();
  private static final int ID_COUNT_DOWN = FilterGuiUtil.nextButtonId();

  private final @Nonnull MultiIconButton countUpB;
  private final @Nonnull MultiIconButton countDownB;

  private int xOffset;
  private int yOffset;

  private final @Nonnull CountingOutputSignalFilter filter;

  public CountingRedstoneSignalFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te,
      @Nonnull IFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn);

    filter = (CountingOutputSignalFilter) filterIn;

    xOffset = 13;
    yOffset = 34;

    int stringWidth = this.getFontRenderer().getStringWidth(Lang.GUI_REDSTONE_FILTER_COUNT.get());

    countUpB = MultiIconButton.createAddButton(this, ID_COUNT_UP, xOffset + stringWidth + 24, yOffset - 4);
    countDownB = MultiIconButton.createMinusButton(this, ID_COUNT_DOWN, xOffset + stringWidth + 24, yOffset + 4);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);
    if (button.id == ID_COUNT_UP) {
      filter.setMaxCount(filter.getMaxCount() + 1);
    } else if (button.id == ID_COUNT_DOWN) {
      filter.setMaxCount(filter.getMaxCount() - 1);
    }
    sendFilterChange();
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    countUpB.onGuiInit();
    countDownB.onGuiInit();
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    FontRenderer fr = getFontRenderer();
    int stringWidth = this.getFontRenderer().getStringWidth(Lang.GUI_REDSTONE_FILTER_COUNT.get());
    fr.drawString(Lang.GUI_REDSTONE_FILTER_COUNT.get(), getGuiLeft() + xOffset, getGuiTop() + yOffset, ColorUtil.getRGB(Color.DARK_GRAY));
    String count = Integer.toString(filter.getMaxCount());
    fr.drawString(" " + count, getGuiLeft() + xOffset + stringWidth, getGuiTop() + yOffset, ColorUtil.getRGB(Color.DARK_GRAY));
    super.renderCustomOptions(top, par1, par2, par3);
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return Lang.GUI_REDSTONE_FILTER_COUNTING.get();
  }

}
