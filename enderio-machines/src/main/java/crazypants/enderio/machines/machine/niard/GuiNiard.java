package crazypants.enderio.machines.machine.niard;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;
import com.google.common.collect.Lists;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class GuiNiard extends GuiCapMachineBase<TileNiard> {

  private static final int POWERX = 12;
  private static final int POWERY = 14;
  private static final int POWER_HEIGHT = 42;

  private static final @Nonnull Rectangle RECTANGLE_TANK = new Rectangle(80, 21, 16, 47);

  private final @Nonnull ToggleButton showRangeB;

  public GuiNiard(@Nonnull InventoryPlayer playerInv, @Nonnull TileNiard te) {
    super(te, new ContainerNiard<>(playerInv, te), "niard");
    addDrawingElement(new PowerBar(te.getEnergy(), this, POWERX + 4, POWERY, POWER_HEIGHT));
    addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.GUI_TANK_TANK_TANK_TANK.get());
        text.add(LangFluid.MB(getTileEntity().inputTank));
      }

    });

    showRangeB = new ToggleButton(this, -1, 155, 43, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(16, 16);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public @Nonnull List<String> getToolTipText() {
        return Lists.newArrayList((showRangeB.isSelected() ? Lang.GUI_HIDE_RANGE : Lang.GUI_SHOW_RANGE).get());
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
    ((ContainerNiard<?>) inventorySlots).createGhostSlots(ghostSlots);
    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    if (b == showRangeB) {
      getTileEntity().setShowRange(showRangeB.isSelected());
      return;
    }
    super.actionPerformed(b);
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().inputTank.getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();

    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    RenderUtil.renderGuiTank(getTileEntity().inputTank, guiLeft + RECTANGLE_TANK.x, guiTop + RECTANGLE_TANK.y, zLevel, RECTANGLE_TANK.width,
        RECTANGLE_TANK.height);

    renderFlowingFluid(getTileEntity().inputTank.getFluid(), guiLeft + 112, guiTop + 28, 39, 56);

    bindGuiTexture();
    GlStateManager.enableBlend();
    drawTexturedModalRect(guiLeft + 112, guiTop + 28, 200, 0, 39, 56);
    GlStateManager.disableBlend();

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  public static @Nonnull TextureAtlasSprite getFlowingTexture(@Nonnull Fluid fluid) {
    ResourceLocation iconKey = fluid.getStill();
    final TextureAtlasSprite textureExtry = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
    return textureExtry != null ? textureExtry : RenderUtil.getStillTexture(fluid);
  }

  public static void renderFlowingFluid(FluidStack fluid, double x, double y, double width, double height) {
    if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
      return;
    }

    @SuppressWarnings("null")
    TextureAtlasSprite icon = getFlowingTexture(fluid.getFluid());

    RenderUtil.bindBlockTexture();
    int color = fluid.getFluid().getColor(fluid);
    GlStateManager.color((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f);

    GlStateManager.enableBlend();
    for (int i = 0; i < width; i += 16) {
      for (int j = 0; j < (int) height; j += 16) {
        int drawWidth = (int) Math.min(width - i, 16);
        int drawHeight = Math.min((int) height - j, 16);

        int drawX = (int) (x + i);
        int drawY = (int) y + j;

        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(drawX, drawY + drawHeight, 0).tex(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
        buffer.pos(drawX + drawWidth, drawY + drawHeight, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
        buffer.pos(drawX + drawWidth, drawY, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
        buffer.pos(drawX, drawY, 0).tex(minU, minV).endVertex();
        tessellator.draw();
      }
    }
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }

}
