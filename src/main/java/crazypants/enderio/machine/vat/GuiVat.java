package crazypants.enderio.machine.vat;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.recipe.vat.VatRecipeManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class GuiVat extends GuiPoweredMachineBase<TileVat> {

  private static final Rectangle RECTANGLE_OUTPUT_TANK = new Rectangle(132, 12, 15, 47);

  private static final Rectangle RECTANGLE_INPUT_TANK = new Rectangle(30, 12, 15, 47);

  private static final String GUI_TEXTURE = "vat";

  private final IconButton dump1, dump2;

  public GuiVat(InventoryPlayer inventory, TileVat te) {
    super(te, new ContainerVat(inventory, te), GUI_TEXTURE);

    addToolTip(new GuiToolTip(RECTANGLE_INPUT_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("vat.inputTank");
        if (getTileEntity().inputTank.getFluid() != null) {
          heading += ": " + getTileEntity().inputTank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().inputTank));
      }

    });

    addToolTip(new GuiToolTip(RECTANGLE_OUTPUT_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("vat.outputTank");
        if (getTileEntity().outputTank.getFluid() != null) {
          heading += ": " + getTileEntity().outputTank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().outputTank));
      }

    });

    dump1 = new IconButton(this, 1, 29, 62, IconEIO.DUMP_LIQUID);
    dump1.setToolTip(EnderIO.lang.localize("gui.machine.vat.dump.1"));
    dump2 = new IconButton(this, 2, 131, 62, IconEIO.VOID_LIQUID);
    dump2.setToolTip(EnderIO.lang.localize("gui.machine.vat.dump.2"));

    addProgressTooltip(81, 63, 14, 14);
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_INPUT_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().inputTank.getFluid();
    }
    if (RECTANGLE_OUTPUT_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().outputTank.getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  public void initGui() {
    super.initGui();
    dump1.onGuiInit();
    dump2.onGuiInit();
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    int x = 30;
    int y = 12;
    if (mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      renderSlotHighlight(PULL_COLOR, x - 2, y - 2, 15 + 4, 47 + 4);
    }
    if (mode == IoMode.PUSH || mode == IoMode.PUSH_PULL) {
      x = 132;
      renderSlotHighlight(PUSH_COLOR, x - 2, y - 2, 15 + 4, 47 + 4);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    TileVat vat = getTileEntity();

    if (shouldRenderProgress()) {
      int scaled = getProgressScaled(14) + 1;
      drawTexturedModalRect(guiLeft + 81, guiTop + 77 - scaled, 176, 14 - scaled, 14, scaled);

      TextureAtlasSprite inputIcon = null;
      if (vat.currentTaskInputFluid != null) {
        inputIcon = RenderUtil.getStillTexture(vat.currentTaskInputFluid);
      }
      TextureAtlasSprite outputIcon = null;
      if (vat.currentTaskOutputFluid != null) {
        outputIcon = RenderUtil.getStillTexture(vat.currentTaskOutputFluid);
      }

      if (inputIcon != null && outputIcon != null) {
        renderVat(inputIcon, outputIcon, vat.getProgress());
      }

    }

    int x = guiLeft + 30;
    int y = guiTop + 12;
    RenderUtil.renderGuiTank(vat.inputTank, x, y, zLevel, 15, 47);
    x = guiLeft + 132;
    RenderUtil.renderGuiTank(vat.outputTank, x, y, zLevel, 15, 47);

    Fluid outputFluid;
    if (vat.outputTank.getFluidAmount() > 0) {
      outputFluid = vat.outputTank.getFluid().getFluid();
    } else {
      outputFluid = vat.currentTaskOutputFluid;
    }

    Fluid inputFluid;
    if (vat.inputTank.getFluidAmount() > 0) {
      inputFluid = vat.inputTank.getFluid().getFluid();
    } else {
      inputFluid = vat.currentTaskInputFluid;
    }

    float mult;
    ItemStack inStack = vat.getStackInSlot(0);
    if (!inStack.isEmpty()) {
      mult = VatRecipeManager.getInstance().getMultiplierForInput(inputFluid, inStack, outputFluid);
      if (mult > 0) {
        String str = "x" + mult;
        x = guiLeft + 63 - fontRenderer.getStringWidth(str) / 2;
        fontRenderer.drawString(str, x, guiTop + 32, ColorUtil.getRGB(Color.gray), false);
      }
    }
    inStack = vat.getStackInSlot(1);
    if (!inStack.isEmpty()) {
      mult = VatRecipeManager.getInstance().getMultiplierForInput(inputFluid, inStack, outputFluid);
      if (mult > 0) {
        String str = "x" + mult;
        x = guiLeft + 113 - fontRenderer.getStringWidth(str) / 2;
        fontRenderer.drawString(str, x, guiTop + 32, ColorUtil.getRGB(Color.gray), false);
      }
    }

    bindGuiTexture();
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  private void renderVat(TextureAtlasSprite inputIcon, TextureAtlasSprite outputIcon, float progress) {
    RenderUtil.bindBlockTexture();

    int x = guiLeft + 76;
    int y = guiTop + 34;

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glColor4f(1, 1, 1, 0.75f * (1f - progress));
    drawTexturedModalRect(x, y, inputIcon, 26, 28);

    GL11.glColor4f(1, 1, 1, 0.75f * progress);
    drawTexturedModalRect(x, y, outputIcon, 26, 28);

    GL11.glDisable(GL11.GL_BLEND);

    GL11.glColor4f(1, 1, 1, 1);
    bindGuiTexture();
    drawTexturedModalRect(x, y, 0, 256 - 28, 26, 28);
  }

  @Override
  protected void actionPerformed(GuiButton b) throws IOException {
    super.actionPerformed(b);

    if (b == dump1) {
      dump(1);
    } else if (b == dump2) {
      dump(2);
    }
  }

  private void dump(int i) {
    PacketHandler.INSTANCE.sendToServer(new PacketDumpTank(getTileEntity(), i));
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
