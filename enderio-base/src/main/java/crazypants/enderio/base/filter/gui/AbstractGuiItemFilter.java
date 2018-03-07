package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.base.filter.network.ICloseFilterRemoteExec;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractGuiItemFilter extends GuiContainerBaseEIO implements ICloseFilterRemoteExec.GUI {

  private static final int ID_CLOSE_WINDOW_BUTTON = 12615;

  protected final @Nonnull ContainerFilter filterContainer;
  protected final boolean isStickyModeAvailable;

  private final IconButton closeWindowButton;

  public AbstractGuiItemFilter(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    this(playerInv, filterContainer, te, "item_filter");
  }

  protected AbstractGuiItemFilter(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull String... texture) {
    super(filterContainer, texture);
    this.filterContainer = filterContainer;
    xSize = 189;
    ySize = 207;

    isStickyModeAvailable = (filterContainer.filterIndex == FilterGuiUtil.INDEX_OUTPUT);

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

}
