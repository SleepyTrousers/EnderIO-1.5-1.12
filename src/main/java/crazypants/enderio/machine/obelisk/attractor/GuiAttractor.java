package crazypants.enderio.machine.obelisk.attractor;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.ranged.GuiRangedMachine;

public class GuiAttractor extends GuiRangedMachine<TileAttractor> {
  
  public GuiAttractor(InventoryPlayer par1InventoryPlayer, TileAttractor te) {
    super(te, new ContainerAttractor(par1InventoryPlayer, te));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/attractor.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
 }

  @Override
  protected boolean showRecipeButton() {    
    return false;
  }
}
