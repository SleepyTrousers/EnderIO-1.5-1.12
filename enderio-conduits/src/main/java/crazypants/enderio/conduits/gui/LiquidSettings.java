package crazypants.enderio.conduits.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduits.conduit.liquid.FluidFilter;
import crazypants.enderio.conduits.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.network.PacketExtractMode;
import crazypants.enderio.conduits.network.PacketFluidFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class LiquidSettings extends BaseSettingsPanel {

  static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private static final int ID_INSERT_WHITELIST = GuiExternalConnection.nextButtonId();
  private static final int ID_EXTRACT_WHITELIST = GuiExternalConnection.nextButtonId();

  private final RedstoneModeButton rsB;
  private final ColorButton colorB;

  @Nonnull
  private final String autoExtractStr = Lang.GUI_LIQUID_AUTO_EXTRACT.get();
  @Nonnull
  private final String filterStrInsert = Lang.GUI_LIQUID_FILTER.get();
  @Nonnull
  private final String filterStrExtract = Lang.GUI_LIQUID_FILTER.get();

  private final ILiquidConduit conduit;

  private EnderLiquidConduit eConduit;
  private boolean isEnder;
  private static final int filterInsertX = 7;
  private static final int filterY = 81;
  private static final int filterExtractX = filterInsertX + 101;
  private static final Rectangle insertFilterBounds = new Rectangle(filterInsertX, filterY, 90, 18);
  private static final Rectangle extractFilterBounds = new Rectangle(filterExtractX, filterY, 90, 18);
  private GuiToolTip[] filterToolTips;

  private IconButton insertWhiteListB;
  private IconButton extractWhiteListB;

  private final GuiToolTip insertFilterTooltip;
  private final GuiToolTip extractFilterTooltip;

  public LiquidSettings(@Nonnull final GuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, ConduitObject.item_liquid_conduit.getUnlocalisedName(), gui, con, "liquid_settings");

    conduit = (ILiquidConduit) con;
    if (con instanceof EnderLiquidConduit) {
      eConduit = (EnderLiquidConduit) con;
      isEnder = true;

      int x = leftColumn;
      int y = filterY - 20;

      insertWhiteListB = new IconButton(gui, ID_INSERT_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
      insertWhiteListB.setToolTip(Lang.GUI_LIQUID_WHITELIST.get());

      x = rightColumn;

      extractWhiteListB = new IconButton(gui, ID_EXTRACT_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
      extractWhiteListB.setToolTip(Lang.GUI_LIQUID_WHITELIST.get());
    } else {
      isEnder = false;
    }

    int x = rightColumn;
    int y = customTop;

    int x0 = x + 20;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x0, y);
    colorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
    colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());

    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new ConduitRedstoneModeControlable(conduit, gui, colorB));

    insertFilterTooltip = new GuiToolTip(insertFilterBounds, Lang.GUI_LIQUID_FILTER.get()) {
      @Override
      public boolean shouldDraw() {
        return super.shouldDraw() && isEnder;
      }
    };
    extractFilterTooltip = new GuiToolTip(extractFilterBounds, Lang.GUI_LIQUID_FILTER.get()) {
      @Override
      public boolean shouldDraw() {
        return super.shouldDraw() && isEnder;
      }
    };
  }

  private void addFilterTooltips() {
    filterToolTips = new GuiToolTip[5];
    for (int i = 0; i < 5; i++) {
      Rectangle bound = new Rectangle(filterInsertX + (i * 18), filterY, 18, 18);
      filterToolTips[i] = new FilterToolTip(bound, i);
      gui.addToolTip(filterToolTips[i]);
    }
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON) {
      conduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
    } else if (guiButton.id == ID_INSERT_WHITELIST) {
      toggleBlacklist(false);
    } else if (guiButton.id == ID_EXTRACT_WHITELIST) {
      toggleBlacklist(true);
    }
  }

  @Override
  protected void connectionModeChanged(@Nonnull ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    updateGuiVisibility();
  }

  private void toggleBlacklist(boolean isInput) {
    if (!isFilterVisible()) {
      return;
    }
    FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput);
    if (filter == null) {
      filter = new FluidFilter();
    }
    filter.setBlacklist(!filter.isBlacklist());
    setConduitFilter(filter, isInput);
    updateWhiteListButton(filter, isInput);
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {

    if (!isFilterVisible()) {
      return;
    }

    if (!insertFilterBounds.contains(x, y) && !extractFilterBounds.contains(x, y)) {
      return;
    }

    boolean isInput = (x >= filterExtractX);

    ItemStack st = Minecraft.getMinecraft().player.inventory.getItemStack();
    FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput);

    if (filter == null) {
      filter = new FluidFilter();
    }
    int slot = 0;
    if (isInput) {
      slot = (x - filterExtractX) / 18;
    } else {
      slot = (x - filterInsertX) / 18;
    }
    filter.setFluid(slot, st);
    setConduitFilter(filter, isInput);

  }

  protected void setConduitFilter(FluidFilter filter, boolean isInput) {
    eConduit.setFilter(gui.getDir(), filter, isInput);
    PacketHandler.INSTANCE.sendToServer(new PacketFluidFilter(eConduit, gui.getDir(), filter, isInput));
  }

  @Override
  protected void initCustomOptions() {
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    deactivate();
    rsB.onGuiInit();
    rsB.setMode(RedstoneControlMode.IconHolder.getFromMode(conduit.getExtractionRedstoneMode(gui.getDir())));

    if (!isEnder) {
      return;
    }

    if (isFilterVisible()) {
      addFilterTooltips();

      insertWhiteListB.onGuiInit();
      updateWhiteListButton(eConduit.getFilter(gui.getDir(), false), false);

      extractWhiteListB.onGuiInit();
      updateWhiteListButton(eConduit.getFilter(gui.getDir(), true), true);
    }

    gui.addToolTip(insertFilterTooltip);
    gui.addToolTip(extractFilterTooltip);
  }

  private void updateWhiteListButton(FluidFilter filter, boolean isInput) {
    IconButton whitelistButton = null;
    if (isInput) {
      whitelistButton = extractWhiteListB;
    } else {
      whitelistButton = insertWhiteListB;
    }
    if (filter != null && filter.isBlacklist()) {
      whitelistButton.setIcon(IconEIO.FILTER_BLACKLIST);
      whitelistButton.setToolTip(Lang.GUI_LIQUID_BLACKLIST.get());
    } else {
      whitelistButton.setIcon(IconEIO.FILTER_WHITELIST);
      whitelistButton.setToolTip(Lang.GUI_LIQUID_WHITELIST.get());
    }
  }

  @Override
  public void deactivate() {

    rsB.detach();
    colorB.detach();
    if (isEnder) {
      if (filterToolTips != null) {
        for (GuiToolTip tt : filterToolTips) {
          if (tt != null) {
            gui.removeToolTip(tt);
          }
        }
      }
      insertWhiteListB.detach();
      extractWhiteListB.detach();
    }

    gui.removeToolTip(insertFilterTooltip);
    gui.removeToolTip(extractFilterTooltip);
  }

  @Override
  @Nonnull
  public ResourceLocation getTexture() {
    return isEnder ? EnderIO.proxy.getGuiTexture("ender_liquid_settings") : super.getTexture();
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    if (isEnder && isFilterVisible()) {

      FontRenderer fr = gui.getFontRenderer();
      int x = left + leftColumn + 10;
      int y = top + 36;
      fr.drawString(filterStrInsert, x, y, ColorUtil.getRGB(Color.DARK_GRAY));

      x = left + rightColumn + 10;
      fr.drawString(filterStrExtract, x, y, ColorUtil.getRGB(Color.DARK_GRAY));

      x = gui.getGuiLeft() + filterInsertX;
      y = gui.getGuiTop() + filterY;
      GL11.glColor3f(1, 1, 1);
      gui.bindGuiTexture();

      FluidFilter filterInsert = eConduit.getFilter(gui.getDir(), false);
      if (filterInsert != null && !filterInsert.isEmpty()) {
        for (int i = 0; i < filterInsert.size(); i++) {
          FluidStack f = filterInsert.getFluidStackAt(i);
          if (f != null) {
            renderFluid(f, x + (i * 18), y);
          }
        }
      }

      x += 101;
      GlStateManager.color(1, 1, 1);
      gui.bindGuiTexture();

      FluidFilter filterExtract = eConduit.getFilter(gui.getDir(), true);
      if (filterExtract != null && !filterExtract.isEmpty()) {
        for (int i = 0; i < filterExtract.size(); i++) {
          FluidStack f = filterExtract.getFluidStackAt(i);
          if (f != null) {
            renderFluid(f, x + (i * 18), y);
          }
        }
      }

    }
  }

  private void renderFluid(FluidStack f, int x, int y) {

    ResourceLocation iconKey = f.getFluid().getStill();
    TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
    if (icon != null) {
      RenderUtil.renderGuiTank(f, 1000, 1000, x + 1, y + 1, 0, 16, 16);
    }

  }

  private boolean isFilterVisible() {
    if (!isEnder) {
      return false;
    }
    ConnectionMode mode = conduit.getConnectionMode(gui.getDir());
    return mode == ConnectionMode.INPUT || mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
  }

  private class FilterToolTip extends GuiToolTip {

    int index;

    public FilterToolTip(@Nonnull Rectangle bounds, int index) {
      super(bounds, (String[]) null);
      this.index = index;
    }

    @Override
    public @Nonnull List<String> getToolTipText() {
      if (!isFilterVisible()) {
        return Collections.emptyList();
      }
      FluidFilter filter = eConduit.getFilter(gui.getDir(), false);
      if (filter == null) {
        return Collections.emptyList();
      }
      if (filter.getFluidStackAt(index) == null) {
        return Collections.emptyList();
      }
      return Collections.singletonList(filter.getFluidStackAt(index).getLocalizedName());
    }

  }

}
