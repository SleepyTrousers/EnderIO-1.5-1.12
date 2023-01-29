package crazypants.enderio.machine.buffer;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;

public class GuiBuffer extends GuiPoweredMachineBase<TileBuffer> {

    private static final String TEXTURE_SIMPLE = "buffer";
    private static final String TEXTURE_FULL = "buffer_full";

    private TextFieldEnder maxInput;
    private TextFieldEnder maxOutput;

    private int lastInput, lastOutput;

    public GuiBuffer(InventoryPlayer par1InventoryPlayer, TileBuffer te) {
        super(te, new ContainerBuffer(par1InventoryPlayer, te), TEXTURE_SIMPLE, TEXTURE_FULL);
        redstoneButton.setPosition(isFull() ? 153 : 120, 24);
        configB.setPosition(isFull() ? 153 : 120, 42);

        if (te.hasPower()) {
            int x = (isFull() ? 20 : 58);
            int y = guiTop + 27;

            maxInput = new TextFieldEnder(getFontRenderer(), x, y, 60, 12);
            y += 28;
            maxOutput = new TextFieldEnder(getFontRenderer(), x, y, 60, 12);

            textFields.add(maxInput);
            textFields.add(maxOutput);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        if (getTileEntity().hasPower()) {
            maxInput.setMaxStringLength(10);
            maxInput.setText(PowerDisplayUtil.formatPower(getTileEntity().getMaxInput()));
            maxOutput.setMaxStringLength(10);
            maxOutput.setText(PowerDisplayUtil.formatPower(getTileEntity().getMaxOutput()));
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        if (par1 == 'e') {
            super.keyTyped(par1, 1);
        }

        if (getTileEntity().hasPower()) {
            updateInputOutput();
        }
    }

    private void updateInputOutput() {
        int input = PowerDisplayUtil.parsePower(maxInput);
        setMaxInput(input);
        int output = PowerDisplayUtil.parsePower(maxOutput);
        setMaxOutput(output);
        sendUpdateToServer();
    }

    private void setMaxOutput(int output) {
        if (output != lastOutput) {
            lastOutput = output;
            maxOutput.setText(PowerDisplayUtil.formatPower(output));
        }
    }

    private void setMaxInput(int input) {
        if (input != lastInput) {
            lastInput = input;
            maxInput.setText(PowerDisplayUtil.formatPower(input));
            sendUpdateToServer();
        }
    }

    protected void sendUpdateToServer() {
        PacketHandler.INSTANCE.sendToServer(new PacketBufferIO(getTileEntity(), lastInput, lastOutput));
    }

    @Override
    protected boolean showRecipeButton() {
        return false;
    }

    @Override
    protected boolean renderPowerBar() {
        return getTileEntity().hasPower();
    }

    @Override
    public int getYSize() {
        return ySize;
    }

    @Override
    protected int getPowerHeight() {
        return 52;
    }

    @Override
    protected int getPowerX() {
        return isFull() ? 6 : 44;
    }

    @Override
    protected int getPowerY() {
        return 15;
    }

    @Override
    protected int getPowerV() {
        return 0;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        bindGuiTexture(isFull() ? 1 : 0);
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

        bindGuiTexture();

        if (getTileEntity().hasPower()) {
            drawPowerBg(sx, sy);
        }

        if (getTileEntity().hasInventory()) {
            drawSlotBg(sx, sy);
        }

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);

        String invName = EnderIO.lang.localizeExact(getTileEntity().getInventoryName() + ".name");
        getFontRenderer().drawStringWithShadow(
                invName,
                sx + (xSize / 2) - (getFontRenderer().getStringWidth(invName) / 2),
                sy + 4,
                0xFFFFFF);

        if (getTileEntity().hasPower()) {
            sx += isFull() ? 19 : 57;
            sy += 17;

            getFontRenderer().drawStringWithShadow(EnderIO.lang.localize("gui.simple.in"), sx, sy, 0xFFFFFF);
            getFontRenderer().drawStringWithShadow(EnderIO.lang.localize("gui.simple.out"), sx, sy + 27, 0xFFFFFF);
        }
    }

    boolean isFull() {
        return getTileEntity().hasInventory() && getTileEntity().hasPower();
    }

    @Override
    public void renderSlotHighlights(IoMode mode) {
        if (!getTileEntity().hasInventory()) {
            return;
        }

        for (int slot = 0; slot < getTileEntity().getSizeInventory(); slot++) {
            renderSlotHighlight(slot, mode);
        }
    }

    protected void renderSlotHighlight(int slot, IoMode mode) {
        Slot invSlot = (Slot) inventorySlots.inventorySlots.get(slot);
        if (mode == IoMode.PULL) {
            renderSlotHighlight(slot, PULL_COLOR);
        } else if (mode == IoMode.PUSH) {
            renderSlotHighlight(slot, PUSH_COLOR);
        } else if (mode == IoMode.PUSH_PULL) {
            renderSplitHighlight(invSlot.xDisplayPosition, invSlot.yDisplayPosition, 16, 16);
        }
    }

    protected void renderSplitHighlight(int x, int y, int width, int height) {
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, width, height / 2, PULL_COLOR);
        RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y + (height / 2), 0, width, height / 2, PUSH_COLOR);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawPowerBg(int sx, int sy) {
        drawTexturedModalRect(sx + (isFull() ? 5 : 43), sy + 14, xSize + 10, 0, 12, 54);
    }

    private void drawSlotBg(int sx, int sy) {
        drawTexturedModalRect(sx + (isFull() ? 95 : 61), sy + 14, xSize + 22, 0, 54, 54);
    }
}
