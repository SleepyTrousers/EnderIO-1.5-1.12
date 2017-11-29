package crazypants.enderio.base.gui;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.CycleButton;

import crazypants.enderio.base.Lang;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.PacketRedstoneMode;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.tileentity.TileEntity;

public class RedstoneModeButton<T extends TileEntity & IRedstoneModeControlable> extends CycleButton<RedstoneControlMode.IconHolder> {

  private @Nonnull IRedstoneModeControlable model;
  private T te;

  public RedstoneModeButton(@Nonnull IGuiScreen gui, int id, int x, int y, @Nonnull IRedstoneModeControlable model) {
    super(gui, id, x, y, RedstoneControlMode.IconHolder.class);
    this.model = model;
    setModeRaw(RedstoneControlMode.IconHolder.getFromMode(model.getRedstoneControlMode()));
  }

  public RedstoneModeButton(@Nonnull IGuiScreen gui, int id, int x, int y, @Nonnull T te) {
    this(gui, id, x, y, (IRedstoneModeControlable) te);
    this.te = te;
  }

  public void setModeRaw(@Nonnull RedstoneControlMode.IconHolder newMode) {
    super.setMode(newMode);
    setToolTip(Lang.GUI_REDSTONE_MODE.get(), getMode().getTooltip()); // forces our behavior
  }

  @Override
  public void setMode(@Nonnull RedstoneControlMode.IconHolder newMode) {
    setModeRaw(newMode);
    model.setRedstoneControlMode(getMode().getMode());
    if (te != null) {
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneMode(te));
    }
  }

}
