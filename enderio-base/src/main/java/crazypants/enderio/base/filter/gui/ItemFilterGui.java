package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class ItemFilterGui extends GuiContainerBaseEIO {

  private static final @Nonnull String FILTER_HEADER = "Filter";

  private static final int EXIT_LEFT = 13;
  private static final int EXIT_TOP = 20;
  private static final int FILTER_LEFT_OFFSET = 32;
  private static final int FILTER_TOP_OFFSET = 20;
  private static final int HEADER_LEFT = 7;
  private static final int HEADER_TOP = 6;

  private static final int ID_EXIT = FilterGuiUtil.nextButtonId();

  private final @Nonnull IconButton exitButton;

  private IItemFilter filter;
  private IItemFilterGui filterGui;

  public ItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull IItemFilter filter) {
    super(new ContainerItemFilter(playerInv, filter), "item_filter");
    this.filter = filter;

    ySize = 158;
    xSize = 176;

    int x = EXIT_LEFT;
    int y = EXIT_TOP;

    exitButton = new IconButton(this, ID_EXIT, x, y, IconEIO.DISABLED);
  }

  @Override
  public void initGui() {
    super.initGui();
    exitButton.onGuiInit();
    filterGui = filter.getGui(this, (IItemFilterContainer) inventorySlots, false, FILTER_LEFT_OFFSET, FILTER_TOP_OFFSET);
    filterGui.updateButtons();
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);
    if (button.id == ID_EXIT) {
      Minecraft.getMinecraft().player.closeScreen();
    }
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
      filterGui.renderCustomOptions(sy, par1, x, y);
    }

    FontRenderer fr = getFontRenderer();
    int headerColor = 0x404040;
    fr.drawString(FILTER_HEADER, sx + HEADER_LEFT, sy + HEADER_TOP, headerColor);

    super.drawGuiContainerBackgroundLayer(par1, x, y);
  }

}
