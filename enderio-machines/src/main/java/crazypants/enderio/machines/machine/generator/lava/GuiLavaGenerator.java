package crazypants.enderio.machines.machine.generator.lava;

import java.awt.Rectangle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.lang.LangTemperature;
import crazypants.enderio.base.machine.gui.GenericBar;
import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

import static crazypants.enderio.base.lang.Lang.GUI_GENERIC_EFFICIENCY;
import static crazypants.enderio.machines.lang.Lang.GUI_LAVAGEN_HEAT;

public class GuiLavaGenerator extends GuiCapMachineBase<TileLavaGenerator> {

  private static final int POWERX = 12;
  private static final int POWERY = 14;
  private static final int POWER_HEIGHT = 42;

  private static final @Nonnull Rectangle RECTANGLE_TANK = new Rectangle(70, 21, 16, 47);
  private static final @Nonnull Rectangle RECTANGLE_HEAT = new Rectangle(91, 21, 16, 47);

  public GuiLavaGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull TileLavaGenerator te) {
    super(te, new ContainerLavaGenerator<>(playerInv, te), "lava_generator");
    addDrawingElement(new PowerBar(te.getEnergy(), this, POWERX + 4, POWERY, POWER_HEIGHT));
    addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.GUI_TANK_TANK_TANK_TANK.get());
        text.add(LangFluid.MB(getTileEntity().tank));
      }

    });

    addDrawingElement(new GenericBar(this, RECTANGLE_HEAT, -1, new GuiToolTip(RECTANGLE_HEAT, "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(GUI_LAVAGEN_HEAT.get());
        text.add(LangTemperature.degK(getTileEntity().getHeatDisplayValue())); // #.# °C
        text.add(GUI_GENERIC_EFFICIENCY.get((int) (getTileEntity().getHeatFactor() * 100))); // ##% efficiency
        text.add(LangPower.RFt(getTileEntity().getPowerGenPerTick())); // # µI/t
      }

    }) {

      @Override
      protected float getLevel() {
        return getTileEntity().getHeat();
      }

      @Override
      protected int getColor() {
        return getLevel() < .5 ? 0xB02ECB19 : getLevel() < .75 ? 0xB0FFD21F : 0xB0F21818;
      }

    });
  }

  @Override
  protected boolean showRecipeButton() {
    return false; // TODO JEI recipe
  }

  @Override
  public void initGui() {
    super.initGui();
    final NNList<GhostSlot> ghostSlots = getGhostSlotHandler().getGhostSlots();
    ghostSlots.clear();
    ((ContainerLavaGenerator<?>) inventorySlots).createGhostSlots(ghostSlots);
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().tank.getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    RenderUtil.renderGuiTank(getTileEntity().tank, guiLeft + RECTANGLE_TANK.x, guiTop + RECTANGLE_TANK.y, zLevel, RECTANGLE_TANK.width, RECTANGLE_TANK.height);
  }

}
