package crazypants.enderio.machines.machine.alloy;

import java.awt.Rectangle;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;

public abstract class GuiAlloySmelter<T extends TileAlloySmelter> extends GuiInventoryMachineBase<T> implements IAlloySmelterRemoteExec.GUI {

  @SuppressWarnings("unchecked")
  public static @Nonnull <E extends TileAlloySmelter> GuiAlloySmelter<E> create(@Nonnull InventoryPlayer playerInv, @Nonnull E te) {
    if (te instanceof TileAlloySmelter.Simple) {
      return (GuiAlloySmelter<E>) new Simple(playerInv, (TileAlloySmelter.Simple) te);
    } else if (te instanceof TileAlloySmelter.Furnace) {
      return (GuiAlloySmelter<E>) new Furnace(playerInv, (TileAlloySmelter.Furnace) te);
    } else {
      return (GuiAlloySmelter<E>) new Normal(playerInv, te);
    }
  }

  public static class Normal extends GuiAlloySmelter<TileAlloySmelter> {
    public Normal(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileAlloySmelter furnaceInventory) {
      super(par1InventoryPlayer, furnaceInventory, MODE.AUTO);
    }
  }

  public static class Simple extends GuiAlloySmelter<TileAlloySmelter.Simple> {
    public Simple(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileAlloySmelter.Simple furnaceInventory) {
      super(par1InventoryPlayer, furnaceInventory, MODE.SIMPLE_ALLOY);
    }
  }

  public static class Furnace extends GuiAlloySmelter<TileAlloySmelter.Furnace> {
    public Furnace(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileAlloySmelter.Furnace furnaceInventory) {
      super(par1InventoryPlayer, furnaceInventory, MODE.SIMPLE_FURNACE);
    }
  }

  static enum MODE {
    SIMPLE_ALLOY,
    SIMPLE_FURNACE,
    ALLOY {
      @Override
      @Nonnull
      IWidgetIcon getIcon() {
        return IconEIO.ALLOY_MODE_ALLOY;
      }

      @Override
      @Nonnull
      Lang getLang() {
        return Lang.GUI_ALLOY_MODE_ALLOY;
      }
    },
    FURNACE {
      @Override
      @Nonnull
      IWidgetIcon getIcon() {
        return IconEIO.ALLOY_MODE_FURNACE;
      }

      @Override
      @Nonnull
      Lang getLang() {
        return Lang.GUI_ALLOY_MODE_FURNACE;
      }
    },
    AUTO;

    boolean isSimple() {
      return this == MODE.SIMPLE_ALLOY || this == SIMPLE_FURNACE;
    }

    @Nonnull
    IWidgetIcon getIcon() {
      return IconEIO.ALLOY_MODE_BOTH;
    }

    @Nonnull
    Lang getLang() {
      return Lang.GUI_ALLOY_MODE_ALL;
    }
  }

  private final @Nonnull IconButton vanillaFurnaceButton;
  private final @Nonnull GuiToolTip vanillaFurnaceTooltip;
  protected @Nonnull MODE mode;

  protected static final int SMELT_MODE_BUTTON_ID = 76;

  public GuiAlloySmelter(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull T furnaceInventory, @Nonnull MODE mode) {
    super(furnaceInventory, ContainerAlloySmelter.create(par1InventoryPlayer, furnaceInventory), "simple_alloy_smelter", "simple_furnace",
        "alloy_smelter_alloy", "alloy_smelter_furnace", "alloy_smelter_auto");

    this.mode = mode;

    vanillaFurnaceTooltip = new GuiToolTip(new Rectangle(xSize - 5 - BUTTON_SIZE, 62, BUTTON_SIZE, BUTTON_SIZE), (String[]) null);
    vanillaFurnaceTooltip.setIsVisible(!mode.isSimple());

    vanillaFurnaceButton = new IconButton(this, SMELT_MODE_BUTTON_ID, guiLeft + vanillaFurnaceTooltip.getBounds().x,
        guiTop + vanillaFurnaceTooltip.getBounds().y, mode.getIcon());
    vanillaFurnaceButton.visible = !mode.isSimple();
    vanillaFurnaceButton.setIconMargin(2, 2); // hack because icon size is bad

    redstoneButton.setIsVisible(!mode.isSimple());

    addProgressTooltip(55, 35, 14, 14);
    addProgressTooltip(103, 35, 14, 14);

    addDrawingElement(new PowerBar(furnaceInventory, this));
  }

  private MODE getMode() {
    if (mode.isSimple()) {
      return mode;
    }
    switch (getTileEntity().getMode()) {
    case ALL:
      return MODE.AUTO;
    case ALLOY:
      return MODE.ALLOY;
    case FURNACE:
      return MODE.FURNACE;
    default:
      throw new RuntimeException("Just found out that black is smellier than the sound of hot!");
    }
  }

  @Override
  public void initGui() {
    super.initGui();

    vanillaFurnaceButton.x = guiLeft + vanillaFurnaceTooltip.getBounds().x;
    vanillaFurnaceButton.y = guiTop + vanillaFurnaceTooltip.getBounds().y;

    buttonList.add(vanillaFurnaceButton);
    addToolTip(vanillaFurnaceTooltip);

    updateVanillaFurnaceButton();
  }

  @Override
  protected void renderSlotHighlight(int slot, @Nonnull Vector4f col) {
    if (getTileEntity().getSlotDefinition().isOutputSlot(slot)) {
      renderSlotHighlight(col, 75, 54, 24, 24);
    } else {
      super.renderSlotHighlight(slot, col);
    }
  }

  @Override
  protected void mouseClicked(int x, int y, int button) throws IOException {
    if (button == 1 && vanillaFurnaceButton.isMouseOver() && !mode.isSimple()) {
      // um, why do we need this?
      Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      actionPerformed(vanillaFurnaceButton, 1);
    }
    super.mouseClicked(x, y, button);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton par1GuiButton) throws IOException {
    actionPerformed(par1GuiButton, 0);
  }

  private void actionPerformed(GuiButton button, int mbutton) throws IOException {
    if (button.id == SMELT_MODE_BUTTON_ID) {
      doSetMode(mbutton == 0 ? getTileEntity().getMode().next() : getTileEntity().getMode().prev());
    } else {
      super.actionPerformed(button);
    }
  }

  private void updateVanillaFurnaceButton() {
    vanillaFurnaceButton.setIcon(getMode().getIcon());
    vanillaFurnaceTooltip.setToolTipText(Lang.GUI_ALLOY_MODE.get(), getMode().getLang().get());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    updateVanillaFurnaceButton();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture(getMode().ordinal());
    int sx = guiLeft;
    int sy = guiTop;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    if (shouldRenderProgress()) {
      int scaled = getProgressScaled(14) + 1;
      drawTexturedModalRect(sx + 55, sy + 49 - scaled, 176, 14 - scaled, 14, scaled);
      drawTexturedModalRect(sx + 103, sy + 49 - scaled, 176, 14 - scaled, 14, scaled);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
}
