package crazypants.enderio.gui;

import net.minecraft.client.Minecraft;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.gui.IGuiScreen;
import crazypants.util.Lang;

public class RedstoneModeButton extends IconButtonEIO {

  private static IconEIO[] ICONS = new IconEIO[] { IconEIO.REDSTONE_MODE_ALWAYS, IconEIO.REDSTONE_MODE_WITH_SIGNAL, IconEIO.REDSTONE_MODE_WITHOUT_SIGNAL,
      IconEIO.REDSTONE_MODE_NEVER };

  IRedstoneModeControlable model;
  RedstoneControlMode curMode;

  public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model) {
    super(gui, id, x, y, ICONS[model.getRedstoneControlMode().ordinal()]);
    this.model = model;
    curMode = model.getRedstoneControlMode();
    setToolTip(Lang.localize("gui.tooltip.redstoneControlMode"), curMode.getTooltip());
    setIcon(ICONS[curMode.ordinal()]);
  }

  @Override
  public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
    boolean result = super.mousePressed(par1Minecraft, par2, par3);
    if(result) {
      nextMode();
    }
    return result;
  }

  private void nextMode() {
    if(curMode == null) {
      curMode = RedstoneControlMode.ON;
    }
    setMode(curMode.next());
  }

  public void setMode(RedstoneControlMode mode) {
    if(mode == curMode) {
      return;
    }
    curMode = mode;
    setToolTip(Lang.localize("gui.tooltip.redstoneControlMode"), mode.getTooltip());
    setIcon(ICONS[mode.ordinal()]);
    model.setRedstoneControlMode(mode);
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    super.drawButton(mc, mouseX, mouseY);
  }

}
