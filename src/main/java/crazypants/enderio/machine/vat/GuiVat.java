package crazypants.enderio.machine.vat;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

@SideOnly(Side.CLIENT)
public class GuiVat extends GuiPoweredMachineBase<TileVat> {

  private static final String GUI_TEXTURE = "enderio:textures/gui/vat.png";

  private final IconButtonEIO dump1, dump2;

  public GuiVat(InventoryPlayer inventory, TileVat te) {
    super(te, new ContainerVat(inventory, te));

    addToolTip(new GuiToolTip(new Rectangle(30, 12, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("vat.inputTank");
        if(getTileEntity().inputTank.getFluid() != null) {
          heading += ": " + getTileEntity().inputTank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().inputTank));
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(132, 12, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("vat.outputTank");
        if(getTileEntity().outputTank.getFluid() != null) {
          heading += ": " + getTileEntity().outputTank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().outputTank));
      }

    });

    dump1 = new IconButtonEIO(this, 1, 29, 62, IconEIO.REDSTONE_MODE_NEVER);
    dump1.setToolTip(Lang.localize("gui.machine.vat.dump.1"));
    dump2 = new IconButtonEIO(this, 2, 131, 62, IconEIO.REDSTONE_MODE_NEVER);
    dump2.setToolTip(Lang.localize("gui.machine.vat.dump.2"));

    addProgressTooltip(81, 63, 14, 14);
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
    if(mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      renderSlotHighlight(PULL_COLOR, x - 2, y - 2, 15 + 4, 47 + 4);
    }
    if(mode == IoMode.PUSH || mode == IoMode.PUSH_PULL) {
      x = 132;
      renderSlotHighlight(PUSH_COLOR, x - 2, y - 2, 15 + 4, 47 + 4);
    }
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(GUI_TEXTURE);
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    TileVat vat = getTileEntity();

    if(shouldRenderProgress()) {
      int scaled = vat.getProgressScaled(14) + 1;
      drawTexturedModalRect(guiLeft + 81, guiTop + 77 - scaled, 176, 14 - scaled, 14, scaled);

      IIcon inputIcon = null;
      if(vat.currentTaskInputFluid != null) {
        inputIcon = vat.currentTaskInputFluid.getStillIcon();
      }
      IIcon outputIcon = null;
      if(vat.currentTaskOutputFluid != null) {
        outputIcon = vat.currentTaskOutputFluid.getStillIcon();
      }

      if(inputIcon != null && outputIcon != null) {
        renderVat(inputIcon, outputIcon, vat.getProgress());
      }

    }

    int x = guiLeft + 30;
    int y = guiTop + 12;
    RenderUtil.renderGuiTank(vat.inputTank, x, y, zLevel, 15, 47);
    x = guiLeft + 132;
    RenderUtil.renderGuiTank(vat.outputTank, x, y, zLevel, 15, 47);

    if(vat.currentTaskOutputFluid != null || vat.outputTank.getFluidAmount() > 0) {

      Fluid outputFluid;
      if(vat.outputTank.getFluidAmount() > 0) {
        outputFluid = vat.outputTank.getFluid().getFluid();
      } else {
        outputFluid = vat.currentTaskOutputFluid;
      }

      float mult;
      ItemStack inStack = vat.getStackInSlot(0);
      if(inStack != null) {
        mult = VatRecipeManager.instance.getMultiplierForInput(inStack, outputFluid);
        String str = "x" + mult;
        x = guiLeft + 63 - fontRendererObj.getStringWidth(str) / 2;
        fontRendererObj.drawString(str, x, guiTop + 32, ColorUtil.getRGB(Color.gray), false);
      }
      inStack = vat.getStackInSlot(1);
      if(inStack != null) {
        mult = VatRecipeManager.instance.getMultiplierForInput(inStack, outputFluid);
        String str = "x" + mult;
        x = guiLeft + 113 - fontRendererObj.getStringWidth(str) / 2;
        fontRendererObj.drawString(str, x, guiTop + 32, ColorUtil.getRGB(Color.gray), false);
      }
    }

    RenderUtil.bindTexture(GUI_TEXTURE);
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  private void renderVat(IIcon inputIcon, IIcon outputIcon, float progress) {
    RenderUtil.bindBlockTexture();

    int x = guiLeft + 76;
    int y = guiTop + 34;

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glColor4f(1, 1, 1, 0.75f * (1f - progress));
    drawTexturedModelRectFromIcon(x, y, inputIcon, 26, 28);

    GL11.glColor4f(1, 1, 1, 0.75f * progress);
    drawTexturedModelRectFromIcon(x, y, outputIcon, 26, 28);

    GL11.glDisable(GL11.GL_BLEND);

    GL11.glColor4f(1, 1, 1, 1);
    RenderUtil.bindTexture(GUI_TEXTURE);
    drawTexturedModalRect(x, y, 0, 256 - 28, 26, 28);
  }

  @Override
  protected void actionPerformed(GuiButton b) {
    super.actionPerformed(b);

    if(b == dump1) {
      dump(1);
    } else if(b == dump2) {
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
