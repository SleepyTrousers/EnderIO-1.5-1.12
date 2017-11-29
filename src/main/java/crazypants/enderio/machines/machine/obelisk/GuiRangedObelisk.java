package crazypants.enderio.machines.machine.obelisk;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.base.machine.modes.IoMode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class GuiRangedObelisk extends GuiPoweredMachineBase<AbstractRangedTileEntity> {

  ToggleButton showRangeB;

  private static final int RANGE_ID = 8738924;

  public GuiRangedObelisk(InventoryPlayer par1InventoryPlayer, AbstractRangedTileEntity te) {
    this(par1InventoryPlayer, te, new ContainerAbstractObelisk(par1InventoryPlayer, te), "attractor");
  }
  
  public GuiRangedObelisk(InventoryPlayer par1InventoryPlayer, AbstractRangedTileEntity te, Container container, String texture) {
    super(te, container, texture);

    int x = getXSize() - 5 - BUTTON_SIZE;
    showRangeB = new ToggleButton(this, RANGE_ID, x, 44, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
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
    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());
    if(isGhostSlotsEnabled() && inventorySlots instanceof ContainerAbstractObelisk) {
      ((ContainerAbstractObelisk) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
    }
  }

  protected boolean isGhostSlotsEnabled() {
    return true;
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    int range = (int) getTileEntity().getRange();
    drawCenteredString(fontRenderer, EnderIO.lang.localize("gui.spawnGurad.range") + " " + range, getGuiLeft() + sx / 2 + 9, getGuiTop() + 68,
        ColorUtil.getRGB(Color.white));
  }

  @Override
  protected void actionPerformed(GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b.id == RANGE_ID) {
      getTileEntity().setShowRange(showRangeB.isSelected());
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

}
