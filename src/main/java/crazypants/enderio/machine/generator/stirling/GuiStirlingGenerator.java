package crazypants.enderio.machine.generator.stirling;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

@SideOnly(Side.CLIENT)
public class GuiStirlingGenerator extends GuiPoweredMachineBase<TileEntityStirlingGenerator> {

  public GuiStirlingGenerator(InventoryPlayer par1InventoryPlayer, TileEntityStirlingGenerator te) {
    super(te, new StirlingGeneratorContainer(par1InventoryPlayer, te));
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/stirlingGenerator.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    int scaled;

    if(getTileEntity().getProgress() < 1 && getTileEntity().getProgress() > 0) {
      scaled = getTileEntity().getProgressScaled(12);
      drawTexturedModalRect(sx + 80, sy + 64 - scaled, 176, 12 - scaled, 14, scaled + 2);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    FontRenderer fr = getFontRenderer();
    int y = guiTop + fr.FONT_HEIGHT / 2 + 3;

    int output = 0;
    if(getTileEntity().isActive()) {
      output = getTileEntity().getPowerUsePerTick();
    }
    String txt = Lang.localize("stirlingGenerator.output") + " " + PowerDisplayUtil.formatPower(output) + " " + PowerDisplayUtil.abrevation()
        + PowerDisplayUtil.perTickStr();
    int sw = fr.getStringWidth(txt);
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, y, ColorUtil.getRGB(Color.WHITE));

    txt = Lang.localize("stirlingGenerator.burnRate") + " " + (getTileEntity().getBurnTimeMultiplier()) + "x";
    sw = fr.getStringWidth(txt);
    y += fr.FONT_HEIGHT + 3;
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, y, ColorUtil.getRGB(Color.WHITE));

  }
}
