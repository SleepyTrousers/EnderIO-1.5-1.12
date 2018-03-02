package crazypants.enderio.base.filter.gui;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractGuiItemFilter extends GuiContainerBaseEIO implements IItemFilterGui {

  public AbstractGuiItemFilter(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    this(playerInv, filterContainer, te, "item_filter");
  }

  protected AbstractGuiItemFilter(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull String... texture) {
    super(filterContainer, texture);
    xSize = 182;
    ySize = 200;
  }

  @Override
  public void initGui() {
    super.initGui();
    updateButtons();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    renderCustomOptions(getGuiTop() + 13, par1, mouseX, mouseY);
    super.drawGuiContainerBackgroundLayer(par1, mouseX, mouseY);
  }

  @Override
  public void updateButtons() {
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
  }

}
