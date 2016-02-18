package crazypants.enderio.conduit.gui;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumFacing;

public class GuiExternalConnectionSelector extends GuiScreen {

  Set<EnumFacing> cons;
  IConduitBundle cb;

  public GuiExternalConnectionSelector(IConduitBundle cb) {
    this.cb = cb;
    cons = new HashSet<EnumFacing>();
    for (IConduit con : cb.getConduits()) {
      if(con instanceof IInsulatedRedstoneConduit) {
        Set<EnumFacing> conCons = con.getConduitConnections();
        for(EnumFacing dir : EnumFacing.VALUES) {
          if(!conCons.contains(dir)) {
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
    EnumFacing dir = EnumFacing.values()[b.id];
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    BlockCoord loc = cb.getLocation();
    PacketHandler.INSTANCE.sendToServer(new PacketOpenConduitUI(cb.getEntity(), dir));
    player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + dir.ordinal(), player.worldObj, loc.x, loc.y, loc.z);
  }

  @Override
  public void initGui() {
    GuiButton b;
    for (EnumFacing dir : EnumFacing.VALUES) {
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
    int x = width / 2 - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(txt) / 2);
    int y = height / 2 - butHeight * 3 - 5;
        
    drawString(Minecraft.getMinecraft().fontRendererObj, txt, x, y, ColorUtil.getARGB(Color.white));
  }

  private Point getOffsetForDir(EnumFacing dir) {
    int mx = width / 2;
    int my = height / 2;
    int butWidth = 60;
    int butHeight = 20;

    int x = mx - butWidth / 2 + (dir.getFrontOffsetX() * butWidth);
    int y = my - butHeight / 2 + (dir.getFrontOffsetZ() * butHeight * 2);
    x += Math.abs(dir.getFrontOffsetY()) * (5 + butWidth * 2);
    y -= (dir.getFrontOffsetY() * butHeight * 2);

    return new Point(x, y);
  }

}
