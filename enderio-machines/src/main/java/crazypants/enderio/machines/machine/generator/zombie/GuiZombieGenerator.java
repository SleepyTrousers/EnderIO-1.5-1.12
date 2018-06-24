package crazypants.enderio.machines.machine.generator.zombie;

import java.awt.Color;
import java.awt.Rectangle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiZombieGenerator extends GuiInventoryMachineBase<TileZombieGenerator> {

  private static final @Nonnull Rectangle RECTANGLE_FUEL_TANK = new Rectangle(80, 21, 15, 47);

  public GuiZombieGenerator(@Nonnull InventoryPlayer inventory, @Nonnull final TileZombieGenerator tileEntity) {
    super(tileEntity, new ContainerZombieGenerator(inventory, tileEntity), "zombie_generator");

    addToolTip(new GuiToolTip(RECTANGLE_FUEL_TANK, "") {
      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.GUI_ZOMBGEN_FTANK.get());
        text.add(LangFluid.MB(getTileEntity().tank));
        if (tileEntity.tank.getFluidAmount() < tileEntity.getActivationAmount()) {
          text.add(Lang.GUI_ZOMBGEN_MINREQ.get(LangFluid.MB(tileEntity.getActivationAmount())));
        }
      }
    });

    addDrawingElement(new PowerBar(tileEntity, this));
  }

  @Override
  public @Nullable Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_FUEL_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().tank.getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  public void renderSlotHighlights(@Nonnull IoMode mode) {
    super.renderSlotHighlights(mode);

    if (mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 78;
      int y = 19;
      int w = 15 + 4;
      int h = 47 + 4;
      renderSlotHighlight(PULL_COLOR, x, y, w, h);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    TileZombieGenerator gen = getTileEntity();

    FontRenderer fr = getFontRenderer();
    int output = 0;
    if (gen.isActive()) {
      output = gen.getPowerUsePerTick();
    }
    String txt = Lang.GUI_ZOMBGEN_OUTPUT.get(LangPower.RFt(output));
    int sw = fr.getStringWidth(txt);
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, guiTop + fr.FONT_HEIGHT / 2 + 3, ColorUtil.getRGB(Color.WHITE));

    int x = guiLeft + 80;
    int y = guiTop + 21;
    if (!gen.tank.isEmpty()) {

      RenderUtil.renderGuiTank(gen.tank.getFluid(), gen.tank.getCapacity(), gen.tank.getFluidAmount(), x, y, zLevel, 16, 47);

      if (gen.isActive()) {
        txt = LangFluid.tMB(gen.getTicksPerBucketOfFuel() / 1000);
        sw = fr.getStringWidth(txt);
        fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 46, ColorUtil.getRGB(Color.WHITE));
      }
    }

    bindGuiTexture();
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
