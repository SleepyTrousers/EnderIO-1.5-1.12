package crazypants.enderio.conduit.gui;

import java.awt.Color;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.oc.IOCConduit;
import crazypants.enderio.conduit.packet.PacketOCConduitSignalColor;
import net.minecraft.client.gui.GuiButton;

public class OCSettings extends BaseSettingsPanel {

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private ColorButton cb;

  private String signalColorStr = EnderIO.lang.localize("gui.conduit.redstone.color");
  private final IOCConduit occon;

  public OCSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_OC, EnderIO.lang.localize("itemOCConduit.name"), gui, con);
    occon = (IOCConduit) con;

    int x = 0;
    int y = customTop;

    x += gap + gap + 2 + gui.getFontRenderer().getStringWidth(signalColorStr);
    cb = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    cb.setToolTipHeading(EnderIO.lang.localize("gui.conduit.redstone.signalColor"));
    DyeColor sigCol = occon.getSignalColor(gui.getDir());
    cb.setColorIndex(sigCol.ordinal());
    x += cb.getButtonWidth();
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON && cb != null) {
      occon.setSignalColor(gui.getDir(), DyeColor.values()[cb.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketOCConduitSignalColor(occon, gui.getDir()));
    }
  }

  @Override
  protected void initCustomOptions() {
    if (cb != null) {
      cb.setColorIndex(cb.getColorIndex());
      cb.onGuiInit();
    }
  }

  @Override
  public void deactivate() {
    super.deactivate();
    if (cb != null) {
      cb.detach();
    }
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    if (cb != null) {
      gui.getFontRenderer().drawString(signalColorStr, left, top, ColorUtil.getRGB(Color.darkGray));
    }
  }

}
