package crazypants.enderio.base.handler.darksteel.gui;

import java.awt.Rectangle;
import java.io.IOException;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.handler.KeyTracker;
import crazypants.enderio.base.handler.darksteel.gui.DSUContainer.AutoSlot;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import crazypants.enderio.util.AtlasWidgetIcon;
import crazypants.enderio.util.EnumReader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DSUGui extends GuiContainerBaseEIO implements DSURemoteExec.GUI {

  private final static NNList<String> TEXTURES = new NNList<>(EnderIO.DOMAIN + ":items/paint_overlay", "minecraft:items/empty_armor_slot_shield");

  static {
    TEXTURES.addAll(ItemArmor.EMPTY_SLOT_NAMES);
  }

  private final DSUContainer cont;
  private final int initialTab;
  private final AnvilSubGui anvil;

  public DSUGui(DSUContainer par1Container, int initialTab) {
    super(par1Container, "dsu");
    this.cont = par1Container;
    this.initialTab = initialTab;
    this.anvil = new AnvilSubGui(par1Container, this);
    ySize = 206;
  }

  @Override
  public void initGui() {
    super.initGui();
    anvil.initGui();
    cont.createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected boolean doSwitchTab(int tab) {
    ISlotSelector oldTab = cont.activeTab;
    setTab(cont.setTab(tab));
    if (oldTab == cont.activeTab) {
      return false;
    }
    if (cont.activeTab.isItem()) {
      // allow click-through to the slot
      SoundHelper.playSound(mc.world, mc.player, SoundRegistry.TAB_SWITCH, 1, 1);
      return false;
    }
    return true;
  }

  private boolean hasSetTab = false;

  /**
   * Make sure client and server agree on which tab is open initially. Prefer the chest tab (for no reason).
   * <p>
   * This cannot run in the constructor as the GUID is not yet available there to send the network packet.
   */
  private void setInitialTab() {
    if (!hasSetTab) {
      hasSetTab = true;
      EntityEquipmentSlot preferedSlot = initialTab > -1 ? EnumReader.get(EntityEquipmentSlot.class, initialTab) : EntityEquipmentSlot.CHEST;
      boolean wantAnvil = initialTab == -2;
      int found = -1;
      for (UpgradeCap cap : cont.caps) {
        if (cap.isAvailable()) {
          if (wantAnvil && cap.getSlotSelector().isAnvil()) {
            found = cap.getSlotSelector().getTabOrder();
            break;
          }
          if (found < 0 || (cap.getSlotSelector().isSlot() && cap.getSlotSelector().getSlot() == preferedSlot)) {
            found = cap.getSlotSelector().getTabOrder();
          }
        }
      }
      if (found >= 0) {
        setTab(cont.setTab(found));
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    setInitialTab();
    cont.calcSlots();

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    bindGuiTexture();
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    // we have a dark gray background for the ghostslot grayout, let's overpaint it with light gray
    drawScaledCustomSizeModalRect(guiLeft, guiTop + 5, 0, 220, xSize, 1, xSize, 111, 256, 256);

    if (cont.activeTab.isAnvil()) {
      anvil.drawGuiContainerBackgroundLayer(par1, par2, par3);
    } else {
      boolean hasAnySlots = false;
      for (Slot inventorySlot : cont.inventorySlots) {
        if (inventorySlot instanceof DSUContainer.AutoSlot) {
          final AutoSlot slot = (DSUContainer.AutoSlot) inventorySlot;
          if (slot.isEnabled()) {
            hasAnySlots = true;
            if (slot.isHead()) {
              drawTexturedModalRect(guiLeft + slot.getX() - 1 - 6, guiTop + slot.getY() - 1, 200, 18, 6, 18);
            }
            if (slot.isLocked()) {
              drawTexturedModalRect(guiLeft + slot.getX() - 1, guiTop + slot.getY() - 1, 236, 0, 18, 18);
            } else if (slot.isBlocked()) {
              drawTexturedModalRect(guiLeft + slot.getX() - 1, guiTop + slot.getY() - 1, 218, 0, 18, 18);
            } else {
              drawTexturedModalRect(guiLeft + slot.getX() - 1, guiTop + slot.getY() - 1, 200, 0, 18, 18);
            }
          }
        }
      }

      if (hasAnySlots) {
        fontRenderer.drawString(Lang.GUI_DSU_STORAGE.get(), guiLeft + 7, guiTop + 99 - 11, 4210752);
      } else {
        String str = (cont.activeTab.isItem() ? Lang.GUI_DSU_NOT_INSERTED : Lang.GUI_DSU_NOT_INSTALLED).get();
        int y = 0;
        for (String sub : fontRenderer.listFormattedStringToWidth(str, xSize)) {
          if (sub != null) {
            int stringWidth = fontRenderer.getStringWidth(sub);
            fontRenderer.drawString(sub, guiLeft + xSize / 2 - stringWidth / 2, guiTop + (ySize - 86) / 2 + y, 4210752);
            y += 9;
          }
        }
      }
    }

    drawTabs();

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  private void drawTabs() {
    startTabs();
    for (UpgradeCap cap : cont.caps) {
      if (cap.getSlotSelector().isItem()) {
        Rectangle tabarea = renderStdTab(guiLeft, guiTop, cap.getSlotSelector().getTabOrder(), cap.getSlotSelector() == cont.activeTab);
        Slot slot = cap.getSlotSelector().getContainerSlot();
        if (slot != null) {
          slot.xPos = tabarea.x + tabarea.width - 5 - 16 - guiLeft;
          slot.yPos = tabarea.y + 5 - guiTop;
          bindGuiTexture();
          drawTexturedModalRect(guiLeft + slot.xPos - 1, guiTop + slot.yPos - 1, 200, 0, 18, 18);
        }
      } else if (cap.isAvailable()) {
        renderStdTab(guiLeft, guiTop, cap.getSlotSelector().getTabOrder(), cap.getSlotSelector().getItem(mc.player), cap.getSlotSelector() == cont.activeTab);
      } else if (cap.getSlotSelector().isSlot()) {
        renderStdTab(guiLeft, guiTop, cap.getSlotSelector().getTabOrder(),
            new AtlasWidgetIcon(mc.getTextureMapBlocks().getAtlasSprite(TEXTURES.get(cap.getSlotSelector().getSlot().ordinal()))),
            cap.getSlotSelector() == cont.activeTab);
      }
    }
  }

  @Override
  protected void keyTyped(char c, int key) throws IOException {
    if (cont.activeTab.isAnvil() && anvil.keyTyped(c, key)) {
      return;
    }
    if (key == KeyTracker.dsu.getKeyCode()) {
      if (!hideOverlays()) {
        this.mc.player.closeScreen();
      }
      return;
    }
    super.keyTyped(c, key);
  }

  @Override
  protected void mouseClicked(int x, int y, int button) throws IOException {
    if (cont.activeTab.isAnvil()) {
      anvil.mouseClicked(x, y, button);
    }
    super.mouseClicked(x, y, button);
  }

  @Override
  protected void drawForegroundImpl(int mouseX, int mouseY) {
    if (cont.activeTab.isAnvil()) {
      anvil.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
    super.drawForegroundImpl(mouseX, mouseY);
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    anvil.onGuiClosed();
  }
}
