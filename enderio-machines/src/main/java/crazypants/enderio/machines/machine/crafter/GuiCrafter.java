package crazypants.enderio.machines.machine.crafter;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.integration.jei.IHaveGhostTargets;
import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiCrafter extends GuiCapMachineBase<TileCrafter> implements IHaveGhostTargets<GuiCrafter> {

  private static final int POWERX = 10;
  private static final int POWERY = 14;
  private static final int POWER_HEIGHT = 42;

  private final @Nonnull ToggleButton bufferSizeB;

  private final boolean isSimple;

  public GuiCrafter(@Nonnull InventoryPlayer playerInv, @Nonnull TileCrafter te) {
    super(te, ContainerCrafter.create(playerInv, te), "crafter", "simple_crafter");
    isSimple = te instanceof TileCrafter.Simple;
    xSize = getXSize();

    redstoneButton.setIsVisible(!isSimple);

    bufferSizeB = new ToggleButton(this, -1, recipeButton.x, recipeButton.y + 19, IconEIO.ITEM_SINGLE, IconEIO.ITEM_STACK);
    bufferSizeB.setSelectedToolTip(Lang.GUI_BUFFERING_STACK.get());
    bufferSizeB.setUnselectedToolTip(Lang.GUI_BUFFERING_SINGLE.get());
    bufferSizeB.setSelected(te.isBufferStacks());
    bufferSizeB.setIsVisible(!isSimple);

    addDrawingElement(new PowerBar(te.getEnergy(), this, POWERX, POWERY, POWER_HEIGHT));
  }

  @Override
  public void initGui() {
    super.initGui();
    bufferSizeB.onGuiInit();
    ((ContainerCrafter<?>) getInventory()).addCrafterSlots(getGhostSlotHandler());
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

  @SuppressWarnings("unchecked")
  @Override
  public boolean isSlotTarget(GhostSlot slot) {
    return ((ContainerCrafter<?>.DummySlot) slot).slotIndex < 9;
  }
}
