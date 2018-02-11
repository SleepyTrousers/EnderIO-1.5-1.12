package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class ItemFilterGui extends GuiContainerBaseEIO {

  private IItemFilter filter;
  private InventoryPlayer playerInv;
  private IItemFilterGui filterGui;

  public ItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull IItemFilter filter) {
    super(new ContainerItemFilter(playerInv, filter), "item_filter");
    this.filter = filter;
    this.playerInv = playerInv;

  }

  @Override
  public void initGui() {
    super.initGui();
    filterGui = filter.getGui(this, (IItemFilterContainer) inventorySlots, false);
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
    if (filterGui != null) {
      filterGui.renderCustomOptions(y + 13, par1, x, y);
    }
    super.drawGuiContainerBackgroundLayer(par1, x, y);
  }

}
