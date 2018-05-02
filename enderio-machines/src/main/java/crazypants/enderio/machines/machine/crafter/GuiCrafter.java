package crazypants.enderio.machines.machine.crafter;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.gui.GuiButtonIoConfig;
import crazypants.enderio.base.machine.gui.GuiOverlayIoConfig;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiCrafter<T extends TileCrafter> extends GuiContainerBaseEIO {

  private static final int ID_REDSTONE_BUTTON = 139;
  private static final int ID_IO_MODE_BUTTON = 140;
  private static final int ID_BUFFER_BUTTON = 141;

  private static final int POWERX = 10;
  private static final int POWERY = 14;
  private static final int POWER_HEIGHT = 42;

  private final @Nonnull RedstoneModeButton<TileCrafter> rsB;
  private final @Nonnull GuiOverlayIoConfig<TileCrafter> configOverlay;
  private final @Nonnull GuiButtonIoConfig<TileCrafter> configB;

  private final @Nonnull TileCrafter te;

  private final @Nonnull ToggleButton bufferSizeB;

  private final boolean isSimple;

  public GuiCrafter(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(ContainerCrafter.create(playerInv, te), "crafter", "simple_crafter");
    this.te = te;
    isSimple = te instanceof TileCrafter.Simple;
    xSize = getXSize();

    int x = getXSize() - 7 - 16;
    int y = 14;

    rsB = new RedstoneModeButton<TileCrafter>(this, ID_REDSTONE_BUTTON, x, y, te);
    rsB.setIsVisible(!isSimple);

    y += 20;
    configOverlay = new GuiOverlayIoConfig<TileCrafter>(te);
    addOverlay(configOverlay);

    configB = new GuiButtonIoConfig<TileCrafter>(this, ID_IO_MODE_BUTTON, x, y, te, configOverlay);

    y += 20;
    bufferSizeB = new ToggleButton(this, ID_BUFFER_BUTTON, x, y, IconEIO.ITEM_SINGLE, IconEIO.ITEM_STACK);
    bufferSizeB.setSelectedToolTip(Lang.GUI_BUFFERING_STACK.get());
    bufferSizeB.setUnselectedToolTip(Lang.GUI_BUFFERING_SINGLE.get());
    bufferSizeB.setSelected(te.isBufferStacks());
    bufferSizeB.setIsVisible(!isSimple);

    addDrawingElement(new PowerBar(te.getEnergy(), this, POWERX, POWERY, POWER_HEIGHT));
    // recipeButton.setYOrigin(recipeButton.getBounds().y + 19);
  }

  @Override
  public void initGui() {
    super.initGui();
    bufferSizeB.onGuiInit();
    rsB.onGuiInit();
    configB.onGuiInit();
    ((ContainerCrafter) inventorySlots).addCrafterSlots(getGhostSlotHandler());
  }

  @Override
  protected void mouseClickMove(int mouseX, int mouseY, int button, long par4) {
    GhostSlot slot = getGhostSlotHandler().getGhostSlotAt(this, mouseX, mouseY);
    if (slot != null) {
      ItemStack st = Minecraft.getMinecraft().player.inventory.getItemStack();
      // don't replace already set slots while dragging an item
      if (st.isEmpty() || slot.getStack().isEmpty()) {
        slot.putStack(st, st.getCount());
      }
    }
    super.mouseClickMove(mouseX, mouseY, button, par4);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b == bufferSizeB) {
      getTileEntity().setBufferStacks(bufferSizeB.isSelected());
      GuiPacket.send(this, ContainerCrafter.EXEC_SET_BUFFER, bufferSizeB.isSelected());
    }
  }

  private TileCrafter getTileEntity() {
    return te;
  }

  @Override
  public final int getXSize() {
    return 220;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture(isSimple ? 1 : 0);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
