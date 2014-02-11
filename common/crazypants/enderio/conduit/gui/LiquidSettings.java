package crazypants.enderio.conduit.gui;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.conduit.ConduitPacketHandler;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.gui.ColorButton;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.render.ColorUtil;
import crazypants.util.DyeColor;
import crazypants.util.Lang;

public class LiquidSettings extends BaseSettingsPanel {

  static final int ID_REDSTONE_BUTTON = 16;

  private static final int ID_COLOR_BUTTON = 17;

  private RedstoneModeButton rsB;

  private ColorButton colorB;

  private String autoExtractStr = Lang.localize("gui.conduit.fluid.autoExtract");

  private ILiquidConduit conduit;

  protected LiquidSettings(final GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, Lang.localize("itemLiquidConduit.name"), gui, con);

    conduit = (ILiquidConduit) con;

    int x = gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap * 2;
    int y = customTop;

    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        conduit.setExtractionRedstoneMode(mode, gui.dir);
        if(curMode != mode) {
          Packet pkt = ConduitPacketHandler.createExtractionModePacket(conduit, gui.dir, mode);
          PacketDispatcher.sendPacketToServer(pkt);
        }

      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return conduit.getExtractioRedstoneMode(gui.dir);
      }
    });

    x += rsB.getWidth() + gap;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setToolTipHeading(Lang.localize("gui.conduit.redstone.signalColor"));
    colorB.setColorIndex(conduit.getExtractionSignalColor(gui.dir).ordinal());
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if(guiButton.id == ID_COLOR_BUTTON) {
      Packet pkt = ConduitPacketHandler.createSignalColorPacket(conduit, gui.dir, DyeColor.values()[colorB.getColorIndex()]);
      PacketDispatcher.sendPacketToServer(pkt);
    }
  }

  @Override
  protected void connectionModeChanged(ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    if(conectionMode == ConnectionMode.INPUT) {
      rsB.onGuiInit();
      colorB.onGuiInit();
    } else {
      gui.removeButton(rsB);
      gui.removeButton(colorB);
    }

  }

  @Override
  public void deactivate() {
    super.deactivate();
    rsB.setToolTip((String[]) null);
    colorB.setToolTip((String[]) null);
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    if(conduit.getConectionMode(gui.dir) == ConnectionMode.INPUT) {
      int x = gui.getGuiLeft() + gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap + 2;
      int y = customTop;
      gui.getFontRenderer().drawString(autoExtractStr, left, top, ColorUtil.getRGB(Color.DARK_GRAY));
    }
  }

}
