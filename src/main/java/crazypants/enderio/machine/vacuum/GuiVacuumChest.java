package crazypants.enderio.machine.vacuum;

import org.lwjgl.opengl.GL11;

import crazypants.render.RenderUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiVacuumChest extends GuiContainer {

  public GuiVacuumChest(EntityPlayer player, InventoryPlayer inventory, TileVacuumChest te) {
    super(new ContainerVacuumChest(player, inventory, te));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/vacumChest.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);    
  }

}
