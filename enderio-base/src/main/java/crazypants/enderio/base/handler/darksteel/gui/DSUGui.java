package crazypants.enderio.base.handler.darksteel.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.handler.KeyTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DSUGui extends GuiContainerBaseEIO implements DSURemoteExec.GUI {

  private final @Nonnull DSUContainer cont;

  public DSUGui(@Nonnull DSUContainer par1Container) {
    super(par1Container, "dsu");
    this.cont = par1Container;
    ySize = 206;
  }

  @Override
  public void initGui() {
    super.initGui();
    cont.createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected boolean doSwitchTab(int tab) {
    setTab(cont.activeTab = slotFromID(tab));
    return true;
  }

  private boolean hasSetTab = false;

  /**
   * Make sure client and server agree on which tab is open initially. Prefer the chest tab as it is the biggest (with default config).
   * <p>
   * This cannot run in the constructor as the GUID is not yet available there to send the network packet.
   */
  private void setInitialTab() {
    if (!hasSetTab) {
      hasSetTab = true;
      EntityEquipmentSlot found = null;
      for (EntityEquipmentSlot drawTab : EntityEquipmentSlot.values()) {
        if (cont.getItemHandler().getHandlerFromIndex(drawTab.getIndex()).getSlots() > 0) {
          if (found == null || drawTab == EntityEquipmentSlot.CHEST) {
            found = drawTab;
          }
        }
      }
      if (found != null) {
        setTab(cont.activeTab = found);
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    setInitialTab();

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    bindGuiTexture();
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    // we have a dark gray background for the ghostslot grayout, let's overpaint it with light gray
    drawTexturedModalRect(guiLeft, guiTop + 5, 0, 220, xSize, 36);
    drawTexturedModalRect(guiLeft, guiTop + 26, 0, 220, xSize, 36);
    drawTexturedModalRect(guiLeft, guiTop + 62, 0, 220, xSize, 36);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    for (GhostSlot slot : getGhostSlotHandler().getGhostSlots()) {
      if (slot instanceof DSUContainer.UpgradeSlot && slot.isVisible()) {
        drawTexturedModalRect(guiLeft + slot.getX() - 1, guiTop + slot.getY() - 1, 200, 0, 18, 18);
      }
    }

    startTabs();
    for (EntityEquipmentSlot drawTab : EntityEquipmentSlot.values()) {
      if (cont.getItemHandler().getHandlerFromIndex(drawTab.getSlotIndex()).getSlots() > 0) {
        renderStdTab(guiLeft, guiTop, idFromSlot(drawTab), Minecraft.getMinecraft().player.getItemStackFromSlot(drawTab), drawTab == cont.activeTab);
      }
    }

    fontRenderer.drawString("Storage", guiLeft + 7, guiTop + 99 - 11, 4210752);
    fontRenderer.drawString("WIP - may break horribly!", guiLeft + 7 + 15, guiTop + 99 - 11 - 9, 0xff0000);
  }

  @Override
  protected void keyTyped(char c, int key) throws IOException {
    if (key == KeyTracker.dsu.getKeyCode()) {
      if (!hideOverlays()) {
        this.mc.player.closeScreen();
      }
      return;
    }
    super.keyTyped(c, key);
  }

  public static @Nonnull EntityEquipmentSlot slotFromID(int id) {
    switch (id) {
    case 0:
      return EntityEquipmentSlot.MAINHAND;
    case 1:
      return EntityEquipmentSlot.HEAD;
    case 2:
      return EntityEquipmentSlot.CHEST;
    case 3:
      return EntityEquipmentSlot.LEGS;
    case 4:
      return EntityEquipmentSlot.FEET;
    default:
    case 5:
      return EntityEquipmentSlot.OFFHAND;
    }
  }

  public static int idFromSlot(@Nonnull EntityEquipmentSlot slot) {
    switch (slot) {
    case CHEST:
      return 2;
    case FEET:
      return 4;
    case HEAD:
      return 1;
    case LEGS:
      return 3;
    case MAINHAND:
      return 0;
    default:
    case OFFHAND:
      return 5;
    }
  }

}
