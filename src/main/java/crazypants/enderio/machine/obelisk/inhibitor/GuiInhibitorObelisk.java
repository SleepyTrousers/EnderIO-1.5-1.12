package crazypants.enderio.machine.obelisk.inhibitor;

import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.ranged.GuiRangedMachine;

public class GuiInhibitorObelisk extends GuiRangedMachine<TileInhibitorObelisk> {
  
  private long tick, lastUpdate;

  public GuiInhibitorObelisk(TileInhibitorObelisk machine, Container container) {
    super(machine, container);
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/inhibitorObelisk.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    if (getTileEntity().isActive()) {
      tick = getTileEntity().getWorld().getTotalWorldTime();
      int animStage = (int) ((tick - lastUpdate) / 4);
      if (animStage > 5) {
        lastUpdate = tick;
      } else if (animStage > 0) {
        if (animStage >= 1) {
          drawTexturedModalRect(sx + 79, sy + 34, 83, 205, 15, 14);
        }
        if (animStage >= 2) {
          drawTexturedModalRect(sx + 71, sy + 26, 101, 197, 31, 30);
        }
        if (animStage >= 3) {
          drawTexturedModalRect(sx + 64, sy + 19, 135, 189, 44, 44);
        }
        if (animStage >= 4) {
          drawTexturedModalRect(sx + 58, sy + 13, 184, 182, 56, 56);
        }
      }
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
  
  @Override
  protected boolean showRecipeButton() {
    return false;
  }
}
