package crazypants.enderio.machine.generator.stirling;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.text.MessageFormat;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.power.Capacitors;

@SideOnly(Side.CLIENT)
public class GuiStirlingGenerator extends GuiPoweredMachineBase<TileEntityStirlingGenerator> {

  public GuiStirlingGenerator(InventoryPlayer par1InventoryPlayer, TileEntityStirlingGenerator te) {
    super(te, new StirlingGeneratorContainer(par1InventoryPlayer, te), "stirlingGenerator");

    final StirlingGeneratorContainer c = (StirlingGeneratorContainer)inventorySlots;
    Rectangle r = new Rectangle(c.getUpgradeOffset(), new Dimension(16, 16));
    MessageFormat fmt = new MessageFormat(EnderIO.lang.localize("stirlingGenerator.upgrades"));
    ttMan.addToolTip(new GuiToolTip(r,
            EnderIO.lang.localize("stirlingGenerator.upgradeslot"),
            formatUpgrade(fmt, Capacitors.ACTIVATED_CAPACITOR),
            formatUpgrade(fmt, Capacitors.ENDER_CAPACITOR)) {
      @Override
      public boolean shouldDraw() {
        return !c.getUpgradeSlot().getHasStack() && super.shouldDraw();
      }
    });

    addProgressTooltip(80, 52, 14, 14);
  }

  private static float getFactor(Capacitors upgrade) {
    return TileEntityStirlingGenerator.getEnergyMultiplier(upgrade) *
            TileEntityStirlingGenerator.getBurnTimeMultiplier(upgrade);
  }

  private static String formatUpgrade(MessageFormat fmt, Capacitors upgrade) {
    float efficiency = getFactor(upgrade) / getFactor(Capacitors.BASIC_CAPACITOR);
    Object[] args = new Object[] {
      EnderIO.lang.localizeExact(upgrade.unlocalisedName.concat(".name")),
      efficiency,
      EnumChatFormatting.WHITE,
      EnumChatFormatting.GRAY
    };
    return fmt.format(args, new StringBuffer(), null).toString();
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected String formatProgressTooltip(int scaledProgress, float remaining) {
    int totalBurnTime = getTileEntity().totalBurnTime;
    int remainingTicks = (int)(remaining * totalBurnTime);
    int remainingSecs = remainingTicks / 20;
    int remainingRF = getTileEntity().getPowerUsePerTick() * remainingTicks;
    return MessageFormat.format(EnderIO.lang.localize("stirlingGenerator.remaining"),
            remaining, remainingSecs / 60, remainingSecs % 60, remainingRF);
  }

  @Override
  protected int scaleProgressForTooltip(float progress) {
    int totalBurnTime = getTileEntity().totalBurnTime;
    int scale = Math.max(100, (totalBurnTime+19)/20);
    return (int)(progress * scale);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    int scaled;

    if(shouldRenderProgress()) {
      scaled = getProgressScaled(12);
      drawTexturedModalRect(sx + 80, sy + 64 - scaled, 176, 12 - scaled, 14, scaled + 2);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    FontRenderer fr = getFontRenderer();
    int y = guiTop + fr.FONT_HEIGHT / 2 + 3;

    int output = 0;
    if(getTileEntity().isActive()) {
      output = getTileEntity().getPowerUsePerTick();
    }
    String txt = EnderIO.lang.localize("stirlingGenerator.output") + " " + PowerDisplayUtil.formatPower(output) + " " + PowerDisplayUtil.abrevation()
        + PowerDisplayUtil.perTickStr();
    int sw = fr.getStringWidth(txt);
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, y, ColorUtil.getRGB(Color.WHITE));

    txt = String.format("%s %d%%", EnderIO.lang.localize("stirlingGenerator.burnRate"),
        Math.round(getTileEntity().getBurnTimeMultiplier() / Config.stirlingGeneratorBurnTimeMultiplierT1 * 100));
    sw = fr.getStringWidth(txt);
    y += fr.FONT_HEIGHT + 3;
    fr.drawStringWithShadow(txt, guiLeft + xSize / 2 - sw / 2, y, ColorUtil.getRGB(Color.WHITE));

  }
}
