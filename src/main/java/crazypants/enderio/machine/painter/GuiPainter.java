package crazypants.enderio.machine.painter;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.common.vecmath.Vector4f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

@SideOnly(Side.CLIENT)
public class GuiPainter extends GuiPoweredMachineBase<TileEntityPainter> {

    public GuiPainter(InventoryPlayer par1InventoryPlayer, TileEntityPainter te) {
        super(te, new PainterContainer(par1InventoryPlayer, te), "painter");

        addProgressTooltip(88, 34, 24, 16);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int k = guiLeft;
        int l = guiTop;

        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

        if (shouldRenderProgress()) {
            int scaled = getProgressScaled(24);
            drawTexturedModalRect(k + 88, l + 34, 176, 14, scaled + 1, 16);
        }

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }

    @Override
    protected boolean showRecipeButton() {
        return false;
    }

    @Override
    protected void renderSlotHighlight(int slot, Vector4f col) {
        if (getTileEntity().getSlotDefinition().isOutputSlot(slot)) {
            renderSlotHighlight(col, 117, 31, 24, 24);
        } else if (slot != 1) {
            super.renderSlotHighlight(slot, col);
        }
    }
}
