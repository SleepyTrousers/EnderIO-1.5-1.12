package crazypants.enderio.conduit;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.ForgeDirection;
import appeng.api.WorldCoord;
import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.machine.monitor.MJReaderPacketHandler;
import crazypants.render.ColorUtil;

public class GuiExternalConnectionSelector extends GuiScreen {

  Set<ForgeDirection> cons;
  IConduitBundle cb;

  public GuiExternalConnectionSelector(IConduitBundle cb) {
    this.cb = cb;
    cons = new HashSet<ForgeDirection>();
    for (IConduit con : cb.getConduits()) {
      cons.addAll(con.getExternalConnections());
    }
  }

  @Override
  protected void actionPerformed(GuiButton b) {
    ForgeDirection dir = ForgeDirection.values()[b.id];
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    WorldCoord loc = cb.getLocation();

    PacketDispatcher.sendPacketToServer(MJReaderPacketHandler.createOpenConduitGuiPacket(cb, dir));
    player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + dir.ordinal(), player.worldObj, loc.x, loc.y, loc.z);
  }

  @Override
  public void initGui() {
    GuiButton b;
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      Point p = getOffsetForDir(dir);
      b = new GuiButton(dir.ordinal(), p.x, p.y, 60, 20, dir.toString());
      buttonList.add(b);
      if(!cons.contains(dir)) {
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
    int x = width / 2 - (fontRenderer.getStringWidth(txt) / 2);
    int y = height / 2 - (int) (butHeight * 3) - 5;
    Tessellator.instance.startDrawingQuads();
    drawString(fontRenderer, txt, x, y, ColorUtil.getARGB(Color.white));
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
