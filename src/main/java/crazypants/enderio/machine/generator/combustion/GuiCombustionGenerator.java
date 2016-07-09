package crazypants.enderio.machine.generator.combustion;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiCombustionGenerator extends GuiPoweredMachineBase<TileCombustionGenerator> {

  public GuiCombustionGenerator(InventoryPlayer par1InventoryPlayer, TileCombustionGenerator te) {
    super(te, new ContainerCombustionEngine(par1InventoryPlayer, te), "combustionGen");

    addToolTip(new GuiToolTip(new Rectangle(114, 21, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("combustionGenerator.coolantTank");
        if (getTileEntity().getCoolantTank().getFluid() != null) {
          heading += ": " + getTileEntity().getCoolantTank().getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().getCoolantTank()));
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(48, 21, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("combustionGenerator.fuelTank");
        if (getTileEntity().getFuelTank().getFluid() != null) {
          heading += ": " + getTileEntity().getFuelTank().getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().getFuelTank()));
      }

    });

  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    if (mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 48 - 2;
      int y = 21 - 2;
      int w = 15 + 4;
      int h = 47 + 4;
      renderSlotHighlight(PULL_COLOR, x, y, w, h);
      x = 114 - 2;
      renderSlotHighlight(PULL_COLOR, x, y, w, h);
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    TileCombustionGenerator gen = getTileEntity();

    FontRenderer fr = getFontRenderer();
    int output = 0;
    if (gen.isActive()) {
      output = gen.getGeneratedLastTick();
    }
    String txt = EnderIO.lang.localize("combustionGenerator.output") + " " + PowerDisplayUtil.formatPower(output) + " " + PowerDisplayUtil.abrevation()
        + PowerDisplayUtil.perTickStr();
    int sw = fr.getStringWidth(txt);
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, guiTop + fr.FONT_HEIGHT / 2 + 3, ColorUtil.getRGB(Color.WHITE));

    int x = guiLeft + 48;
    int y = guiTop + 21;
    if (gen.getCoolantTank().getFluidAmount() > 0) {
      x = guiLeft + 114;
      // right tank
      RenderUtil.renderGuiTank(gen.getCoolantTank().getFluid(), 4000, gen.getCoolantTank().getFluidAmount() - 1000, x, y, zLevel, 15, 47);

      if (gen.isActive()) {
        txt = gen.getNumTicksPerMbCoolant() + " " + EnderIO.lang.localize("power.tmb");
        sw = fr.getStringWidth(txt);
        fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));
      }

      x = guiLeft + 72;
      y = guiTop + 14;
      // center coolant chamber
      RenderUtil.renderGuiTank(gen.getCoolantTank().getFluid(), 1000, Math.min(gen.getCoolantTank().getFluidAmount(), 1000), x, y + 14, zLevel, 33, 33);
      // draw some gui over the top again to make the center shape
      bindGuiTexture();
      drawTexturedModalRect(x, y + 14, 0, 223, 33, 33);
      y += 7;
    }

    if (gen.getFuelTank().getFluidAmount() > 0) {
      // left tank
      x = guiLeft + 48;

      RenderUtil.renderGuiTank(gen.getFuelTank().getFluid(), 4000, gen.getFuelTank().getFluidAmount() - 1000, x, y, zLevel, 15, 47);

      if (gen.isActive()) {
        txt = gen.getNumTicksPerMbFuel() + " " + EnderIO.lang.localize("power.tmb");
        sw = fr.getStringWidth(txt);
        fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));
      }

      // center tank
      RenderUtil.renderGuiTank(gen.getFuelTank().getFluid(), 1000, Math.min(gen.getFuelTank().getFluidAmount(), 1000), guiLeft + 81, guiTop + 38, zLevel, 14,
          14);
    }

    bindGuiTexture();
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected int getPowerX() {
    return 10;
  }

  @Override
  protected int getPowerY() {
    return 13;
  }

  @Override
  protected int getPowerHeight() {
    return 60;
  }

}