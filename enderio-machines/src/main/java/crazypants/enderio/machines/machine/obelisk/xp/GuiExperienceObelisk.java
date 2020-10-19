package crazypants.enderio.machines.machine.obelisk.xp;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.gui.GuiMachineBase;
import crazypants.enderio.base.xp.ExperienceBarRenderer;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiExperienceObelisk extends GuiMachineBase<TileExperienceObelisk> implements ContainerExperienceObeliskProxy {

  private @Nonnull IconButton p;
  private @Nonnull IconButton pp;
  private @Nonnull IconButton ppp;

  private @Nonnull IconButton m;
  private @Nonnull IconButton mm;
  private @Nonnull IconButton mmm;

  public GuiExperienceObelisk(@Nonnull InventoryPlayer playerInv, @Nonnull TileExperienceObelisk te) {
    super(te, new ContainerExperienceObelisk(Minecraft.getMinecraft().player, te), "experience_obelisk");
    ySize = 116;

    int spacing = 5;
    int bw = 20;
    int startX = xSize / 2 - bw / 2 - spacing - bw;

    int x;
    int y;

    x = startX;
    y = 20;
    p = new IconButton(this, 800, x, y, IconEIO.SINGLE_PLUS) {
      @Override
      public boolean isEnabled() {
        return super.isEnabled() && XpUtil.getPlayerXP(Minecraft.getMinecraft().player) >= XpUtil.getExperienceForLevel(1);
      }
    };
    p.setSize(bw, bw);
    p.setIconMargin(2, 2);
    p.setToolTip(new GuiToolTip(p.getBounds(), Lang.GUI_XP_STORE_1_1.get(), Lang.GUI_XP_STORE_1_2.get()) {
      @Override
      protected void updateText() {
        if (text.size() == 3) {
          text.remove(2);
        }
        if (XpUtil.getPlayerXP(Minecraft.getMinecraft().player) <= 0) {
          text.add(Lang.GUI_XP_STORE_EMPTY.get());
        }
      };
    });

    x += spacing + bw;
    pp = new IconButton(this, 801, x, y, IconEIO.DOUBLE_PLUS) {
      @Override
      public boolean isEnabled() {
        return super.isEnabled() && XpUtil.getPlayerXP(Minecraft.getMinecraft().player) >= XpUtil.getExperienceForLevel(10);
      }
    };
    pp.setSize(bw, bw);
    pp.setIconMargin(2, 2);
    pp.setToolTip(new GuiToolTip(pp.getBounds(), Lang.GUI_XP_STORE_10_1.get(), Lang.GUI_XP_STORE_10_2.get()) {
      @Override
      protected void updateText() {
        if (text.size() == 3) {
          text.remove(2);
        }
        if (XpUtil.getPlayerXP(Minecraft.getMinecraft().player) <= 0) {
          text.add(Lang.GUI_XP_STORE_EMPTY.get());
        }
      };
    });

    x += spacing + bw;
    ppp = new IconButton(this, 802, x, y, IconEIO.TRIPLE_PLUS) {
      @Override
      public boolean isEnabled() {
        return super.isEnabled() && XpUtil.getPlayerXP(Minecraft.getMinecraft().player) > 0;
      }
    };
    ppp.setSize(bw, bw);
    ppp.setIconMargin(2, 2);
    ppp.setToolTip(new GuiToolTip(ppp.getBounds(), Lang.GUI_XP_STORE_ALL_1.get(), Lang.GUI_XP_STORE_ALL_2.get()) {
      @Override
      protected void updateText() {
        if (text.size() == 3) {
          text.remove(2);
        }
        if (XpUtil.getPlayerXP(Minecraft.getMinecraft().player) <= 0) {
          text.add(Lang.GUI_XP_STORE_EMPTY.get());
        }
      };
    });

    x = startX;
    y = 75;
    m = new IconButton(this, 803, x, y, IconEIO.SINGLE_MINUS) {
      @Override
      public boolean isEnabled() {
        return super.isEnabled() && getTileEntity().getContainer().getExperienceTotal() >= XpUtil.getExperienceForLevel(1);
      }
    };
    m.setSize(bw, bw);
    m.setIconMargin(2, 2);
    m.setToolTip(new GuiToolTip(m.getBounds(), Lang.GUI_XP_RETR_1_1.get(), Lang.GUI_XP_RETR_1_2.get()) {
      @Override
      protected void updateText() {
        if (text.size() == 3) {
          text.remove(2);
        }
        if (getTileEntity().getContainer().getExperienceTotal() <= 0) {
          text.add(Lang.GUI_XP_RETR_EMPTY.get());
        }
      };
    });

    x += spacing + bw;
    mm = new IconButton(this, 804, x, y, IconEIO.DOUBLE_MINUS) {
      @Override
      public boolean isEnabled() {
        return super.isEnabled() && getTileEntity().getContainer().getExperienceTotal() >= XpUtil.getExperienceForLevel(10);
      }
    };
    mm.setSize(bw, bw);
    mm.setIconMargin(2, 2);
    mm.setToolTip(new GuiToolTip(mm.getBounds(), Lang.GUI_XP_RETR_10_1.get(), Lang.GUI_XP_RETR_10_2.get()) {
      @Override
      protected void updateText() {
        if (text.size() == 3) {
          text.remove(2);
        }
        if (getTileEntity().getContainer().getExperienceTotal() <= 0) {
          text.add(Lang.GUI_XP_RETR_EMPTY.get());
        }
      };
    });

    x += spacing + bw;
    mmm = new IconButton(this, 805, x, y, IconEIO.TRIPLE_MINUS) {
      @Override
      public boolean isEnabled() {
        return super.isEnabled() && getTileEntity().getContainer().getExperienceTotal() > 0;
      }
    };
    mmm.setSize(bw, bw);
    mmm.setIconMargin(2, 2);
    mmm.setToolTip(new GuiToolTip(mmm.getBounds(), Lang.GUI_XP_RETR_ALL_1.get(), Lang.GUI_XP_RETR_ALL_2.get(), "") {
      @Override
      protected void updateText() {
        if (getTileEntity().getContainer().getExperienceTotal() <= 0) {
          text.set(2, Lang.GUI_XP_RETR_EMPTY.get());
        } else {
          text.set(2, Lang.GUI_XP_RETR_ALL_3
              .get(XpUtil.getLevelForExperience(getTileEntity().getContainer().getExperienceTotal() + XpUtil.getPlayerXP(Minecraft.getMinecraft().player))));
        }
      }
    });
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
    if (b == p) {
      doDrainXP(1);
    } else if (b == pp) {
      doDrainXP(10);
    } else if (b == ppp) {
      doDrainXP(5000);
    } else if (b == m) {
      doAddXP(1);
    } else if (b == mm) {
      doAddXP(10);
    } else if (b == mmm) {
      doAddXP(5000);
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
