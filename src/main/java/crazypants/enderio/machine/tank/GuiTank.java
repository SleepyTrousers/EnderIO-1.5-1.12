package crazypants.enderio.machine.tank;

import java.awt.Rectangle;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.network.PacketHandler;

public class GuiTank extends GuiMachineBase<TileTank> {

    public enum VoidMode implements ICycleEnum {

        ALWAYS(IconEIO.TICK),
        IF_NOT_CONTAINER(IconEIO.ITEM_SINGLE),
        NEVER(IconEIO.CROSS);

        private final IWidgetIcon icon;
        private final List<String> unlocTooltips;

        private VoidMode(IWidgetIcon icon) {
            this.icon = icon;
            String prefix = "gui.void.mode";
            String mode = prefix + "." + name().toLowerCase(Locale.US);
            this.unlocTooltips = Lists.newArrayList(prefix, mode, mode + ".desc");
        }

        @Override
        public IWidgetIcon getIcon() {
            return icon;
        }

        @Override
        public List<String> getTooltipLines() {
            return EnderIO.lang.localizeAll(unlocTooltips);
        }
    }

    private CycleButton<VoidMode> voidBut;

    public GuiTank(InventoryPlayer par1InventoryPlayer, TileTank te) {
        super(te, new ContainerTank(par1InventoryPlayer, te), "tank");

        addToolTip(new GuiToolTip(new Rectangle(80, 21, 16, 47), "") {

            @Override
            protected void updateText() {
                text.clear();
                String heading = EnderIO.lang.localize("tank.tank");
                if (getTileEntity().tank.getFluid() != null) {
                    heading += ": " + getTileEntity().tank.getFluid().getLocalizedName();
                }
                text.add(heading);
                text.add(Fluids.toCapactityString(getTileEntity().tank));
            }
        });

        addToolTip(new GuiToolTip(new Rectangle(14, 35, 18, 18), EnderIO.lang.localize("gui.tooltip.voidslot")) {

            public boolean shouldDraw() {
                return super.shouldDraw() && getTileEntity().canVoidItems();
            }
        });

        voidBut = new CycleButton<VoidMode>(this, 123, 155, 43, VoidMode.class);
    }

    @Override
    public void initGui() {
        super.initGui();
        voidBut.onGuiInit();
        voidBut.setMode(getTileEntity().getVoidMode());
        ((ContainerTank) inventorySlots).createGhostSlots(getGhostSlots());
    }

    @Override
    public void updateScreen() {
        Slot slot = (Slot) inventorySlots.inventorySlots.get(2);
        if (getTileEntity().canVoidItems()) {
            slot.xDisplayPosition = 15;
            slot.yDisplayPosition = 36;
            voidBut.onGuiInit();
        } else {
            slot.xDisplayPosition = 10000;
            slot.yDisplayPosition = 10000;
            voidBut.detach();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == voidBut.id) {
            getTileEntity().setVoidMode(voidBut.getMode());
            PacketHandler.INSTANCE.sendToServer(new PacketTankVoidMode(getTileEntity()));
        }
    }

    @Override
    protected boolean showRecipeButton() {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

        if (getTileEntity().canVoidItems()) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(2);
            drawTexturedModalRect(sx + slot.xDisplayPosition - 1, sy + slot.yDisplayPosition - 1, xSize, 0, 18, 18);
            IconEIO.map.render(IconEIO.MINUS, sx + slot.xDisplayPosition, sy + slot.yDisplayPosition, true);
        }

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);

        RenderUtil.bindBlockTexture();
        RenderUtil.renderGuiTank(getTileEntity().tank, guiLeft + 80, guiTop + 21, zLevel, 16, 47);
    }
}
