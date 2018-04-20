package crazypants.enderio.conduits.gui;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduits.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.network.PacketEnderLiquidConduit;
import crazypants.enderio.conduits.network.PacketExtractMode;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class LiquidSettings extends BaseSettingsPanel {

  static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private static final int ID_INSERT_CHANNEL = GuiExternalConnection.nextButtonId();
  private static final int ID_EXTRACT_CHANNEL = GuiExternalConnection.nextButtonId();
  private static final int ID_PRIORITY_UP = GuiExternalConnection.nextButtonId();
  private static final int ID_PRIORITY_DOWN = GuiExternalConnection.nextButtonId();
  private static final int ID_ROUND_ROBIN = GuiExternalConnection.nextButtonId();
  private static final int ID_LOOP = GuiExternalConnection.nextButtonId();

  private final RedstoneModeButton rsB;
  private final ColorButton colorB;
  private boolean isEnder = false;
  private EnderLiquidConduit eCon;

  private ColorButton insertChannelB;
  private ColorButton extractChannelB;

  private final MultiIconButton priUpB;
  private final MultiIconButton priDownB;

  private final ToggleButton roundRobinB;
  private final ToggleButton loopB;

  private int priLeft = 46;
  private int priWidth = 32;

  private final ILiquidConduit conduit;

  public LiquidSettings(@Nonnull final IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, ConduitObject.item_liquid_conduit.getUnlocalisedName(), gui, con, "in_out_settings");

    conduit = (ILiquidConduit) con;
    if (con instanceof EnderLiquidConduit) {
      isEnder = true;
      eCon = (EnderLiquidConduit) con;
    }

    int x = leftColumn;
    int y = customTop;

    insertChannelB = new ColorButton(gui, ID_INSERT_CHANNEL, x, y);
    insertChannelB.setColorIndex(0);
    insertChannelB.setToolTipHeading(Lang.GUI_CONDUIT_CHANNEL.get());

    x = rightColumn;
    extractChannelB = new ColorButton(gui, ID_EXTRACT_CHANNEL, x, y);
    extractChannelB.setColorIndex(0);
    extractChannelB.setToolTipHeading(Lang.GUI_CONDUIT_CHANNEL.get());

    x += 4 + extractChannelB.getWidth();
    roundRobinB = new ToggleButton(gui, ID_ROUND_ROBIN, x, y, IconEIO.ROUND_ROBIN_OFF, IconEIO.ROUND_ROBIN);
    roundRobinB.setSelectedToolTip(Lang.GUI_ROUND_ROBIN_ENABLED.get());
    roundRobinB.setUnselectedToolTip(Lang.GUI_ROUND_ROBIN_DISABLED.get());
    roundRobinB.setPaintSelectedBorder(false);

    x += 4 + roundRobinB.getWidth();
    loopB = new ToggleButton(gui, ID_LOOP, x, y, IconEIO.LOOP_OFF, IconEIO.LOOP);
    loopB.setSelectedToolTip(Lang.GUI_SELF_FEED_ENABLED.get());
    loopB.setUnselectedToolTip(Lang.GUI_SELF_FEED_DISABLED.get());
    loopB.setPaintSelectedBorder(false);

    x = rightColumn;
    int x0 = x + 20;
    if (isEnder) {
      y += insertChannelB.getHeight() + 6;
    }
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x0, y);
    colorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
    colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());

    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new ConduitRedstoneModeControlable(conduit, gui, colorB));

    x = priLeft + priWidth + 9;

    priUpB = MultiIconButton.createAddButton(gui, ID_PRIORITY_UP, x, y);
    priDownB = MultiIconButton.createMinusButton(gui, ID_PRIORITY_DOWN, x, y + 8);

  }

  @Override
  @Nonnull
  public ResourceLocation getTexture() {
    return isEnder ? EnderIO.proxy.getGuiTexture("filter_upgrade_settings") : super.getTexture();
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
    } else if (guiButton.id == ID_INSERT_CHANNEL) {
      DyeColor col = DyeColor.values()[insertChannelB.getColorIndex()];
      eCon.setOutputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_EXTRACT_CHANNEL) {
      DyeColor col = DyeColor.values()[extractChannelB.getColorIndex()];
      eCon.setInputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_PRIORITY_UP) {
      eCon.setOutputPriority(gui.getDir(), eCon.getOutputPriority(gui.getDir()) + 1);
    } else if (guiButton.id == ID_PRIORITY_DOWN) {
      eCon.setOutputPriority(gui.getDir(), eCon.getOutputPriority(gui.getDir()) - 1);
    } else if (guiButton.id == ID_ROUND_ROBIN) {
      eCon.setRoundRobinEnabled(gui.getDir(), !eCon.isRoundRobinEnabled(gui.getDir()));
    } else if (guiButton.id == ID_LOOP) {
      eCon.setSelfFeedEnabled(gui.getDir(), !eCon.isSelfFeedEnabled(gui.getDir()));
    }
    if (isEnder) {
      PacketHandler.INSTANCE.sendToServer(new PacketEnderLiquidConduit(eCon, gui.getDir()));
    }
  }

  @Override
  protected void connectionModeChanged(@Nonnull ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    if (isEnder) {
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(eCon, gui.getDir()));
    }
    updateGuiVisibility();
  }

  @Override
  protected void initCustomOptions() {
    gui.getContainer().setInOutSlotsVisible(true, true, conduit);
    gui.getContainer().createGhostSlots(gui.getGhostSlotHandler().getGhostSlots());
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    rsB.onGuiInit();
    rsB.setMode(RedstoneControlMode.IconHolder.getFromMode(conduit.getExtractionRedstoneMode(gui.getDir())));

    if (isEnder) {
      insertChannelB.onGuiInit();
      insertChannelB.setColorIndex(eCon.getOutputColor(gui.getDir()).ordinal());
      extractChannelB.onGuiInit();
      extractChannelB.setColorIndex(eCon.getInputColor(gui.getDir()).ordinal());

      priUpB.onGuiInit();
      priDownB.onGuiInit();

      roundRobinB.onGuiInit();
      roundRobinB.setSelected(eCon.isRoundRobinEnabled(gui.getDir()));

      loopB.onGuiInit();
      loopB.setSelected(eCon.isSelfFeedEnabled(gui.getDir()));
    }
  }

  @Override
  public void deactivate() {
    gui.getContainer().setInOutSlotsVisible(false, false, conduit);
    rsB.detach();
    colorB.detach();
    insertChannelB.detach();
    extractChannelB.detach();
    priDownB.detach();
    priUpB.detach();
    roundRobinB.detach();
    loopB.detach();
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    if (!isEnder) {
      return;
    }
    FontRenderer fr = gui.getFontRenderer();

    GlStateManager.color(1, 1, 1);
    IconEIO.map.render(EnderWidget.BUTTON_DOWN, left + priLeft, top - 5, priWidth, 16, 0, true);
    String str = eCon.getOutputPriority(gui.getDir()) + "";
    int sw = fr.getStringWidth(str);

    String priority = Lang.GUI_PRIORITY.get();
    fr.drawString(priority, left + 12, top + 25, ColorUtil.getRGB(Color.black));
    fr.drawString(str, left + priLeft + priWidth - sw - gap, top + 25, ColorUtil.getRGB(Color.black));
  }

  @Override
  protected boolean hasFilters() {
    return true;
  }

}
