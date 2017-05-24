package crazypants.enderio.machine.gui;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.interfaces.IIoConfigurable;
import crazypants.enderio.machine.modes.PacketIoMode;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.tileentity.TileEntity;

public class GuiButtonIoConfig<E extends TileEntity & IIoConfigurable> extends ToggleButton {

  private final E config;
  private final GuiOverlayIoConfig<E> configOverlay;

  public GuiButtonIoConfig(IGuiScreen gui, int id, int x, int y, E config, GuiOverlayIoConfig<E> configOverlay) {
    super(gui, id, x, y, IconEIO.IO_CONFIG_UP, IconEIO.IO_CONFIG_DOWN);
    this.config = config;
    this.configOverlay = configOverlay;
    this.configOverlay.setConfigB(this);

    String configTooltip = EnderIO.lang.localize("gui.machine.ioMode.overlay.tooltip");
    setUnselectedToolTip(configTooltip);

    ArrayList<String> list = new ArrayList<String>();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.machine.ioMode.overlay.tooltip.visible.line");
    if(!list.isEmpty()) {
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
      configOverlay.setIsVisible(vis);
    }
    return true;
  }
}
