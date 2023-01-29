package crazypants.enderio.machine.spawner;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.PacketHandler;

public class GuiPoweredSpawner extends GuiPoweredMachineBase<TilePoweredSpawner> {

    private final MultiIconButton modeB;
    private final Rectangle progressTooltipRect;
    private boolean wasSpawnMode;
    private String header;

    public GuiPoweredSpawner(InventoryPlayer par1InventoryPlayer, TilePoweredSpawner te) {
        super(te, new ContainerPoweredSpawner(par1InventoryPlayer, te), "poweredSpawner");

        modeB = MultiIconButton.createRightArrowButton(this, 8888, 115, 10);
        modeB.setSize(10, 16);

        addProgressTooltip(80, 34, 14, 14);
        progressTooltipRect = progressTooltips.get(0).getBounds();

        updateSpawnMode(te.isSpawnMode());
    }

    @Override
    public void initGui() {
        super.initGui();
        modeB.onGuiInit();
        ((ContainerPoweredSpawner) inventorySlots).createGhostSlots(getGhostSlots());
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton == modeB) {
            getTileEntity().setSpawnMode(!getTileEntity().isSpawnMode());
            PacketHandler.INSTANCE.sendToServer(new PacketMode(getTileEntity()));
        } else {
            super.actionPerformed(par1GuiButton);
        }
    }

    private void updateSpawnMode(boolean spawnMode) {
        wasSpawnMode = spawnMode;
        ((ContainerPoweredSpawner) inventorySlots).setSlotVisibility(!spawnMode);

        if (spawnMode) {
            header = EnderIO.lang.localize("gui.machine.poweredspawner.spawn");
            progressTooltipRect.x = 80;
            progressTooltipRect.y = 34;
            progressTooltipRect.width = 14;
            progressTooltipRect.height = 14;
        } else {
            header = EnderIO.lang.localize("gui.machine.poweredspawner.capture");
            progressTooltipRect.x = 52;
            progressTooltipRect.y = 40;
            progressTooltipRect.width = 72;
            progressTooltipRect.height = 21;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);

        TilePoweredSpawner spawner = getTileEntity();
        boolean spawnMode = spawner.isSpawnMode();

        if (spawnMode != wasSpawnMode) {
            updateSpawnMode(spawnMode);
        }

        FontRenderer fr = getFontRenderer();
        int x = sx + xSize / 2 - fr.getStringWidth(header) / 2;
        int y = sy + fr.FONT_HEIGHT + 6;
        fr.drawStringWithShadow(header, x, y, ColorUtil.getRGB(Color.WHITE));

        bindGuiTexture();

        if (spawnMode) {
            drawTexturedModalRect(sx + 80, sy + 34, 207, 0, 17, 15);
            if (shouldRenderProgress()) {
                int scaled = getProgressScaled(14) + 1;
                drawTexturedModalRect(sx + 81, sy + 34 + 14 - scaled, 176, 14 - scaled, 14, scaled);
            }
        } else {
            drawTexturedModalRect(sx + 52, sy + 40, 52, 170, 72, 21);
            if (shouldRenderProgress()) {
                int scaled = getProgressScaled(24);
                drawTexturedModalRect(sx + 76, sy + 43, 176, 14, scaled + 1, 16);
            }
        }
    }

    @Override
    protected boolean showRecipeButton() {
        return false;
    }
}
