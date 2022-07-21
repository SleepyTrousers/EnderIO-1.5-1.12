package crazypants.enderio.conduit.gui;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.network.PacketHandler;
import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class GuiExternalConnectionSelector extends GuiScreen {

    Set<ForgeDirection> cons;
    IConduitBundle cb;
    String adjacentBlockNames[] = new String[6];
    Point textPositions[] = new Point[6];

    public GuiExternalConnectionSelector(IConduitBundle cb) {
        this.cb = cb;
        cons = new HashSet<ForgeDirection>();
        for (IConduit con : cb.getConduits()) {
            if (con instanceof IInsulatedRedstoneConduit) {
                Set<ForgeDirection> conCons = con.getConduitConnections();
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    if (!conCons.contains(dir)) {
                        cons.add(dir);
                    }
                }

            } else {
                cons.addAll(con.getExternalConnections());
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        ForgeDirection dir = ForgeDirection.values()[b.id];
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        BlockCoord loc = cb.getLocation();
        PacketHandler.INSTANCE.sendToServer(new PacketOpenConduitUI(cb.getEntity(), dir));
        player.openGui(
                EnderIO.instance,
                GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + dir.ordinal(),
                player.worldObj,
                loc.x,
                loc.y,
                loc.z);
    }

    protected String getBlockNameForDirection(ForgeDirection direction) {
        BlockCoord coords = cb.getLocation();
        Block b = cb.getWorld()
                .getBlock(coords.x + direction.offsetX, coords.y + direction.offsetY, coords.z + direction.offsetZ);
        if (b != null && b != Blocks.air) {
            return b.getLocalizedName();
        }
        return null;
    }

    @Override
    public void initGui() {
        GuiButton b;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            Point p = getOffsetForDir(dir);
            adjacentBlockNames[dir.ordinal()] = getBlockNameForDirection(dir);
            textPositions[dir.ordinal()] = new Point(p.x, p.y + 20);
            b = new GuiButton(dir.ordinal(), p.x, p.y, 60, 20, dir.toString());
            buttonList.add(b);
            if (!cons.contains(dir)) {
                b.enabled = false;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {

        drawDefaultBackground();

        super.drawScreen(par1, par2, par3);

        int butHeight = 20;
        String txt = "Select Connection to Adjust";
        int x = width / 2 - (Minecraft.getMinecraft().fontRenderer.getStringWidth(txt) / 2);
        int y = height / 2 - butHeight * 3 - 5;
        Tessellator.instance.startDrawingQuads();
        drawString(Minecraft.getMinecraft().fontRenderer, txt, x, y, ColorUtil.getARGB(Color.white));
        for (int i = 0; i < 6; i++) {
            String blockName = adjacentBlockNames[i];
            if (blockName == null) {
                continue;
            }
            int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(blockName);
            drawString(
                    Minecraft.getMinecraft().fontRenderer,
                    blockName,
                    textPositions[i].x + 60 / 2 - textWidth / 2,
                    textPositions[i].y,
                    ColorUtil.getARGB(Color.gray));
        }
        Tessellator.instance.draw();
    }

    private Point getOffsetForDir(ForgeDirection dir) {
        int mx = width / 2;
        int my = height / 2;
        int butWidth = 60;
        int butHeight = 20;

        int x = mx - butWidth / 2 + (dir.offsetX * butWidth);
        int y = my - butHeight / 2 + (dir.offsetZ * butHeight * 2);
        x += Math.abs(dir.offsetY) * (5 + butWidth * 2);
        y -= (dir.offsetY * butHeight * 2);

        return new Point(x, y);
    }
}
