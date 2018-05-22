package crazypants.enderio.machines.machine.vacuum.xp;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.client.render.RenderUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.enderio.base.machine.gui.GuiMachineBase.BUTTON_SIZE;

public class GuiXPVacuum extends GuiContainerBaseEIO implements IVacuumRangeRemoteExec.GUI {

  private static final int RANGE_LEFT = 123;
  private static final int RANGE_TOP = 32;
  private static final int RANGE_WIDTH = 16;

  private static final int ID_RANGE_UP = 4611;
  private static final int ID_RANGE_DOWN = 4612;
  private static final int ID_SHOW_RANGE = 4613;

  private final @Nonnull ToggleButton showRangeB;
  private final @Nonnull GuiToolTip rangeTooltip;
  private final @Nonnull GuiToolTip primeTooltip;
  private final @Nonnull MultiIconButton rangeUpB;
  private final @Nonnull MultiIconButton rangeDownB;

  private final @Nonnull String headerRange;
  private final @Nonnull String headerXPVacuum;

  private final @Nonnull TileXPVacuum te;

  public GuiXPVacuum(@Nonnull Container container, @Nonnull TileXPVacuum te) {
    super(container, "xp_vacuum");
    this.te = te;

    ySize = 152;
    xSize = 176;

    int x = RANGE_LEFT;
    int y = RANGE_TOP;

    rangeTooltip = new GuiToolTip(new Rectangle(x, y, RANGE_WIDTH, 16), Lang.GUI_VACUUM_RANGE_TOOLTIP.get());
    primeTooltip = new GuiToolTip(new Rectangle(27, 22, 32, 32), Lang.GUI_VACUUM_PRIME_TOOLTIP.get());

    x += RANGE_WIDTH;
    rangeUpB = MultiIconButton.createAddButton(this, ID_RANGE_UP, x, y);

    y += 8;
    rangeDownB = MultiIconButton.createMinusButton(this, ID_RANGE_DOWN, x, y);

    showRangeB = new ToggleButton(this, ID_SHOW_RANGE, x + 10, y - 8, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public @Nonnull List<String> getToolTipText() {
        return Lists.newArrayList((showRangeB.isSelected() ? Lang.GUI_HIDE_RANGE : Lang.GUI_SHOW_RANGE).get());
      }
    });

    headerRange = Lang.GUI_VACUUM_RANGE.get();
    headerXPVacuum = Lang.GUI_VACUUM_XP_HEADER.get();
  }

  @Override
  public void initGui() {
    super.initGui();

    rangeUpB.onGuiInit();
    rangeDownB.onGuiInit();
    addToolTip(rangeTooltip);
    if (!te.isFormed()) {
      addToolTip(primeTooltip);
    }
    showRangeB.onGuiInit();
    showRangeB.setSelected(te.isShowingRange());
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    switch (guiButton.id) {
    case ID_RANGE_UP:
      doSetVacuumRange((int) (te.getRange() + 1));
      break;
    case ID_RANGE_DOWN:
      doSetVacuumRange((int) (te.getRange() - 1));
      break;
    case ID_SHOW_RANGE:
      te.setShowRange(showRangeB.isSelected());
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    FontRenderer fr = getFontRenderer();
    fr.drawString(headerRange, sx + xSize - 11 - fr.getStringWidth(headerRange), sy + 19, ColorUtil.getRGB(Color.DARK_GRAY));
    fr.drawString(headerXPVacuum, getGuiLeft() + 20, getGuiTop() + 6, ColorUtil.getRGB(Color.DARK_GRAY));

    IconEIO.map.render(EnderWidget.BUTTON_DOWN, sx + RANGE_LEFT, sy + RANGE_TOP, RANGE_WIDTH, 16, 0, true);
    String str = Integer.toString((int) te.getRange());
    int sw = fr.getStringWidth(str);
    fr.drawString(str, sx + RANGE_LEFT + RANGE_WIDTH - sw - 5, sy + RANGE_TOP + 5, ColorUtil.getRGB(Color.black));

    if (te.isFormed()) {
      renderFluid(new FluidStack(Fluids.XP_JUICE.getFluid(), 1), getGuiLeft() + 26, getGuiTop() + 21);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  private void renderFluid(FluidStack f, int x, int y) {
    ResourceLocation iconKey = f.getFluid().getStill();
    TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
    if (icon != null) {
      RenderUtil.renderGuiTank(f, 1000, 1000, x + 1, y + 1, 0, 32, 32);
    }
  }

}
