package crazypants.enderio.gui;

import net.minecraft.client.Minecraft;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;

public class RedstoneModeButton extends IconButton {

  private static IconEIO[] ICONS = new IconEIO[] { IconEIO.REDSTONE_MODE_ALWAYS, IconEIO.REDSTONE_MODE_WITH_SIGNAL, IconEIO.REDSTONE_MODE_WITHOUT_SIGNAL,
      IconEIO.REDSTONE_MODE_NEVER };

  private IRedstoneModeControlable model;
  private RedstoneControlMode curMode;

  private BlockCoord bc;

  private String tooltipKey = "enderio.gui.tooltip.redstoneControlMode";

  public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model) {
    this(gui, id, x, y, model, null);
  }

  public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model, BlockCoord bc) {
    super(gui, id, x, y, ICONS[model.getRedstoneControlMode().ordinal()]);
    this.model = model;
    this.bc = bc;
    curMode = model.getRedstoneControlMode();
    setToolTip(EnderIO.lang.localize("gui.tooltip.redstoneControlMode"), curMode.getTooltip());
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

  @Override
  public boolean mousePressedButton(Minecraft mc, int x, int y, int button) {
    boolean result = button == 1 && super.checkMousePress(mc, x, y);
    if(result) {
      prevMode();
    }
    return result;
  }

  public String getTooltipKey() {
    return tooltipKey;
  }

  public void setTooltipKey(String tooltipKey) {
    this.tooltipKey = tooltipKey;
    setToolTip(EnderIO.lang.localizeExact(tooltipKey), model.getRedstoneControlMode().getTooltip());
  }

  private void nextMode() {
    if(curMode == null) {
      curMode = RedstoneControlMode.ON;
    }
    setMode(curMode.next());
  }
  
  private void prevMode() {
    if(curMode == null) {
      curMode = RedstoneControlMode.ON;
    }
    setMode(curMode.previous());
  }

  public void setMode(RedstoneControlMode mode) {
    if(mode == curMode) {
      return;
    }
    curMode = mode;
    setToolTip(EnderIO.lang.localizeExact(tooltipKey), mode.getTooltip());
    setIcon(ICONS[mode.ordinal()]);
    model.setRedstoneControlMode(mode);
    if(bc != null) {
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneMode(model, bc.x, bc.y, bc.z));
    }
  }
}
