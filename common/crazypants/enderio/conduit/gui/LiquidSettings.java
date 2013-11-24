package crazypants.enderio.conduit.gui;

import java.awt.Color;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.render.ColorUtil;

public class LiquidSettings extends BaseSettingsPanel {

  static final int ID_AUTO_EXTRACT = 16;

  //private CheckBoxEIO autoExtractCB;
  private RedstoneModeButton rsB;

  private String autoExtractStr = "Auto Extract";

  private ILiquidConduit conduit;

  protected LiquidSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, ModObject.itemLiquidConduit.name, gui, con);

    conduit = (ILiquidConduit) con;

    int x = gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap * 2;
    int y = customTop;

    rsB = new RedstoneModeButton(gui, -99, x, y, new IRedstoneModeControlable() {

      RedstoneControlMode m = RedstoneControlMode.ON;

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        m = mode;
      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return m;
      }
    });

  }

  @Override
  protected void connectionModeChanged(ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    if(conectionMode == ConnectionMode.INPUT) {
      rsB.setMode(RedstoneControlMode.ON);
      rsB.onGuiInit();
    }

  }

  @Override
  public void deactivate() {
    super.deactivate();
    rsB.setToolTip((String[]) null);
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    int x = gui.getGuiLeft() + gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap + 2;
    int y = customTop;
    gui.getFontRenderer().drawString(autoExtractStr, left, top, ColorUtil.getRGB(Color.DARK_GRAY));
  }

}
