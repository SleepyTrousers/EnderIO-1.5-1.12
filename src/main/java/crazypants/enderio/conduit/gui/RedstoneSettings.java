package crazypants.enderio.conduit.gui;

import java.awt.Color;

import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.packet.PacketRedstoneConduitOutputStrength;
import crazypants.enderio.conduit.packet.PacketRedstoneConduitSignalColor;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;

public class RedstoneSettings extends BaseSettingsPanel {

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private static final int ID_STRONG_BUTTON = GuiExternalConnection.nextButtonId();
  private ColorButton cb;

  private CheckBox strongCB;

  private String signalColorStr = EnderIO.lang.localize("gui.conduit.redstone.color");
  private String signalStringthStr = EnderIO.lang.localize("gui.conduit.redstone.signalStrengh");
  private IRedstoneConduit insCon;

  private int stongLabelX;

  public RedstoneSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_REDSTONE, EnderIO.lang.localize("itemRedstoneConduitInsulated.name"), gui, con);

    int x = 0;
    int y = customTop;

    if(con instanceof IRedstoneConduit) {
      insCon = (IRedstoneConduit) con;
    }

    if(insCon != null) {
      if(!insCon.isSpecialConnection(gui.getDir())) {
        x += gap + gap + 2 + gui.getFontRenderer().getStringWidth(signalColorStr);
        cb = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
        cb.setToolTipHeading(EnderIO.lang.localize("gui.conduit.redstone.signalColor"));
        DyeColor sigCol = insCon.getSignalColor(gui.getDir());
        cb.setColorIndex(sigCol.ordinal());
        x += cb.getButtonWidth();

      }
      stongLabelX = x;
      x += gap + gui.getFontRenderer().getStringWidth(signalStringthStr) + gap + 3;
      strongCB = new CheckBox(gui, ID_STRONG_BUTTON, x, y);
      strongCB.setToolTip(EnderIO.lang.localize("gui.conduit.redstone.signalStrengh.tooltip"));
    }
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if(guiButton.id == ID_COLOR_BUTTON && cb != null) {
      insCon.setSignalColor(gui.getDir(), DyeColor.values()[cb.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitSignalColor(insCon, gui.getDir()));
    } else if(guiButton.id == ID_STRONG_BUTTON && strongCB != null) {
      insCon.setOutputStrength(gui.getDir(), strongCB.isSelected());
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitOutputStrength(insCon, gui.getDir()));
    }
  }

  @Override
  protected void initCustomOptions() {
    if(insCon != null) {
      if(cb != null) {
        cb.setColorIndex(cb.getColorIndex());
        cb.onGuiInit();
      }
      strongCB.onGuiInit();
      strongCB.setSelected(insCon.isOutputStrong(gui.getDir()));
    }
  }

  @Override
  public void deactivate() {
    super.deactivate();
    if(cb != null) {
      cb.detach();
    }
    if(strongCB != null) {
      strongCB.detach();
    }
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    if(insCon != null) {
      if(cb != null) {
        gui.getFontRenderer().drawString(signalColorStr, left, top, ColorUtil.getRGB(Color.darkGray));
      }
      if(strongCB != null) {
        gui.getFontRenderer().drawString(signalStringthStr, left + stongLabelX, top, ColorUtil.getRGB(Color.darkGray));
      }
    }
  }

}
