package crazypants.enderio.machine.alloy;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IIconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.alloy.TileAlloySmelter.Mode;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.PacketHandler;

public class GuiAlloySmelter extends GuiPoweredMachineBase<TileAlloySmelter> {

    private final IIconButton vanillaFurnaceButton;
    private final GuiToolTip vanillaFurnaceTooltip;

    protected static final int SMELT_MODE_BUTTON_ID = 76;

    public GuiAlloySmelter(InventoryPlayer par1InventoryPlayer, TileAlloySmelter furnaceInventory) {
        super(furnaceInventory, new ContainerAlloySmelter(par1InventoryPlayer, furnaceInventory), "alloySmelter");

        vanillaFurnaceButton = new IIconButton(
                getFontRenderer(),
                SMELT_MODE_BUTTON_ID,
                0,
                0,
                null,
                RenderUtil.BLOCK_TEX);
        vanillaFurnaceButton.setSize(BUTTON_SIZE, BUTTON_SIZE);

        vanillaFurnaceTooltip = new GuiToolTip(
                new Rectangle(xSize - 5 - BUTTON_SIZE, 62, BUTTON_SIZE, BUTTON_SIZE),
                (String[]) null);

        addProgressTooltip(55, 35, 14, 14);
        addProgressTooltip(103, 35, 14, 14);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        vanillaFurnaceButton.xPosition = guiLeft + vanillaFurnaceTooltip.getBounds().x;
        vanillaFurnaceButton.yPosition = guiTop + vanillaFurnaceTooltip.getBounds().y;

        buttonList.add(vanillaFurnaceButton);
        addToolTip(vanillaFurnaceTooltip);

        updateVanillaFurnaceButton();
    }

    @Override
    protected void renderSlotHighlight(int slot, Vector4f col) {
        if (getTileEntity().getSlotDefinition().isOutputSlot(slot)) {
            renderSlotHighlight(col, 75, 54, 24, 24);
        } else {
            super.renderSlotHighlight(slot, col);
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == SMELT_MODE_BUTTON_ID) {
            getTileEntity().setMode(getTileEntity().getMode().next());
            updateVanillaFurnaceButton();
            PacketHandler.INSTANCE.sendToServer(new PacketClientState(getTileEntity()));
        } else {
            super.actionPerformed(par1GuiButton);
        }
    }

    private void updateVanillaFurnaceButton() {
        IIcon icon = EnderIO.blockAlloySmelter.vanillaSmeltingOn;
        String unlocText = "gui.alloy.mode.all";
        if (getTileEntity().getMode() == Mode.ALLOY) {
            icon = EnderIO.blockAlloySmelter.vanillaSmeltingOff;
            unlocText = "gui.alloy.mode.alloy";
        } else if (getTileEntity().getMode() == Mode.FURNACE) {
            icon = EnderIO.blockAlloySmelter.vanillaSmeltingOnly;
            unlocText = "gui.alloy.mode.furnace";
        }
        vanillaFurnaceButton.setIcon(icon);
        vanillaFurnaceTooltip
                .setToolTipText(EnderIO.lang.localize("gui.alloy.mode.heading"), EnderIO.lang.localize(unlocText));
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = guiLeft;
        int sy = guiTop;

        drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

        if (shouldRenderProgress()) {
            int scaled = getProgressScaled(14) + 1;
            drawTexturedModalRect(sx + 55, sy + 49 - scaled, 176, 14 - scaled, 14, scaled);
            drawTexturedModalRect(sx + 103, sy + 49 - scaled, 176, 14 - scaled, 14, scaled);
        }

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }
}
