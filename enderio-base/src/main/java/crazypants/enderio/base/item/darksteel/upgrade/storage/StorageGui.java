package crazypants.enderio.base.item.darksteel.upgrade.storage;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StorageGui extends GuiContainerBaseEIO implements StorageRemoteExec.GUI {

  private final @Nonnull StorageContainer cont;

  public StorageGui(@Nonnull StorageContainer par1Container) {
    super(par1Container, "inventory_9x6");
    this.cont = par1Container;
    ySize = 206;
  }

  @Override
  protected boolean doSwitchTab(int tab) {
    setTab(cont.activeTab = NullHelper.first(StorageData.ARMOR[tab], EntityEquipmentSlot.HEAD));
    return true;
  }

  private boolean hasSetTab = false;

  private void setInitialTab() {
    if (!hasSetTab) {
      hasSetTab = true;
      for (EntityEquipmentSlot drawTab : StorageData.ARMOR) {
        if (cont.getItemHandler().getHandlerFromIndex(drawTab.getIndex()).getSlots() > 0) {
          setTab(cont.activeTab = drawTab);
          return;
        }
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    setInitialTab();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    int cols = StorageData.cols(cont.activeTab);
    int rows = cont.getItemHandler().getHandlerFromIndex(cont.activeTab.getIndex()).getSlots() / cols;

    RenderUtil.bindTexture(EnderIO.proxy.getGuiTexture("inventory_" + cols + "x" + rows));
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    startTabs();
    for (EntityEquipmentSlot drawTab : StorageData.ARMOR) {
      if (cont.getItemHandler().getHandlerFromIndex(drawTab.getIndex()).getSlots() > 0) {
        renderStdTab(sx, sy, 3 - drawTab.getIndex(), Minecraft.getMinecraft().player.inventory.armorInventory.get(drawTab.getIndex()),
            drawTab == cont.activeTab);
      }
    }

  }

}
