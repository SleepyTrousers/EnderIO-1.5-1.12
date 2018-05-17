package crazypants.enderio.conduits.oc.gui;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduits.gui.BaseSettingsPanel;
import crazypants.enderio.conduits.gui.GuiExternalConnection;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.oc.conduit.IOCConduit;
import crazypants.enderio.conduits.oc.init.ConduitOpenComputersObject;
import crazypants.enderio.conduits.oc.network.PacketHandler;
import crazypants.enderio.conduits.oc.network.PacketOCConduitSignalColor;
import net.minecraft.client.gui.GuiButton;

public class OCSettings extends BaseSettingsPanel {

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private @Nonnull ColorButton cb;

  private @Nonnull String signalColorStr = Lang.GUI_SIGNAL_COLOR.get();
  private final @Nonnull IOCConduit occon;

  public OCSettings(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_OC, ConduitOpenComputersObject.item_opencomputers_conduit.getUnlocalisedName(), gui, con, "simple_settings");
    occon = (IOCConduit) con;

    int x = leftColumn;
    int y = customTop;

    cb = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    cb.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
    DyeColor sigCol = occon.getSignalColor(gui.getDir());
    cb.setColorIndex(sigCol.ordinal());
    x += cb.getButtonWidth();
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON) {
      occon.setSignalColor(gui.getDir(), DyeColor.values()[cb.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketOCConduitSignalColor(occon, gui.getDir()));
    }
  }

  @Override
  protected void initCustomOptions() {
    cb.setColorIndex(cb.getColorIndex());
    cb.onGuiInit();
  }

  @Override
  public void deactivate() {
    super.deactivate();
    cb.detach();
  }

  @Override
  protected void renderCustomOptions(int topIn, float par1, int par2, int par3) {
    gui.getFontRenderer().drawString(signalColorStr, left + leftColumn + 10, topIn + 2, ColorUtil.getRGB(Color.darkGray));
  }

  @Override
  protected boolean hasInOutModes() {
    return false;
  }

}
