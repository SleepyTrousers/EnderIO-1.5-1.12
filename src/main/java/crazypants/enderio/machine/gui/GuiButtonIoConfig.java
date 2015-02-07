package crazypants.enderio.machine.gui;

import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.PacketIoMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.IGuiScreen;
import crazypants.util.Lang;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;

public class GuiButtonIoConfig extends ToggleButtonEIO {

  private final IIoConfigurable config;
  private final GuiOverlayIoConfig configOverlay;

  @SuppressWarnings("LeakingThisInConstructor")
  public GuiButtonIoConfig(IGuiScreen gui, int id, int x, int y, IIoConfigurable config, GuiOverlayIoConfig configOverlay) {
    super(gui, id, x, y, IconEIO.IO_CONFIG_UP, IconEIO.IO_CONFIG_DOWN);
    this.config = config;
    this.configOverlay = configOverlay;
    this.configOverlay.setConfigB(this);

    String configTooltip = Lang.localize("gui.machine.ioMode.overlay.tooltip");
    setUnselectedToolTip(configTooltip);

    ArrayList<String> list = new ArrayList<String>();
    list.add(configTooltip);
    TooltipAddera.addTooltipFromResources(list, "enderio.gui.machine.ioMode.overlay.tooltip.visible.line");
    if(list.size() > 1) {
      setSelectedToolTip(list.toArray(new String[list.size()]));
    }
  }

  @Override
  protected boolean toggleSelected() {
    if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
      if(!configOverlay.isVisible()) {
        return false;
      }
      config.clearAllIoModes();
      PacketHandler.INSTANCE.sendToServer(new PacketIoMode(config));
    } else {
      boolean vis = !configOverlay.isVisible();
      configOverlay.setVisible(vis);
    }
    return true;
  }
}
