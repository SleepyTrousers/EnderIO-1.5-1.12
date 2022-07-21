package crazypants.enderio.machine.farm;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;
import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

public class GuiFarmStation extends GuiPoweredMachineBase<TileFarmStation> {

    private static final int LOCK_ID = 1234;

    public GuiFarmStation(InventoryPlayer par1InventoryPlayer, TileFarmStation machine) {
        super(machine, new FarmStationContainer(par1InventoryPlayer, machine), "farmStation");
        setYSize(ySize + 3);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        int x = getGuiLeft() + 36;
        int y = getGuiTop() + 43;

        buttonList.add(createLockButton(TileFarmStation.minSupSlot + 0, x, y));
        buttonList.add(createLockButton(TileFarmStation.minSupSlot + 1, x + 52, y));
        buttonList.add(createLockButton(TileFarmStation.minSupSlot + 2, x, y + 20));
        buttonList.add(createLockButton(TileFarmStation.minSupSlot + 3, x + 52, y + 20));

        ((FarmStationContainer) inventorySlots).createGhostSlots(getGhostSlots());
    }

    private IconButton createLockButton(int slot, int x, int y) {
        return new ToggleButton(this, LOCK_ID + slot, x, y, IconEIO.LOCK_UNLOCKED, IconEIO.LOCK_LOCKED)
                .setSelected(getTileEntity().isSlotLocked(slot));
    }

    @Override
    protected void drawForegroundImpl(int mouseX, int mouseY) {
        super.drawForegroundImpl(mouseX, mouseY);
        if (!isConfigOverlayEnabled()) {
            for (int i = TileFarmStation.minSupSlot; i <= TileFarmStation.maxSupSlot; i++) {
                if (getTileEntity().isSlotLocked(i)) {
                    Slot slot = inventorySlots.getSlot(i);
                    GL11.glEnable(GL11.GL_BLEND);
                    RenderUtil.renderQuad2D(
                            slot.xDisplayPosition, slot.yDisplayPosition, 0, 16, 16, new Vector4f(0, 0, 0, 0.5));
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        GL11.glEnable(GL11.GL_BLEND);
        fr.drawString("SW", sx + 55, sy + 48, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
        fr.drawString("NW", sx + 55, sy + 66, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
        fr.drawString("SE", sx + 73, sy + 48, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
        fr.drawString("NE", sx + 73, sy + 66, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
        GL11.glDisable(GL11.GL_BLEND);

        bindGuiTexture();
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id >= LOCK_ID + TileFarmStation.minSupSlot && b.id <= LOCK_ID + TileFarmStation.maxSupSlot) {
            getTileEntity().toggleLockedState(b.id - LOCK_ID);
        }
        super.actionPerformed(b);
    }

    @Override
    protected boolean showRecipeButton() {
        return false;
    }

    @Override
    protected String getPowerOutputLabel() {
        return EnderIO.lang.localize("farm.gui.baseUse");
    }

    @Override
    protected int getPowerHeight() {
        return super.getPowerHeight() + 3;
    }
}
