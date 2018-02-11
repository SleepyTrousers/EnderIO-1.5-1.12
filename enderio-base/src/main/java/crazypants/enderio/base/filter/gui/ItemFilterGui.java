package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class ItemFilterGui extends GuiContainerBaseEIO {

  private IItemFilter filter;
  private IItemFilterGui filterGui;

  public ItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull IItemFilter filter) {
    super(new ContainerItemFilter(playerInv, filter), "item_filter");
    this.filter = filter;

    ySize = 206;
    xSize = 206;

  }

  @Override
  public void initGui() {
    super.initGui();
    filterGui = filter.getGui(this, (IItemFilterContainer) inventorySlots, false);
    filterGui.updateButtons();
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);
    if (filterGui != null) {
      filterGui.actionPerformed(button);
    }
  }

  @Override
  public void mouseClicked(int x, int y, int par3) throws IOException {
    super.mouseClicked(x, y, par3);
    if (filterGui != null) {
      filterGui.mouseClicked(x, y, par3);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int x, int y) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    if (filterGui != null) {
      filterGui.renderCustomOptions(y + 13, par1, x, y);
    }
    super.drawGuiContainerBackgroundLayer(par1, x, y);
  }

}
