package crazypants.enderio.machine.spawner;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;

public class GuiPoweredSpawner extends GuiMachineBase {

  public GuiPoweredSpawner(InventoryPlayer par1InventoryPlayer, TilePoweredSpawner te) {
    super(te, new ContainerPoweredSpawner(par1InventoryPlayer, te));
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/poweredSpawner.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
