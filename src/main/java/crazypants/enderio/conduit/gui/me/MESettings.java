package crazypants.enderio.conduit.gui.me;

import java.awt.Color;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.gui.BaseSettingsPanel;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.gui.IconEIO;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class MESettings extends BaseSettingsPanel {

  private IMEConduit conduit;

  public MESettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ME, Lang.localize("itemMEConduit.name"), gui, con);

    this.conduit = (IMEConduit) con;
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    super.renderCustomOptions(top, par1, par2, par3);
    
    if (conduit.isDense()) {
      return;
    }

    int x = gui.getGuiLeft() + 35;
    int y = gui.getGuiTop() + 40;

    String str = "Insert Bus";
    int width = gui.getFontRenderer().getStringWidth(str);
    x -= width / 2;
    gui.getFontRenderer().drawString(str, x, y, ColorUtil.getRGB(Color.darkGray));

    ItemStack bus = gui.getContainer().getSlot(gui.getContainer().getMEBusSlot()).getStack();
    if(bus != null) {
      str = "\u2794 " + bus.getDisplayName();
      width = gui.getFontRenderer().getStringWidth(str);
      x = gui.getGuiLeft() + 52 + (width / 2);
      gui.getFontRenderer().drawString(str, x, y, ColorUtil.getRGB(Color.darkGray));
    }

    GL11.glColor4f(1, 1, 1, 1);

    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
    x = gui.getGuiLeft() + 70;
    y = gui.getGuiTop() + 35;
    gui.drawTexturedModalRect(x, y, 0, 238, 18, 18);

    x = gui.getGuiLeft();
    y = gui.getGuiTop() + 100;
    gui.drawTexturedModalRect(x, y, 0, 100, 256, 100);
  }

  @Override
  protected void initCustomOptions() {
    if(!conduit.isDense()) {
      gui.getContainer().setInventorySlotsVisible(true);
      gui.getContainer().setMeSlotsVisible(true);
    }
  }

  @Override
  public void deactivate() {
    if(!conduit.isDense()) {
      gui.getContainer().setInventorySlotsVisible(false);
      gui.getContainer().setMeSlotsVisible(false);
    }
  }
}
