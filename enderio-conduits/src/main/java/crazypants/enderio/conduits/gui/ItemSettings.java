package crazypants.enderio.conduits.gui;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.conduit.item.IItemConduit;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.network.PacketExtractMode;
import crazypants.enderio.conduits.network.PacketItemConduitFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class ItemSettings extends BaseSettingsPanel {

  private static final int ID_REDSTONE_BUTTON = 12614;
  private static final int ID_COLOR_BUTTON = 179816;
  private static final int ID_LOOP = 22;
  private static final int ID_ROUND_ROBIN = 24;
  private static final int ID_PRIORITY_UP = 25;
  private static final int ID_PRIORITY_DOWN = 26;
  private static final int ID_INSERT_CHANNEL = 23;
  private static final int ID_EXTRACT_CHANNEL = 27;

  private @Nonnull IItemConduit itemConduit;

  private final ToggleButton loopB;
  private final ToggleButton roundRobinB;

  private final MultiIconButton priUpB;
  private final MultiIconButton priDownB;

  private final RedstoneModeButton<?> rsB;
  private final @Nonnull ColorButton colorB;

  private ColorButton insertChannelB;
  private ColorButton extractChannelB;

  private int priLeft = 46;
  private int priWidth = 32;

  public ItemSettings(@Nonnull final IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, ConduitObject.item_item_conduit.getUnlocalisedName(), gui, con, "filter_upgrade_settings");
    itemConduit = (IItemConduit) con;

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

    y += insertChannelB.getHeight() + 6;
    x = rightColumn;

    int x0 = x + 20;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x0, y);
    colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
    colorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());

    rsB = new RedstoneModeButton<>(gui, ID_REDSTONE_BUTTON, x, y, new ConduitRedstoneModeControlable(itemConduit, gui, colorB));

    x = priLeft + priWidth + 9;
    priUpB = MultiIconButton.createAddButton(gui, ID_PRIORITY_UP, x, y);
    priDownB = MultiIconButton.createMinusButton(gui, ID_PRIORITY_DOWN, x, y + 8);

  }

  @Override
  protected void initCustomOptions() {
    gui.getContainer().setInOutSlotsVisible(true, true, itemConduit);
    gui.getContainer().createGhostSlots(gui.getGhostSlotHandler().getGhostSlots());
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    updateButtons();
  }

  private void updateButtons() {
    rsB.onGuiInit();
    rsB.setMode(RedstoneControlMode.IconHolder.getFromMode(itemConduit.getExtractionRedstoneMode(gui.getDir())));

    loopB.onGuiInit();
    loopB.setSelected(itemConduit.isSelfFeedEnabled(gui.getDir()));
    roundRobinB.onGuiInit();
    roundRobinB.setSelected(itemConduit.isRoundRobinEnabled(gui.getDir()));

    priUpB.onGuiInit();
    priDownB.onGuiInit();

    insertChannelB.onGuiInit();
    insertChannelB.setColorIndex(itemConduit.getOutputColor(gui.getDir()).ordinal());
    extractChannelB.onGuiInit();
    extractChannelB.setColorIndex(itemConduit.getInputColor(gui.getDir()).ordinal());
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON) {
      itemConduit.setExtractionSignalColor(gui.getDir(), DyeColor.fromIndex(colorB.getColorIndex()));
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
      return;
    } else if (guiButton.id == ID_LOOP) {
      itemConduit.setSelfFeedEnabled(gui.getDir(), !itemConduit.isSelfFeedEnabled(gui.getDir()));
    } else if (guiButton.id == ID_ROUND_ROBIN) {
      itemConduit.setRoundRobinEnabled(gui.getDir(), !itemConduit.isRoundRobinEnabled(gui.getDir()));
    } else if (guiButton.id == ID_PRIORITY_UP) {
      itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) + 1);
    } else if (guiButton.id == ID_PRIORITY_DOWN) {
      itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) - 1);
    } else if (guiButton.id == ID_INSERT_CHANNEL) {
      DyeColor col = DyeColor.fromIndex(insertChannelB.getColorIndex());
      itemConduit.setOutputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_EXTRACT_CHANNEL) {
      DyeColor col = DyeColor.fromIndex(extractChannelB.getColorIndex());
      itemConduit.setInputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_INSERT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_OUTPUT_ITEM);
      return;
    } else if (guiButton.id == ID_EXTRACT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_INPUT_ITEM);
      return;
    }
    PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
  }

  @Override
  protected void connectionModeChanged(@Nonnull ConnectionMode mode) {
    super.connectionModeChanged(mode);
    PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
    updateGuiVisibility();
  }

  @Override
  protected void renderCustomOptions(int top1, float par1, int par2, int par3) {
    FontRenderer fr = gui.getFontRenderer();

    GlStateManager.color(1, 1, 1);
    IconEIO.map.render(EnderWidget.BUTTON_DOWN, left + priLeft, top1 - 5, priWidth, 16, 0, true);
    String str = itemConduit.getOutputPriority(gui.getDir()) + "";
    int sw = fr.getStringWidth(str);

    String priority = Lang.GUI_PRIORITY.get();
    fr.drawString(priority, left + 12, top1 + 25, ColorUtil.getRGB(Color.black));
    fr.drawString(str, left + priLeft + priWidth - sw - gap, top1 + 25, ColorUtil.getRGB(Color.black));
  }

  @Override
  public void deactivate() {
    super.deactivate();
    gui.getContainer().setInOutSlotsVisible(false, false, itemConduit);
    rsB.detach();
    colorB.detach();
    roundRobinB.detach();
    loopB.detach();
    priUpB.detach();
    priDownB.detach();
    insertChannelB.detach();
    extractChannelB.detach();
  }

  @Override
  protected boolean hasFilters() {
    return true;
  }

  @Override
  protected boolean hasUpgrades() {
    return true;
  }

}
