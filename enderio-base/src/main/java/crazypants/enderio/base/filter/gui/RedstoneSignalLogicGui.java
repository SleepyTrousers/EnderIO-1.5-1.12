package crazypants.enderio.base.filter.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.redstone.LogicOutputSignalFilter;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class RedstoneSignalLogicGui extends AbstractFilterGui {

  private static final int ID_COLOR = FilterGuiUtil.nextButtonId();

  private final @Nonnull LogicOutputSignalFilter filter;

  private int xOffset;
  private int yOffset;

  private final @Nonnull List<ColorButton> colorButtons;

  public RedstoneSignalLogicGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn);

    filter = (LogicOutputSignalFilter) filterIn;

    xOffset = 13;
    yOffset = 34;

    colorButtons = new ArrayList<ColorButton>(filter.getNumColors());

    int x = xOffset;
    int y = yOffset;
    for (int i = 0; i < filter.getNumColors(); i++) {
      ColorButton colorB = new ColorButton(this, ID_COLOR + i, x, y);
      colorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
      DyeColor color = filter.getColor(i);
      colorB.setColorIndex(color.ordinal());
      colorButtons.add(colorB);
      x += 20;
    }
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);
    for (int i = 0; i < filter.getNumColors(); i++) {
      if (button.id == ID_COLOR + i) {
        filter.setColor(i, DyeColor.values()[colorButtons.get(i).getColorIndex()]);
      }
    }
    sendFilterChange();
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    for (int i = 0; i < filter.getNumColors(); i++) {
      colorButtons.get(i).onGuiInit();
    }
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return filter.getHeading();
  }

}
