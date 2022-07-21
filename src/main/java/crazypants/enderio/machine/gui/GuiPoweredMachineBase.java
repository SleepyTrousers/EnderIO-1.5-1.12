package crazypants.enderio.machine.gui;

import com.enderio.core.client.gui.widget.GuiToolTip;
import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public abstract class GuiPoweredMachineBase<T extends AbstractPoweredMachineEntity> extends GuiMachineBase<T> {

    protected static final int POWER_Y = 14;
    protected final int POWER_X = 15;
    protected static final int POWER_WIDTH = 10;
    protected static final int POWER_HEIGHT = 42;
    protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

    public GuiPoweredMachineBase(T machine, Container container, String... guiTexture) {
        super(machine, container, guiTexture);
        if (renderPowerBar()) {
            addToolTip(new GuiToolTip(new Rectangle(getPowerX(), getPowerY(), getPowerWidth(), getPowerHeight()), "") {

                @Override
                protected void updateText() {
                    text.clear();
                    if (renderPowerBar()) {
                        updatePowerBarTooltip(text);
                    }
                }
            });
        }
    }

    protected String getPowerOutputLabel() {
        return StatCollector.translateToLocal("enderio.gui.max");
    }

    protected int getPowerOutputValue() {
        return getTileEntity().getPowerUsePerTick();
    }

    protected void updatePowerBarTooltip(List<String> text) {
        text.add(getPowerOutputLabel() + " " + PowerDisplayUtil.formatPower(getPowerOutputValue()) + " "
                + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr());
        text.add(PowerDisplayUtil.formatStoredPower(
                getTileEntity().getEnergyStored(), getTileEntity().getMaxEnergyStored()));
    }

    public void renderPowerBar(int k, int l) {
        if (renderPowerBar()) {
            int i1 = getTileEntity().getEnergyStoredScaled(getPowerHeight());
            // x, y, u, v, width, height
            drawTexturedModalRect(
                    k + getPowerX(),
                    l + (getPowerY() + getPowerHeight()) - i1,
                    getPowerU(),
                    getPowerV(),
                    getPowerWidth(),
                    i1);
        }
    }

    protected int getPowerX() {
        return POWER_X;
    }

    protected int getPowerY() {
        return POWER_Y;
    }

    protected int getPowerWidth() {
        return POWER_WIDTH;
    }

    protected int getPowerHeight() {
        return POWER_HEIGHT;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        renderPowerBar(k, l);

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }

    protected int getPowerV() {
        return 31;
    }

    protected int getPowerU() {
        return 176;
    }

    protected boolean renderPowerBar() {
        return true;
    }
}
