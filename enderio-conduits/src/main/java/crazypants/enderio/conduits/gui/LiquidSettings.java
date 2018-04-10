package crazypants.enderio.conduits.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduits.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.network.PacketConduitFilter;
import crazypants.enderio.conduits.network.PacketExtractMode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class LiquidSettings extends BaseSettingsPanel {

  static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();

  private final RedstoneModeButton rsB;
  private final ColorButton colorB;
  private boolean isEnder = false;

  private final ILiquidConduit conduit;

  public LiquidSettings(@Nonnull final GuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, ConduitObject.item_liquid_conduit.getUnlocalisedName(), gui, con, "liquid_settings");

    conduit = (ILiquidConduit) con;
    if (con instanceof EnderLiquidConduit) {
      isEnder = true;
    }

    int x = rightColumn;
    int y = customTop;

    int x0 = x + 20;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x0, y);
    colorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
    colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());

    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new ConduitRedstoneModeControlable(conduit, gui, colorB));

  }

  @Override
  @Nonnull
  public ResourceLocation getTexture() {
    return isEnder ? EnderIO.proxy.getGuiTexture("ender_liquid_settings") : super.getTexture();
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON) {
      conduit.setExtractionSignalColor(gui.getDir(), DyeColor.fromIndex(colorB.getColorIndex()));
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
    } else if (guiButton.id == ID_INSERT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_OUTPUT_FLUID);
      return;
    } else if (guiButton.id == ID_EXTRACT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_INPUT_FLUID);
      return;
    }
    if (isEnder) {
      PacketHandler.INSTANCE.sendToServer(new PacketConduitFilter(conduit, gui.getDir()));
    }
  }

  @Override
  protected void connectionModeChanged(@Nonnull ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    updateGuiVisibility();
  }

  @Override
  protected void initCustomOptions() {
    gui.getContainer().setInOutSlotsVisible(true, false, conduit);
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    rsB.onGuiInit();
    rsB.setMode(RedstoneControlMode.IconHolder.getFromMode(conduit.getExtractionRedstoneMode(gui.getDir())));
  }

  @Override
  public void deactivate() {
    gui.getContainer().setInOutSlotsVisible(false, false, conduit);
    rsB.detach();
    colorB.detach();
  }

  @Override
  protected boolean hasFilters() {
    return true;
  }

}
