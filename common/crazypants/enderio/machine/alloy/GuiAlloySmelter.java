package crazypants.enderio.machine.alloy;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;


public class GuiAlloySmelter extends GuiMachineBase {

  private TileAlloySmelter tileEntity;

  public GuiAlloySmelter(InventoryPlayer par1InventoryPlayer, TileAlloySmelter furnaceInventory) {
    super(furnaceInventory, new ContainerAlloySmelter(par1InventoryPlayer, furnaceInventory));
    this.tileEntity = furnaceInventory;
  }


  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);    
    RenderUtil.bindTexture("enderio:textures/gui/alloySmelter.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    
    int scaled;    
    
    if(tileEntity.getProgress() < 1 && tileEntity.getProgress() > 0) {
      scaled = tileEntity.getProgressScaled(12);
      drawTexturedModalRect(sx + 55, sy + 48 - scaled, 176, 12 - scaled, 14, scaled + 2);      
      drawTexturedModalRect(sx + 103, sy + 48 - scaled, 176, 12 - scaled, 14, scaled + 2);
    }
    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
}
