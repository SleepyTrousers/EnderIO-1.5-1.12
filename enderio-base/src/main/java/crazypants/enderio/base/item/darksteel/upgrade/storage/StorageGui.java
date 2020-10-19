package crazypants.enderio.base.item.darksteel.upgrade.storage;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.handler.KeyTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StorageGui extends GuiContainerBaseEIO<StorageContainer> implements StorageContainerProxy {

  public StorageGui(@Nonnull StorageContainer par1Container) {
    super(par1Container, par1Container, "inventory_9x6");
    ySize = 206;
  }

  @Override
  protected boolean doSwitchTab(int tab) {
    setTab(getOwner().activeTab = NullHelper.first(StorageUpgrade.ARMOR[tab], EntityEquipmentSlot.HEAD));
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
      for (EntityEquipmentSlot drawTab : StorageUpgrade.ARMOR) {
        if (getOwner().getItemHandler().getHandlerFromIndex(drawTab.getIndex()).getSlots() > 0) {
          if (found == null || drawTab == EntityEquipmentSlot.CHEST) {
            found = drawTab;
          }
        }
      }
      if (found != null) {
        setTab(getOwner().activeTab = found);
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    setInitialTab();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    int cols = StorageUpgrade.cols(getOwner().activeTab);
    int rows = getOwner().getItemHandler().getHandlerFromIndex(getOwner().activeTab.getIndex()).getSlots() / cols;

    // Should we construct this from a single texture instead of having pre-baked textures for each size?
    // Pre-baked is probably better for texture packs...
    RenderUtil.bindTexture(EnderIO.proxy.getGuiTexture("inventory_" + cols + "x" + rows));
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    startTabs();
    for (int i = 0; i < StorageUpgrade.ARMOR.length; i++) {
      EntityEquipmentSlot drawTab = StorageUpgrade.ARMOR[i];
      if (getOwner().getItemHandler().getHandlerFromIndex(drawTab.getIndex()).getSlots() > 0) {
        renderStdTab(sx, sy, i, Minecraft.getMinecraft().player.inventory.armorInventory.get(drawTab.getIndex()), drawTab == getOwner().activeTab);
      }
    }
  }

  @Override
  protected void keyTyped(char c, int key) throws IOException {
    if (key == KeyTracker.inventory.getBinding().getKeyCode()) {
      if (!hideOverlays()) {
        this.mc.player.closeScreen();
      }
      return;
    }
    super.keyTyped(c, key);
  }

}
