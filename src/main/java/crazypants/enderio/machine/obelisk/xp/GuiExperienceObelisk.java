package crazypants.enderio.machine.obelisk.xp;

import com.enderio.core.client.gui.button.IconButton;
import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.network.GuiPacket;
import crazypants.enderio.xp.ExperienceBarRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

import javax.annotation.Nonnull;
import java.io.IOException;

public class GuiExperienceObelisk extends GuiMachineBase<TileExperienceObelisk> {

  private @Nonnull IconButton p;
  private @Nonnull IconButton pp;
  private @Nonnull IconButton ppp;

  private @Nonnull IconButton m;
  private @Nonnull IconButton mm;
  private @Nonnull IconButton mmm;

  public GuiExperienceObelisk(@Nonnull InventoryPlayer playerInv, @Nonnull TileExperienceObelisk te) {
    super(te, new ContainerExperienceObelisk(te), "experience_obelisk");
    ySize = 116;

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
    p.setToolTip(EnderIO.lang.localize("gui.machine.button.storelevel"), EnderIO.lang.localize("gui.machine.tooltip.storelevel"));

    x += spacing + bw;
    pp = new IconButton(this, 801, x, y, IconEIO.DOUBLE_PLUS);
    pp.setSize(bw, bw);
    pp.setIconMargin(2, 2);
    pp.setToolTip(EnderIO.lang.localize("gui.machine.button.storelevels"), EnderIO.lang.localize("gui.machine.tooltip.storelevels"));

    x += spacing + bw;
    ppp = new IconButton(this, 802, x, y, IconEIO.TRIPLE_PLUS);
    ppp.setSize(bw, bw);
    ppp.setIconMargin(2, 2);
    ppp.setToolTip(EnderIO.lang.localize("gui.machine.button.storeall"), EnderIO.lang.localize("gui.machine.tooltip.storeall"));

    x = startX;
    y = 75;
    m = new IconButton(this, 803, x, y, IconEIO.SINGLE_MINUS);
    m.setSize(bw, bw);
    m.setIconMargin(2, 2);
    m.setToolTip(EnderIO.lang.localize("gui.machine.button.retrievelevel"), EnderIO.lang.localize("gui.machine.tooltip.retrievelevel"));

    x += spacing + bw;
    mm = new IconButton(this, 804, x, y, IconEIO.DOUBLE_MINUS);
    mm.setSize(bw, bw);
    mm.setIconMargin(2, 2);
    mm.setToolTip(EnderIO.lang.localize("gui.machine.button.retrievelevels"), EnderIO.lang.localize("gui.machine.tooltip.retrievelevels"));

    x += spacing + bw;
    mmm = new IconButton(this, 805, x, y, IconEIO.TRIPLE_MINUS);
    mmm.setSize(bw, bw);
    mmm.setIconMargin(2, 2);
    mmm.setToolTip(EnderIO.lang.localize("gui.machine.button.retrieveall"), EnderIO.lang.localize("gui.machine.tooltip.retrieveall"));
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
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
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
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      int currLevel = player.experienceLevel;
      int targetLevel = Math.max(0, currLevel + levels);

      GuiPacket.send(this, ContainerExperienceObelisk.DWN_XP, targetLevel);
    } else {
      GuiPacket.send(this, ContainerExperienceObelisk.ADD_XP, levels);
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    int width1 = 110;
    ExperienceBarRenderer.render(this, getGuiLeft() + xSize / 2 - width1 / 2, getGuiTop() + 55, width1, getTileEntity().getContainer());

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

}
