package crazypants.enderio.base.machine.gui;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.interfaces.IIoConfigurable;
import crazypants.enderio.base.machine.modes.PacketIoMode;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.tileentity.TileEntity;

public class GuiButtonIoConfig<E extends TileEntity & IIoConfigurable> extends ToggleButton {

  private final @Nonnull E config;
  private final @Nonnull GuiOverlayIoConfig<E> configOverlay;

  public GuiButtonIoConfig(@Nonnull IGuiScreen gui, int id, int x, int y, @Nonnull E config, @Nonnull GuiOverlayIoConfig<E> configOverlay) {
    super(gui, id, x, y, IconEIO.IO_CONFIG_UP, IconEIO.IO_CONFIG_DOWN);
    this.config = config;
    this.configOverlay = configOverlay;
    this.configOverlay.setConfigB(this);

    String configTooltip = EnderIO.lang.localize("gui.machine.ioMode.overlay.tooltip");
    setUnselectedToolTip(configTooltip);

    ArrayList<String> list = new ArrayList<String>();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.machine.ioMode.overlay.tooltip.visible.line");
    if (!list.isEmpty()) {
      setSelectedToolTip(list.toArray(new String[list.size()]));
    }
  }

  @Override
  protected boolean toggleSelected() {
    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
      if (!configOverlay.isVisible()) {
        return false;
      }
      config.clearAllIoModes();
      PacketHandler.INSTANCE.sendToServer(new PacketIoMode(config));
    } else {
      boolean vis = !configOverlay.isVisible();
      configOverlay.setIsVisible(vis);
    }
    return true;
  }

}
