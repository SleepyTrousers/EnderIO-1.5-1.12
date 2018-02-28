package crazypants.enderio.base.filter.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractGuiItemFilter extends GuiContainerBaseEIO {

  public AbstractGuiItemFilter(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    super(filterContainer, "item_filter");
    // TODO Auto-generated constructor stub
  }

}
