package crazypants.enderio.machine.monitor.v2;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiPMon extends GuiPoweredMachineBase<TilePMon> {

  protected int timebase = 2;
  protected int timebaseOffset = 0;
  protected InvisibleButton plus;
  protected InvisibleButton minus;

  public GuiPMon(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TilePMon te) {
    super(te, new ContainerPMon(par1InventoryPlayer, te), "pmon");

    plus = new InvisibleButton(this, 1, 154, 28);
    plus.setToolTip("+");
    minus = new InvisibleButton(this, 2, 154, 52);
    minus.setToolTip("-");
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.visible = false;
    configB.visible = false;
    plus.onGuiInit();
    minus.onGuiInit();
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    if (btn.id == 1) {
      if (timebase >= 6) {
        return;
      }
      timebase++;
      timebaseOffset -= 16;
    } else if (btn.id == 2) {
      if (timebase <= 0) {
        return;
      }
      timebase--;
      timebaseOffset += 16;
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected int getPowerX() {
    return 8;
  }

  @Override
  protected int getPowerY() {
    return 10;
  }

  @Override
  protected int getPowerWidth() {
    return 4;
  }

  @Override
  protected int getPowerHeight() {
    return 66;
  }

  private long lastTick = 0;

  private void drawTimebase(int x, int y) {
    int u = 200, v = timebase * 16 + timebaseOffset, w = 18, h = 16;
    if (v < 0) {
      v = 0;
    } else if (v > 6 * 16) {
      v = 6 * 16;
    }
    drawTexturedModalRect(x, y, u, v, w, h);
    if (lastTick != EnderIO.proxy.getTickCount()) {
      lastTick = EnderIO.proxy.getTickCount();
      if (timebaseOffset < 0) {
        timebaseOffset += 1 - timebaseOffset / 8;
      } else if (timebaseOffset > 0) {
        timebaseOffset -= 1 + timebaseOffset / 8;
      }
    }
  }

  private void drawGraph(int x, int y) {
    StatCollector stat = getTileEntity().getStatCollector(timebase);
    int[][] values = stat.getValues();
    for (int i = 0; i < stat.MAX_VALUES; i++) {
      int min = values[0][i], max = values[1][i];
      drawTexturedModalRect(x + i, y + 63 - max, 220, 63 - max, 1, max - min + 1);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    drawTimebase(sx + 149, sy + 35);
    drawGraph(sx + 48, sy + 11);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

  }

}
