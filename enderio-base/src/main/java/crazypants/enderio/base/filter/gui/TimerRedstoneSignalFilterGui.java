package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.redstone.TimerInputSignalFilter;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class TimerRedstoneSignalFilterGui extends AbstractFilterGui {

  private static final int ID_TIME_UP = FilterGuiUtil.nextButtonId();
  private static final int ID_TIME_DOWN = FilterGuiUtil.nextButtonId();

  private final @Nonnull MultiIconButton timeUpB;
  private final @Nonnull MultiIconButton timeDownB;

  private int xOffset;
  private int yOffset;

  private final @Nonnull TimerInputSignalFilter filter;

  public TimerRedstoneSignalFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn);

    filter = (TimerInputSignalFilter) filterIn;

    xOffset = 13;
    yOffset = 34;

    int stringWidth = this.getFontRenderer().getStringWidth(Lang.GUI_REDSTONE_FILTER_TIME.get());

    timeUpB = MultiIconButton.createAddButton(this, ID_TIME_UP, xOffset + stringWidth + 24, yOffset - 4);
    timeDownB = MultiIconButton.createMinusButton(this, ID_TIME_DOWN, xOffset + stringWidth + 24, yOffset + 4);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);
    if (button.id == ID_TIME_UP) {
      filter.setTime(filter.getTime() + 1);
    } else if (button.id == ID_TIME_DOWN) {
      filter.setTime(filter.getTime() - 1);
    }
    sendFilterChange();
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    timeUpB.onGuiInit();
    timeDownB.onGuiInit();
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    FontRenderer fr = getFontRenderer();
    int stringWidth = this.getFontRenderer().getStringWidth(Lang.GUI_REDSTONE_FILTER_COUNT.get());
    fr.drawString(Lang.GUI_REDSTONE_FILTER_TIME.get(), getGuiLeft() + xOffset, getGuiTop() + yOffset, ColorUtil.getRGB(Color.DARK_GRAY));
    String count = Integer.toString(filter.getTime());
    fr.drawString(" " + count, getGuiLeft() + xOffset + stringWidth, getGuiTop() + yOffset, ColorUtil.getRGB(Color.DARK_GRAY));
    super.renderCustomOptions(top, par1, par2, par3);
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return Lang.GUI_REDSTONE_FILTER_TIMER.get();
  }

}
