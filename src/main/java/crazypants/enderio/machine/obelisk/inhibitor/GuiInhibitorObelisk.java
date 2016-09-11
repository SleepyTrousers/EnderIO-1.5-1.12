package crazypants.enderio.machine.obelisk.inhibitor;

import java.awt.Color;

import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiInhibitorObelisk extends GuiPoweredMachineBase<TileInhibitorObelisk> {

  public GuiInhibitorObelisk(TileInhibitorObelisk machine, Container container) {
    super(machine, container, "inhibitor");
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    int range = (int) getTileEntity().getRange();
    drawCenteredString(fontRendererObj, EnderIO.lang.localize("gui.spawnGurad.range") + " " + range, getGuiLeft() + sx / 2 + 9, getGuiTop() + 68,
        ColorUtil.getRGB(Color.white));
  }
}
