package crazypants.enderio.machine;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.gui.GuiContainerBase;
import crazypants.gui.GuiToolTip;
import crazypants.util.BlockCoord;

public abstract class GuiMachineBase extends GuiContainerBase {

  protected static final int POWER_Y = 14;
  protected final int POWER_X = 15;
  protected static final int POWER_WIDTH = 10;
  protected static final int POWER_HEIGHT = 42;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  public static final int BUTTON_SIZE = 16;

  private AbstractMachineEntity tileEntity;

  private RedstoneModeButton redstoneButton;

  public GuiMachineBase(AbstractMachineEntity machine, Container container) {
    super(container);
    tileEntity = machine;
    addToolTip(new GuiToolTip(new Rectangle(getPowerX(), getPowerY(), getPowerWidth(), getPowerHeight()), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(PowerDisplayUtil.formatPower(tileEntity.getEnergyStored()) + "/"
            + PowerDisplayUtil.formatPower(tileEntity.getCapacitor().getMaxEnergyStored()) + " " + PowerDisplayUtil.abrevation());
      }

    });
    int x = xSize - 5 - BUTTON_SIZE;
    int y = 5;
    redstoneButton = new RedstoneModeButton(this, -1, x, y, tileEntity, new BlockCoord(tileEntity));
  }

  protected int getPowerX() {
    return POWER_X;
  }

  protected int getPowerY() {
    return POWER_Y;
  }

  protected int getPowerWidth() {
    return POWER_WIDTH;
  }

  protected int getPowerHeight() {
    return POWER_HEIGHT;
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.onGuiInit();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;
    int i1 = tileEntity.getEnergyStoredScaled(getPowerHeight());
    // x, y, u, v, width, height
    drawTexturedModalRect(k + getPowerX(), l + (getPowerY() + getPowerHeight()) - i1, 176, 31, getPowerWidth(), i1);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }

  }

}
