package crazypants.enderio.machines.machine.generator.combustion;

import java.awt.Color;
import java.awt.Rectangle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.fluid.SmartTank;

import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;

public class GuiCombustionGenerator<T extends TileCombustionGenerator> extends GuiInventoryMachineBase<T> {

  private static final @Nonnull Rectangle RECTANGLE_FUEL_TANK = new Rectangle(48, 21, 15, 47);
  private static final @Nonnull Rectangle RECTANGLE_COOLANT_TANK = new Rectangle(114, 21, 15, 47);

  public GuiCombustionGenerator(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull T te) {
    super(te, new ContainerCombustionGenerator<T>(par1InventoryPlayer, te), "combustion_gen");

    addToolTip(new GuiToolTip(RECTANGLE_COOLANT_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        final FluidStack coolant = getTileEntity().getCoolantTank().getFluid();
        if (coolant != null) {
          text.add(Lang.GUI_COMBGEN_CTANK.get(coolant.getLocalizedName()));
        } else {
          text.add(Lang.GUI_COMBGEN_CTANK_EMPTY.get());
        }
        text.add(LangFluid.MB(getTileEntity().getCoolantTank()));
      }

    });

    addToolTip(new GuiToolTip(RECTANGLE_FUEL_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        final FluidStack fuel = getTileEntity().getFuelTank().getFluid();
        if (fuel != null) {
          text.add(Lang.GUI_COMBGEN_FTANK.get(fuel.getLocalizedName()));
        } else {
          text.add(Lang.GUI_COMBGEN_FTANK_EMPTY.get());
        }
        text.add(LangFluid.MB(getTileEntity().getFuelTank()));
      }

    });

    addDrawingElement(new PowerBar<TileCombustionGenerator>(te, this, 15, 14, 42) {
      @Override
      protected String getPowerOutputLabel(@Nonnull String rft) {
        return super.getPowerOutputLabel(LangPower.RFt(getTileEntity().getMath().getEnergyPerTick()));
      }
    });
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_COOLANT_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().getCoolantTank().getFluid();
    }
    if (RECTANGLE_FUEL_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().getFuelTank().getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  public void renderSlotHighlights(@Nonnull IoMode mode) {
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

  private final int CENTER = 1000;

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    TileCombustionGenerator gen = getTileEntity();
    CombustionMath math = gen.getMath();

    FontRenderer fr = getFontRenderer();
    int output = 0;
    if (gen.isActive()) {
      output = math.getEnergyPerTick();
    }
    String txt = Lang.GUI_COMBGEN_OUTPUT.get(LangPower.RFt(output));
    int sw = fr.getStringWidth(txt);
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, guiTop + fr.FONT_HEIGHT / 2 + 3, ColorUtil.getRGB(Color.WHITE));

    int x = guiLeft + 48;
    int y = guiTop + 21;
    final SmartTank coolantTank = gen.getCoolantTank();
    if (!coolantTank.isEmpty()) {
      x = guiLeft + 114;
      // right tank
      final int fluidAmount = coolantTank.getFluidAmount();
      final FluidStack fluid = coolantTank.getFluid();
      if (fluidAmount > CENTER) {
        RenderUtil.renderGuiTank(fluid, coolantTank.getCapacity() - CENTER, fluidAmount - CENTER, x, y, zLevel, 15, 47);
      }

      if (gen.isActive()) {
        txt = LangFluid.tMB(math.getTicksPerCoolant());
        sw = fr.getStringWidth(txt);
        fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));
      }

      // center coolant chamber
      RenderUtil.renderGuiTank(fluid, CENTER, Math.min(fluidAmount, CENTER), guiLeft + 72, guiTop + 28, zLevel, 33, 33);
      // draw some gui over the top again to make the center shape
      bindGuiTexture();
      drawTexturedModalRect(guiLeft + 72, guiTop + 28, 0, 223, 33, 33);
    }

    final SmartTank fuelTank = gen.getFuelTank();
    if (!fuelTank.isEmpty()) {
      // left tank
      x = guiLeft + 48;

      final int fluidAmount = fuelTank.getFluidAmount();
      final FluidStack fluid = fuelTank.getFluid();
      if (fluidAmount > CENTER) {
        RenderUtil.renderGuiTank(fluid, fuelTank.getCapacity() - CENTER, fluidAmount - CENTER, x, y, zLevel, 15, 47);
      }

      if (gen.isActive()) {
        txt = LangFluid.tMB(math.getTicksPerFuel());
        sw = fr.getStringWidth(txt);
        fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));
      }

      // center tank
      RenderUtil.renderGuiTank(fluid, CENTER, Math.min(fluidAmount, CENTER), guiLeft + 81, guiTop + 38, zLevel, 14, 14);
    }

    bindGuiTexture();
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}