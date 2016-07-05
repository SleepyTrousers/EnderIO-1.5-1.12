package crazypants.enderio.machine.killera;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.SoundUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.ExperienceBarRenderer;
import crazypants.enderio.xp.PacketGivePlayerXP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class GuiKillerJoe extends GuiMachineBase<TileKillerJoe> {

  private IconButton m;
  private IconButton mm;
  private IconButton mmm;
  private ToggleButton showRangeB;

  public GuiKillerJoe(InventoryPlayer inventory, final TileKillerJoe tileEntity) {
    super(tileEntity, new ContainerKillerJoe(inventory, tileEntity), "killerJoe");

    addToolTip(new GuiToolTip(new Rectangle(18, 11, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("killerJoe.fuelTank");
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().tank));
        if(tileEntity.tank.getFluidAmount() < tileEntity.getActivationAmount()) {
          text.add(EnderIO.lang.localize("gui.fluid.minReq", tileEntity.getActivationAmount() + Fluids.MB()));
        }
      }

    });

    int spacing = 5;
    int bw = 16;

    int x = 81;
    int y = 44;

    m = new IconButton(this, 803, x, y, IconEIO.SINGLE_MINUS);
    m.setSize(bw, bw);
    m.setToolTip(EnderIO.lang.localize("gui.machine.button.retrievelevel"), EnderIO.lang.localize("gui.machine.tooltip.retrievelevel"));

    x += spacing + bw;
    mm = new IconButton(this, 804, x, y, IconEIO.DOUBLE_MINUS);
    mm.setSize(bw, bw);
    mm.setToolTip(EnderIO.lang.localize("gui.machine.button.retrievelevels"), EnderIO.lang.localize("gui.machine.tooltip.retrievelevels"));

    x += spacing + bw;
    mmm = new IconButton(this, 805, x, y, IconEIO.TRIPLE_MINUS);
    mmm.setSize(bw, bw);
    mmm.setToolTip(EnderIO.lang.localize("gui.machine.button.retrieveall"), EnderIO.lang.localize("gui.machine.tooltip.retrieveall"));

    x = getXSize() - 5 - BUTTON_SIZE;
    showRangeB = new ToggleButton(this, -1, x, 44, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public List<String> getToolTipText() {
        return Lists.newArrayList(EnderIO.lang.localize(showRangeB.isSelected() ? "gui.spawnGurad.hideRange" : "gui.spawnGurad.showRange"));
      }
    });

  }

  @Override
  public void initGui() {
    super.initGui();
    m.onGuiInit();
    mm.onGuiInit();
    mmm.onGuiInit();
    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());
    ((ContainerKillerJoe) inventorySlots).createGhostSlots(getGhostSlots());
  }

  @Override
  protected void actionPerformed(GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b == m) {
      PacketHandler.INSTANCE.sendToServer(new PacketGivePlayerXP(getTileEntity(), 1));
      SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.experience_orb.pickup"));
      SoundUtil.playClientSoundFX(soundEvent, getTileEntity());
    } else if (b == mm) {
      PacketHandler.INSTANCE.sendToServer(new PacketGivePlayerXP(getTileEntity(), 10));
      SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.experience_orb.pickup"));
      SoundUtil.playClientSoundFX(soundEvent, getTileEntity());
    } else if (b == mmm) {
      PacketHandler.INSTANCE.sendToServer(new PacketGivePlayerXP(getTileEntity(), 5000));
      SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.experience_orb.pickup"));
      SoundUtil.playClientSoundFX(soundEvent, getTileEntity());
    } else if (b == showRangeB) {
      getTileEntity().setShowRange(showRangeB.isSelected());
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    if(mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 16;
      int y = 9;
      int w = 15 + 4;
      int h = 47 + 4;
      renderSlotHighlight(PULL_COLOR, x, y, w, h);
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    int x = guiLeft + 18;
    int y = guiTop + 11;
    TileKillerJoe joe = getTileEntity();
    if(joe.tank.getFluidAmount() > 0) {
      RenderUtil.renderGuiTank(joe.tank.getFluid(), joe.tank.getCapacity(), joe.tank.getFluidAmount(), x, y, zLevel, 16, 47);
    }
    ExperienceBarRenderer.render(this, sx + 77, sy + 30, 66, joe.getContainer());
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
