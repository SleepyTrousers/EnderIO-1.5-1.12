package crazypants.enderio.machine.farm;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;

public class GuiFarmStation extends GuiMachineBase {

  public GuiFarmStation(InventoryPlayer par1InventoryPlayer, TileFarmStation machine) {
    super(machine, new FarmStationContainer(par1InventoryPlayer, machine));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/farmStation.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }
  @Override
  protected int getPowerHeight() {
    return 57;
  }


}
