package crazypants.enderio.conduit.gui.me;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.gui.BaseSettingsPanel;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.gui.IconEIO;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class MESettings extends BaseSettingsPanel {

  public MESettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ME, Lang.localize("itemMEConduit.name"), gui, con);
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    super.renderCustomOptions(top, par1, par2, par3);
    
    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
    int x = gui.getGuiLeft() + 49;
    int y = gui.getGuiTop() + 49;
    gui.drawTexturedModalRect(x, y, 0, 238, 18, 18);
    
    x = gui.getGuiLeft();
    y = gui.getGuiTop() + 100;
    gui.drawTexturedModalRect(x, y, 0, 100, 256, 100);
  }

  @Override
  protected void initCustomOptions() {
    gui.getContainer().setInventorySlotsVisible(true);
    gui.getContainer().setMeSlotsVisible(true);
  }

  @Override
  public void deactivate() {
    gui.getContainer().setInventorySlotsVisible(false);
    gui.getContainer().setMeSlotsVisible(false);
  }
}
