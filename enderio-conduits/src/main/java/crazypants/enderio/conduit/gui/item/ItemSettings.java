package crazypants.enderio.conduit.gui.item;

import java.awt.Color;
import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IFilterChangeListener;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.network.IOpenFilterRemoteExec;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.gui.BaseSettingsPanel;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.init.ConduitObject;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.lang.Lang;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class ItemSettings extends BaseSettingsPanel implements IOpenFilterRemoteExec.GUI {

  private static final int ID_REDSTONE_BUTTON = 12614;
  private static final int ID_COLOR_BUTTON = 179816;
  private static final int ID_LOOP = 22;
  private static final int ID_ROUND_ROBIN = 24;
  private static final int ID_PRIORITY_UP = 25;
  private static final int ID_PRIORITY_DOWN = 26;
  private static final int ID_INSERT_CHANNEL = 23;
  private static final int ID_EXTRACT_CHANNEL = 27;
  private static final int ID_INSERT_FILTER_OPTIONS = 28;
  private static final int ID_EXTRACT_FILTER_OPTIONS = 29;

  private IItemConduit itemConduit;

  private final ToggleButton loopB;
  private final ToggleButton roundRobinB;

  private final MultiIconButton priUpB;
  private final MultiIconButton priDownB;

  private final RedstoneModeButton<?> rsB;
  private final ColorButton colorB;

  private ColorButton insertChannelB;
  private ColorButton extractChannelB;

  private IconButton insertFilterOptionsB;
  private IconButton extractFilterOptionsB;

  private int priLeft = 46;
  private int priWidth = 32;

  private final GuiToolTip speedUpgradeTooltip;
  private final GuiToolTip functionUpgradeTooltip;
  private final GuiToolTip filterExtractUpgradeTooltip;
  private final GuiToolTip filterInsertUpgradeTooltip;

  public ItemSettings(@Nonnull final GuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, ConduitObject.item_item_conduit.getUnlocalisedName(), gui, con, "item_settings");
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

    filterExtractUpgradeTooltip = new GuiToolTip(new Rectangle(rightColumn, 70, 18, 18), Lang.GUI_ITEM_FILTER_UPGRADE.get()) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasFilter(false) && super.shouldDraw();
      }
    };

    filterInsertUpgradeTooltip = new GuiToolTip(new Rectangle(leftColumn, 70, 18, 18), Lang.GUI_ITEM_FILTER_UPGRADE.get()) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasFilter(true) && super.shouldDraw();
      }
    };

    speedUpgradeTooltip = new GuiToolTip(new Rectangle(x - 22, customTop + 43, 18, 18), Lang.GUI_ITEM_SPEED_UPGRADE.get(),
        Lang.GUI_ITEM_SPEED_UPGRADE_2.get()) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasSpeedUpgrades() && super.shouldDraw();
      }
    };

    // ArrayList<String> list = new ArrayList<String>();
    // SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.conduit.item.functionupgrade.line");
    // for (FunctionUpgrade upgrade : FunctionUpgrade.values()) {
    // list.add(EnderIO.lang.localizeExact(upgrade.unlocName.concat(".name")));
    // }
    functionUpgradeTooltip = new GuiToolTip(new Rectangle(x + 4, customTop + 43, 18, 18), Lang.GUI_ITEM_FUNCTION_UPGRADE.get(),
        Lang.GUI_ITEM_FUNCTION_UPGRADE_2.get()) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasFunctionUpgrade() && super.shouldDraw();
      }
    };

    y += insertChannelB.getHeight() + 6;
    x = rightColumn;

    rsB = new RedstoneModeButton<>(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(@Nonnull RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        itemConduit.setExtractionRedstoneMode(mode, gui.getDir());
        if (mode == RedstoneControlMode.OFF || mode == RedstoneControlMode.ON) {
          colorB.onGuiInit();
          colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
          colorB.setIsVisible(true);
        } else {
          colorB.setIsVisible(false);
        }
        if (curMode != mode) {
          PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
        }

      }

      @Override
      @Nonnull
      public RedstoneControlMode getRedstoneControlMode() {
        return itemConduit.getExtractionRedstoneMode(gui.getDir());
      }

      @Override
      public boolean getRedstoneControlStatus() {
        return false;
      }
    });

    x += rsB.getWidth() + 4;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
    colorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());

    x = priLeft + priWidth + 9;
    priUpB = MultiIconButton.createAddButton(gui, ID_PRIORITY_UP, x, y);
    priDownB = MultiIconButton.createMinusButton(gui, ID_PRIORITY_DOWN, x, y + 8);

    gui.getContainer().addFilterListener(new IFilterChangeListener() {
      @Override
      public void onFilterChanged() {
        filtersChanged();
      }
    });

    x = leftColumn;
    y = 92;

    insertFilterOptionsB = new IconButton(gui, ID_INSERT_FILTER_OPTIONS, x, y, IconEIO.GEAR_LIGHT);
    insertFilterOptionsB.setToolTip(crazypants.enderio.base.lang.Lang.GUI_EDIT_ITEM_FILTER.get());

    x = rightColumn;

    extractFilterOptionsB = new IconButton(gui, ID_EXTRACT_FILTER_OPTIONS, x, y, IconEIO.GEAR_LIGHT);
    extractFilterOptionsB.setToolTip(crazypants.enderio.base.lang.Lang.GUI_EDIT_ITEM_FILTER.get());

    gui.getContainer().setInOutSlotsVisible(true, true);

  }

  @Override
  protected void initCustomOptions() {
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    deactivate();
    updateButtons();
  }

  private void filtersChanged() {
    insertFilterOptionsB.onGuiInit();
    extractFilterOptionsB.onGuiInit();

    if (gui.getContainer().hasFilter(true)) {
      insertFilterOptionsB.setIsVisible(true);
    } else {
      insertFilterOptionsB.setIsVisible(false);
    }

    if (gui.getContainer().hasFilter(false)) {
      extractFilterOptionsB.setIsVisible(true);
    } else {
      extractFilterOptionsB.setIsVisible(false);
    }
  }

  private void updateButtons() {
    gui.getContainer().setInOutSlotsVisible(true, true);
    rsB.onGuiInit();
    rsB.setMode(RedstoneControlMode.IconHolder.getFromMode(itemConduit.getExtractionRedstoneMode(gui.getDir())));

    gui.addToolTip(filterExtractUpgradeTooltip);
    gui.addToolTip(filterInsertUpgradeTooltip);
    gui.addToolTip(functionUpgradeTooltip);
    loopB.onGuiInit();
    loopB.setSelected(itemConduit.isSelfFeedEnabled(gui.getDir()));
    roundRobinB.onGuiInit();
    roundRobinB.setSelected(itemConduit.isRoundRobinEnabled(gui.getDir()));
    gui.addToolTip(speedUpgradeTooltip);

    priUpB.onGuiInit();
    priDownB.onGuiInit();

    insertChannelB.onGuiInit();
    insertChannelB.setColorIndex(itemConduit.getOutputColor(gui.getDir()).ordinal());
    extractChannelB.onGuiInit();
    extractChannelB.setColorIndex(itemConduit.getInputColor(gui.getDir()).ordinal());

    filtersChanged();
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON) {
      itemConduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
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
      DyeColor col = DyeColor.values()[insertChannelB.getColorIndex()];
      itemConduit.setOutputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_EXTRACT_CHANNEL) {
      DyeColor col = DyeColor.values()[extractChannelB.getColorIndex()];
      itemConduit.setInputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_INSERT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_OUTPUT);
      return;
    } else if (guiButton.id == ID_EXTRACT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_INPUT);
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
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    FontRenderer fr = gui.getFontRenderer();

    GlStateManager.color(1, 1, 1);
    IconEIO.map.render(EnderWidget.BUTTON_DOWN, left + priLeft, top - 5, priWidth, 16, 0, true);
    String str = itemConduit.getOutputPriority(gui.getDir()) + "";
    int sw = fr.getStringWidth(str);

    String priority = Lang.GUI_PRIORITY.get();
    fr.drawString(priority, left + 12, top + 25, ColorUtil.getRGB(Color.black));
    fr.drawString(str, left + priLeft + priWidth - sw - gap, top + 25, ColorUtil.getRGB(Color.black));
  }

  @Override
  public void deactivate() {
    gui.getContainer().setInOutSlotsVisible(false, false);
    rsB.detach();
    colorB.detach();
    roundRobinB.detach();
    loopB.detach();
    priUpB.detach();
    priDownB.detach();
    gui.removeToolTip(speedUpgradeTooltip);
    gui.removeToolTip(functionUpgradeTooltip);
    gui.removeToolTip(filterExtractUpgradeTooltip);
    gui.removeToolTip(filterInsertUpgradeTooltip);
    insertChannelB.detach();
    extractChannelB.detach();
    insertFilterOptionsB.detach();
    extractFilterOptionsB.detach();
  }

  @Override
  public void setGuiID(int id) {
    gui.setGuiID(id);
  }

  @Override
  public int getGuiID() {
    return gui.getGuiID();
  }

}
