package crazypants.enderio.gui;

import java.awt.Rectangle;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.IGuiScreen;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;

public class RedstoneModeButton extends IconButtonEIO {

  private static IconEIO[] ICONS = new IconEIO[] { IconEIO.REDSTONE_MODE_ALWAYS, IconEIO.REDSTONE_MODE_WITH_SIGNAL, IconEIO.REDSTONE_MODE_WITHOUT_SIGNAL,
      IconEIO.REDSTONE_MODE_NEVER };

  private IRedstoneModeControlable model;
  private RedstoneControlMode curMode;
  
  private boolean rightMouseDown = false;

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

  public String getTooltipKey() {
    return tooltipKey;
  }

  public void setTooltipKey(String tooltipKey) {
    this.tooltipKey = tooltipKey;
    setToolTip(Lang.localize(tooltipKey, false), model.getRedstoneControlMode().getTooltip());
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
    setToolTip(Lang.localize(tooltipKey, false), mode.getTooltip());
    setIcon(ICONS[mode.ordinal()]);
    model.setRedstoneControlMode(mode);
    if(bc != null) {
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneMode(model, bc.x, bc.y, bc.z));
    }
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    super.drawButton(mc, mouseX, mouseY);
    
    Rectangle r = new Rectangle(xPosition, yPosition, width, height);
    if(r.contains(mouseX, mouseY)) {
      if(rightMouseDown && Mouse.getEventButton() == 1 && !Mouse.getEventButtonState()) {
        prevMode();
      }
      rightMouseDown = Mouse.getEventButton() == 1 && Mouse.getEventButtonState();
    } else {
      rightMouseDown = false;
    }
  }

}
