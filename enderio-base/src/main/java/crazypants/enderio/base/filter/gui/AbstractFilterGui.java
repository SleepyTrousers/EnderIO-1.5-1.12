package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.network.ICloseFilterRemoteExec;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.filter.network.PacketHeldFilterUpdate;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.integration.jei.GhostSlotTarget;
import crazypants.enderio.base.integration.jei.IHaveGhostTargets;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractFilterGui extends GuiContainerBaseEIO implements IHaveGhostTargets<AbstractFilterGui>, ICloseFilterRemoteExec.GUI {

  private static final int ID_CLOSE_WINDOW_BUTTON = 12615;

  protected final @Nonnull ContainerFilter filterContainer;
  protected final boolean isStickyModeAvailable;

  private final IconButton closeWindowButton;
  private final @Nonnull IFilter filter;

  public AbstractFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IFilter filter) {
    this(playerInv, filterContainer, te, filter, "item_filter");
  }

  protected AbstractFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IFilter filter,
      @Nonnull String... texture) {
    super(filterContainer, texture);
    this.filterContainer = filterContainer;
    xSize = 189;
    ySize = 207;

    this.filter = filter;

    isStickyModeAvailable = (filterContainer.getFilterIndex() == FilterGuiUtil.INDEX_OUTPUT_ITEM);

    closeWindowButton = new IconButton(this, ID_CLOSE_WINDOW_BUTTON, 3, 3, IconEIO.ARROW_LEFT);
    closeWindowButton.setToolTip(Lang.GUI_ITEM_FILTER_CLOSE.get(), Lang.GUI_ITEM_FILTER_CLOSE_2.get());
  }

  @Override
  public void initGui() {
    super.initGui();

    closeWindowButton.onGuiInit();
    updateButtons();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    fr.drawString(getUnlocalisedNameForHeading(), getGuiLeft() + 28, getGuiTop() + 13, ColorUtil.getRGB(Color.DARK_GRAY));

    renderCustomOptions(getGuiTop() + 13, par1, mouseX, mouseY);
    super.drawGuiContainerBackgroundLayer(par1, mouseX, mouseY);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);

    if (button.id == ID_CLOSE_WINDOW_BUTTON) {
      doCloseFilterGui();
    }
  }

  public void updateButtons() {
  }

  public void renderCustomOptions(int top, float par1, int par2, int par3) {
  }

  @Nonnull
  protected abstract String getUnlocalisedNameForHeading();

  public void sendFilterChange() {
    updateButtons();
    TileEntity te = filterContainer.getTileEntity();
    if (te != null) {
      PacketHandler.INSTANCE.sendToServer(new PacketFilterUpdate(te, getFilter(), filterContainer.getFilterIndex(), filterContainer.getParam1()));
    } else {
      PacketHandler.INSTANCE.sendToServer(new PacketHeldFilterUpdate(getFilter(), filterContainer.getFilterIndex()));
    }
  }

  @Nonnull
  private IFilter getFilter() {
    return filter;
  }

  @Override
  public List<GhostSlotTarget<?>> getGhostTargets() {
    return new ArrayList<>();
  }

}
