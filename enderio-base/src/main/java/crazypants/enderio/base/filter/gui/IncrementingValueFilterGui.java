package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.redstone.IFilterIncrementingValue;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class IncrementingValueFilterGui extends AbstractFilterGui {

  private static final int ID_VALUE_UP = FilterGuiUtil.nextButtonId();
  private static final int ID_VALUE_DOWN = FilterGuiUtil.nextButtonId();

  private final @Nonnull MultiIconButton valueUpB;
  private final @Nonnull MultiIconButton valueDownB;

  private int xOffset;
  private int yOffset;

  private final @Nonnull IFilterIncrementingValue incrementingFilter;

  public IncrementingValueFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn);

    incrementingFilter = (IFilterIncrementingValue) filterIn;

    xOffset = 13;
    yOffset = 34;

    int stringWidth = this.getFontRenderer().getStringWidth(incrementingFilter.getIncrementingValueName());

    valueUpB = MultiIconButton.createAddButton(this, ID_VALUE_UP, xOffset + stringWidth + 24, yOffset - 4);
    valueDownB = MultiIconButton.createMinusButton(this, ID_VALUE_DOWN, xOffset + stringWidth + 24, yOffset + 4);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);

    int multiplier = 1;
    if (isShiftKeyDown()) {
      multiplier = 10;
    } else if (isCtrlKeyDown()) {
      multiplier = 20;
    }

    if (button.id == ID_VALUE_UP) {
      incrementingFilter.setIncrementingValue(incrementingFilter.getIncrementingValue() + 1 * multiplier);
    } else if (button.id == ID_VALUE_DOWN) {
      incrementingFilter.setIncrementingValue(incrementingFilter.getIncrementingValue() - 1 * multiplier);
    }
    sendFilterChange();
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    valueUpB.onGuiInit();
    valueDownB.onGuiInit();
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    FontRenderer fr = getFontRenderer();
    int stringWidth = getFontRenderer().getStringWidth(incrementingFilter.getIncrementingValueName());
    fr.drawString(incrementingFilter.getIncrementingValueName(), getGuiLeft() + xOffset, getGuiTop() + yOffset, ColorUtil.getRGB(Color.DARK_GRAY));
    String count = Integer.toString(incrementingFilter.getIncrementingValue());
    fr.drawString(" " + count, getGuiLeft() + xOffset + stringWidth, getGuiTop() + yOffset, ColorUtil.getRGB(Color.DARK_GRAY));
    super.renderCustomOptions(top, par1, par2, par3);
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return Lang.GUI_REDSTONE_FILTER_TIMER.get();
  }

}
