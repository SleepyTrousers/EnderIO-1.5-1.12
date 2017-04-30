package crazypants.enderio.gui;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.CycleButton;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.tileentity.TileEntity;

public class RedstoneModeButton<T extends TileEntity & IRedstoneModeControlable> extends CycleButton<RedstoneControlMode.IconHolder> {

  private IRedstoneModeControlable model;
  private T te;

  private String tooltipKey = "enderio.gui.tooltip.redstoneControlMode";

  public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model) {
    super(gui, id, x, y, RedstoneControlMode.IconHolder.class);
    this.model = model;
    setModeRaw(RedstoneControlMode.IconHolder.getFromMode(model.getRedstoneControlMode()));
  }

  public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, T te) {
    this(gui, id, x, y, (IRedstoneModeControlable) te);
    this.te = te;
  }

  public void setModeRaw(RedstoneControlMode.IconHolder newMode) {
    if (model == null) {
      return;
    }
    super.setMode(newMode);
    setTooltipKey(tooltipKey); // forces our behavior
  }

  @Override
  public void setMode(RedstoneControlMode.IconHolder newMode) {
    if (model == null) {
      return;
    }
    setModeRaw(newMode);
    model.setRedstoneControlMode(getMode().getMode());
    if (te != null) {
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneMode(te));
    }
  }

  public void setTooltipKey(String key) {
    tooltipKey = key;
    setToolTip(EnderIO.lang.localizeExact(tooltipKey), getMode().getTooltip());
  }
}

