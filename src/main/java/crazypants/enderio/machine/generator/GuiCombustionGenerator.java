package crazypants.enderio.machine.generator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;

public class GuiCombustionGenerator extends GuiMachineBase {

  private TileCombustionGenerator entity;

  public GuiCombustionGenerator(InventoryPlayer par1InventoryPlayer, TileCombustionGenerator te) {
    super(te, new EmptyContainer());
    this.entity = te;
  }


  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/combustionGen.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    int scaled;

    if(entity.getProgress() < 1 && entity.getProgress() > 0) {
      scaled = entity.getProgressScaled(12);
      drawTexturedModalRect(sx + 80, sy + 65 - scaled, 176, 12 - scaled, 14, scaled + 2);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
  
  
  public static class EmptyContainer extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer var1) {
      return true;
    }
    
  }
  
}