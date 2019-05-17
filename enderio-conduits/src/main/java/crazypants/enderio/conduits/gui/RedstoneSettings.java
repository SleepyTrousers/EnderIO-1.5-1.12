package crazypants.enderio.conduits.gui;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.network.PacketRedstoneConduitOutputStrength;
import crazypants.enderio.conduits.network.PacketRedstoneConduitSignalColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

public class RedstoneSettings extends BaseSettingsPanel {

  private static final int ID_INPUT_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private static final int ID_STRONG_BUTTON = GuiExternalConnection.nextButtonId();
  private static final int ID_OUTPUT_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private @Nonnull ColorButton inputColorB;
  private @Nonnull ColorButton outputColorB;

  private CheckBox strongCB;

  private @Nonnull String signalColorStr = Lang.GUI_SIGNAL_COLOR.get();
  private @Nonnull String signalStrengthStr = Lang.GUI_REDSTONE_SIGNAL_STRENGTH.get();
  private @Nonnull IRedstoneConduit insCon;

  public RedstoneSettings(@Nonnull final IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_REDSTONE, ConduitObject.item_redstone_conduit.getUnlocalisedName(), gui, con, "filter_settings");

    int x = leftColumn;
    int y = customTop + 4;

    insCon = (IRedstoneConduit) con;

    inputColorB = new ColorButton(gui, ID_INPUT_COLOR_BUTTON, x, y);
    inputColorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
    DyeColor sigCol = insCon.getInputSignalColor(gui.getDir());
    inputColorB.setColorIndex(sigCol.ordinal());

    x = rightColumn;
    outputColorB = new ColorButton(gui, ID_OUTPUT_COLOR_BUTTON, x, y);
    outputColorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
    DyeColor sigColOut = insCon.getOutputSignalColor(gui.getDir());
    outputColorB.setColorIndex(sigColOut.ordinal());

    y += 20;
    strongCB = new CheckBox(gui, ID_STRONG_BUTTON, x, y);
    strongCB.setToolTip(Lang.GUI_REDSTONE_SIGNAL_STRENGTH.get());
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_INPUT_COLOR_BUTTON) {
      insCon.setInputSignalColor(gui.getDir(), DyeColor.values()[inputColorB.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitSignalColor(insCon, gui.getDir(), true));
    } else if (guiButton.id == ID_OUTPUT_COLOR_BUTTON) {
      insCon.setOutputSignalColor(gui.getDir(), DyeColor.values()[outputColorB.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitSignalColor(insCon, gui.getDir(), false));
    } else if (guiButton.id == ID_STRONG_BUTTON && strongCB != null) {
      insCon.setOutputStrength(gui.getDir(), strongCB.isSelected());
      PacketHandler.INSTANCE.sendToServer(new PacketRedstoneConduitOutputStrength(insCon, gui.getDir()));
    } else if (guiButton.id == ID_INSERT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_OUTPUT_REDSTONE);
      return;
    } else if (guiButton.id == ID_EXTRACT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_INPUT_REDSTONE);
      return;
    }
  }

  @Override
  protected void initCustomOptions() {
    gui.getContainer().setInOutSlotsVisible(true, false, insCon);
    createGhostSlots();

    inputColorB.setColorIndex(insCon.getInputSignalColor(gui.getDir()).ordinal());
    inputColorB.onGuiInit();
    outputColorB.setColorIndex(insCon.getOutputSignalColor(gui.getDir()).ordinal());
    outputColorB.onGuiInit();
    strongCB.onGuiInit();
    strongCB.setSelected(insCon.isOutputStrong(gui.getDir()));
    filtersChanged();
  }

  @Override
  public boolean updateConduit(@Nonnull IClientConduit conduit) {
    this.con = conduit;
    // Note: Redstone conduits track their connections a bit differently to other conduits, so we need to catch the case that it is not actually
    // connected to anything and display it in the GUI as such.
    final ConnectionMode connectionMode = con.containsExternalConnection(gui.getDir()) ? con.getConnectionMode(gui.getDir()) : ConnectionMode.NOT_SET;
    if (oldConnectionMode != connectionMode) {
      connectionModeChanged(connectionMode);
    }
    return true;
  }

  private void createGhostSlots() {
    NNList<ItemStack> filtersOut = new NNList<>(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.REDSTONE_FILTER_BASE.ordinal()),
        new ItemStack(ModObject.itemRedstoneAndFilter.getItemNN()), new ItemStack(ModObject.itemRedstoneNandFilter.getItemNN()),
        new ItemStack(ModObject.itemRedstoneNorFilter.getItemNN()), new ItemStack(ModObject.itemRedstoneNotFilter.getItemNN()),
        new ItemStack(ModObject.itemRedstoneOrFilter.getItemNN()), new ItemStack(ModObject.itemRedstoneToggleFilter.getItemNN()),
        new ItemStack(ModObject.itemRedstoneXnorFilter.getItemNN()), new ItemStack(ModObject.itemRedstoneXorFilter.getItemNN()));
    NNList<ItemStack> filtersIn = new NNList<>(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.REDSTONE_FILTER_BASE.ordinal()),
        new ItemStack(ModObject.itemRedstoneSensorFilter.getItemNN()), new ItemStack(ModObject.itemRedstoneTimerFilter.getItemNN()));
    NNList<ItemStack> upgrades = new NNList<>();
    gui.getContainer().createGhostSlots(gui.getGhostSlotHandler().getGhostSlots(), filtersIn, filtersOut, upgrades);
  }

  @Override
  public void deactivate() {
    super.deactivate();
    gui.getContainer().setInOutSlotsVisible(false, false, insCon);
    inputColorB.detach();
    outputColorB.detach();
    strongCB.detach();
  }

  @Override
  protected void renderCustomOptions(int topIn, float par1, int par2, int par3) {
    gui.getFontRenderer().drawString(signalColorStr, left + 31, topIn + 6, ColorUtil.getRGB(Color.darkGray));
    gui.getFontRenderer().drawString(signalColorStr, left + 121, topIn + 6, ColorUtil.getRGB(Color.darkGray));
    gui.getFontRenderer().drawString(signalStrengthStr, left + 121, topIn + 26, ColorUtil.getRGB(Color.darkGray));
  }

  @Override
  @Nonnull
  protected String getInputHeading() {
    return Lang.GUI_REDSTONE_CONDUIT_INPUT_MODE.get();
  }

  @Override
  @Nonnull
  protected String getOutputHeading() {
    return Lang.GUI_REDSTONE_CONDUIT_OUTPUT_MODE.get();
  }

  @Override
  protected boolean hasFilterGui(boolean output) {
    IFilterContainer<?> container = (IFilterContainer<?>) gui.getContainer();
    IFilter filter = container.getFilter(!output ? FilterGuiUtil.INDEX_INPUT_REDSTONE : FilterGuiUtil.INDEX_OUTPUT_REDSTONE);
    if (filter != null) {
      return filter.hasGui();
    }
    return super.hasFilterGui(output);
  }

  @Override
  protected boolean hasFilters() {
    return true;
  }

}
