package crazypants.enderio.conduits.gui;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.network.PacketRedstoneConduitOutputStrength;
import crazypants.enderio.conduits.network.PacketRedstoneConduitSignalColor;
import net.minecraft.client.gui.GuiButton;

public class RedstoneSettings extends BaseSettingsPanel {

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private static final int ID_STRONG_BUTTON = GuiExternalConnection.nextButtonId();
  private ColorButton cb;

  private CheckBox strongCB;

  private @Nonnull String signalColorStr = Lang.GUI_SIGNAL_COLOR.get();
  private @Nonnull String signalStrengthStr = Lang.GUI_REDSTONE_SIGNAL_STRENGTH.get();
  private IRedstoneConduit insCon;

  public RedstoneSettings(@Nonnull final IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_REDSTONE, ConduitObject.item_redstone_conduit.getUnlocalisedName(), gui, con, "in_out_settings", true);

    int x = leftColumn;
    int y = customTop + 4;

    if (con instanceof IRedstoneConduit) {
      insCon = (IRedstoneConduit) con;
    }

    if (insCon != null) {
      cb = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
      cb.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
      DyeColor sigCol = insCon.getSignalColor(gui.getDir());
      cb.setColorIndex(sigCol.ordinal());

      x = rightColumn;
      strongCB = new CheckBox(gui, ID_STRONG_BUTTON, x, y);
      strongCB.setToolTip(Lang.GUI_REDSTONE_SIGNAL_STRENGTH.get());
    }
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON && cb != null) {
      insCon.setSignalColor(gui.getDir(), DyeColor.values()[cb.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitSignalColor(insCon, gui.getDir()));
    } else if (guiButton.id == ID_STRONG_BUTTON && strongCB != null) {
      insCon.setOutputStrength(gui.getDir(), strongCB.isSelected());
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitOutputStrength(insCon, gui.getDir()));
    }
  }

  @Override
  protected void initCustomOptions() {
    if (insCon != null) {
      if (cb != null) {
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
    if (cb != null) {
      cb.detach();
    }
    if (strongCB != null) {
      strongCB.detach();
    }
  }

  @Override
  protected void renderCustomOptions(int topIn, float par1, int par2, int par3) {
    if (insCon != null) {
      if (cb != null) {
        gui.getFontRenderer().drawString(signalColorStr, left + 31, topIn + 6, ColorUtil.getRGB(Color.darkGray));
      }
      if (strongCB != null) {
        gui.getFontRenderer().drawString(signalStrengthStr, left + 121, topIn + 6, ColorUtil.getRGB(Color.darkGray));
      }
    }
  }

}
