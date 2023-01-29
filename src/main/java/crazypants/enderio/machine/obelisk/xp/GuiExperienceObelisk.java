package crazypants.enderio.machine.obelisk.xp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.ContainerNoInv;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.ExperienceBarRenderer;
import crazypants.enderio.xp.PacketDrainPlayerXP;
import crazypants.enderio.xp.PacketGivePlayerXP;

public class GuiExperienceObelisk extends GuiMachineBase<TileExperienceObelisk> {

    private IconButton p;
    private IconButton pp;
    private IconButton ppp;

    private IconButton m;
    private IconButton mm;
    private IconButton mmm;

    public GuiExperienceObelisk(InventoryPlayer playerInv, TileExperienceObelisk te) {
        super(te, new ContainerNoInv(te), "experianceObelisk");
        ySize = 115;

        int spacing = 5;
        int bw = 20;
        int startX = xSize / 2 - bw / 2 - spacing - bw;

        int x;
        int y;

        x = startX;
        y = 20;
        p = new IconButton(this, 800, x, y, IconEIO.SINGLE_PLUS);
        p.setSize(bw, bw);
        p.setIconMargin(2, 2);
        p.setToolTip(
                EnderIO.lang.localize("gui.machine.button.storelevel"),
                EnderIO.lang.localize("gui.machine.tooltip.storelevel"));

        x += spacing + bw;
        pp = new IconButton(this, 801, x, y, IconEIO.DOUBLE_PLUS);
        pp.setSize(bw, bw);
        pp.setIconMargin(2, 2);
        pp.setToolTip(
                EnderIO.lang.localize("gui.machine.button.storelevels"),
                EnderIO.lang.localize("gui.machine.tooltip.storelevels"));

        x += spacing + bw;
        ppp = new IconButton(this, 802, x, y, IconEIO.TRIPLE_PLUS);
        ppp.setSize(bw, bw);
        ppp.setIconMargin(2, 2);
        ppp.setToolTip(
                EnderIO.lang.localize("gui.machine.button.storeall"),
                EnderIO.lang.localize("gui.machine.tooltip.storeall"));

        x = startX;
        y = 75;
        m = new IconButton(this, 803, x, y, IconEIO.SINGLE_MINUS);
        m.setSize(bw, bw);
        m.setIconMargin(2, 2);
        m.setToolTip(
                EnderIO.lang.localize("gui.machine.button.retrievelevel"),
                EnderIO.lang.localize("gui.machine.tooltip.retrievelevel"));

        x += spacing + bw;
        mm = new IconButton(this, 804, x, y, IconEIO.DOUBLE_MINUS);
        mm.setSize(bw, bw);
        mm.setIconMargin(2, 2);
        mm.setToolTip(
                EnderIO.lang.localize("gui.machine.button.retrievelevels"),
                EnderIO.lang.localize("gui.machine.tooltip.retrievelevels"));

        x += spacing + bw;
        mmm = new IconButton(this, 805, x, y, IconEIO.TRIPLE_MINUS);
        mmm.setSize(bw, bw);
        mmm.setIconMargin(2, 2);
        mmm.setToolTip(
                EnderIO.lang.localize("gui.machine.button.retrieveall"),
                EnderIO.lang.localize("gui.machine.tooltip.retrieveall"));
    }

    @Override
    public void initGui() {
        super.initGui();
        p.onGuiInit();
        pp.onGuiInit();
        ppp.onGuiInit();

        m.onGuiInit();
        mm.onGuiInit();
        mmm.onGuiInit();
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        super.actionPerformed(b);
        int levels = 0;
        if (b == p) {
            levels = -1;
        } else if (b == pp) {
            levels = -10;
        } else if (b == ppp) {
            levels = -5000;
        } else if (b == m) {
            levels = 1;
        } else if (b == mm) {
            levels = 10;
        } else if (b == mmm) {
            levels = 5000;
        } else {
            return;
        }

        if (levels < 0) {

            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            int currLevel = player.experienceLevel;
            int targetLevel = Math.max(0, currLevel + levels);

            PacketDrainPlayerXP packet = new PacketDrainPlayerXP(getTileEntity(), targetLevel, false);
            PacketHandler.INSTANCE.sendToServer(packet);
        } else {

            PacketGivePlayerXP packet = new PacketGivePlayerXP(getTileEntity(), levels);
            PacketHandler.INSTANCE.sendToServer(packet);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

        int width = 110;
        ExperienceBarRenderer.render(
                this,
                getGuiLeft() + xSize / 2 - width / 2,
                getGuiTop() + 55,
                width,
                getTileEntity().getContainer());

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }

    @Override
    protected boolean showRecipeButton() {
        return false;
    }
}
