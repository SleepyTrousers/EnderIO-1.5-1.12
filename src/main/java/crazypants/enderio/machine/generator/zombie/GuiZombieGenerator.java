package crazypants.enderio.machine.generator.zombie;

import java.awt.Color;
import java.awt.Rectangle;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiZombieGenerator extends GuiPoweredMachineBase<TileZombieGenerator> {

  private static final Rectangle RECTANGLE_FUEL_TANK = new Rectangle(80, 21, 15, 47);

  public GuiZombieGenerator(InventoryPlayer inventory, final TileZombieGenerator tileEntity) {
    super(tileEntity, new ContainerZombieGenerator(inventory, tileEntity), "zombieGenerator");
    
    addToolTip(new GuiToolTip(RECTANGLE_FUEL_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("zombieGenerator.fuelTank");
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().tank));
        if(tileEntity.tank.getFluidAmount() < tileEntity.getActivationAmount()) {
          text.add(EnderIO.lang.localize("gui.fluid.minReq", tileEntity.getActivationAmount() + Fluids.MB()));
        }
      }
    });

  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_FUEL_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().tank.getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    if(mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 78;
      int y = 19;
      int w = 15 + 4;
      int h = 47 + 4;
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
    TileZombieGenerator gen = getTileEntity();

    FontRenderer fr = getFontRenderer();
    int output = 0;
    if(gen.isActive()) {
      output = gen.outputPerTick;
    }
    String txt = EnderIO.lang.localize("combustionGenerator.output") + " " + PowerDisplayUtil.formatPower(output) + " " + PowerDisplayUtil.abrevation()
        + PowerDisplayUtil.perTickStr();
    int sw = fr.getStringWidth(txt);
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, guiTop + fr.FONT_HEIGHT / 2 + 3, ColorUtil.getRGB(Color.WHITE));

    int x = guiLeft + 80;
    int y = guiTop + 21;
    if(gen.tank.getFluidAmount() > 0) {

      RenderUtil.renderGuiTank(gen.tank.getFluid(), gen.tank.getCapacity(), gen.tank.getFluidAmount(), x, y, zLevel, 16, 47);

      if(gen.isActive()) {
        txt = gen.tickPerBucketOfFuel / 1000 + " " + EnderIO.lang.localize("power.tmb");
        sw = fr.getStringWidth(txt);
        fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 46, ColorUtil.getRGB(Color.WHITE));
      }
    }

    bindGuiTexture();
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected int getPowerX() {
    return 15;
  }

  @Override
  protected int getPowerY() {
    return 20;
  }

  @Override
  protected int getPowerHeight() {
    return 48;
  }

}
