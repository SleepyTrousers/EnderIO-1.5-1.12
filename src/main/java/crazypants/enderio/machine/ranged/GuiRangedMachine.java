package crazypants.enderio.machine.ranged;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiRangedMachine<T extends AbstractPoweredMachineEntity & IRanged> extends GuiPoweredMachineBase<T> {

  private static final int RANGE_ID = 3879456;
  private ToggleButton showRangeB;

  public GuiRangedMachine(T machine, Container container) {
    super(machine, container);

    int x = getXSize() - 5 - BUTTON_SIZE;
    showRangeB = new ToggleButton(this, RANGE_ID, x, 44, IconEIO.PLUS, IconEIO.MINUS);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public List<String> getToolTipText() {
        return Lists.newArrayList(EnderIO.lang.localize(showRangeB.isSelected() ? "gui.spawnGurad.hideRange" : "gui.spawnGurad.showRange"));
      }
    });
  }
  
  @Override
  public void initGui() {
    super.initGui();
    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());
  }
  
  @Override
  protected void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if(b.id == RANGE_ID) {
      getTileEntity().setShowRange(showRangeB.isSelected());
    }
  }

  @Override
  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawForegroundImpl(mouseX, mouseY);
    int range = (int) getTileEntity().getRange();
    String str = EnderIO.lang.localize("gui.spawnGurad.range") + " " + range;
    drawString(fontRendererObj, str, xSize - fontRendererObj.getStringWidth(str) - 8, 68, ColorUtil.getRGB(Color.white));
  }
}
