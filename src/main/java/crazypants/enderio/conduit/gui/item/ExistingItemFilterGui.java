package crazypants.enderio.conduit.gui.item;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.ExistingItemFilter;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.network.PacketHandler;

public class ExistingItemFilterGui implements IItemFilterGui {

  private static final int ID_NBT = GuiExternalConnection.nextButtonId();
  private static final int ID_META = GuiExternalConnection.nextButtonId();
  private static final int ID_ORE_DICT = GuiExternalConnection.nextButtonId();
  private static final int ID_STICKY = GuiExternalConnection.nextButtonId();

  private static final int ID_SNAPSHOT = GuiExternalConnection.nextButtonId();
  private static final int ID_CLEAR = GuiExternalConnection.nextButtonId();
  private static final int ID_SHOW = GuiExternalConnection.nextButtonId();
  private static final int ID_MERGE = GuiExternalConnection.nextButtonId();


  private IItemConduit itemConduit;
  private GuiExternalConnection gui;

  private ToggleButton useMetaB;
  private ToggleButton useNbtB;
  private ToggleButton useOreDictB;
  private ToggleButton stickyB;

  private final IconButton whiteListB;

  private GuiButton snapshotB;
  private GuiButton clearB;
  private GuiButton showB;
  private GuiButton mergeB;
  private SnapshotOverlay snapshotOverlay;

  boolean isInput;
  private int buttonIdOffset = 0;

  private ExistingItemFilter filter;

  public ExistingItemFilterGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    this.gui = gui;
    this.itemConduit = itemConduit;
    this.isInput = isInput;
    this.buttonIdOffset = isInput ? 0 : 256;

    if(isInput) {
      filter = (ExistingItemFilter) itemConduit.getInputFilter(gui.getDir());
    } else {
      filter = (ExistingItemFilter) itemConduit.getOutputFilter(gui.getDir());
    }

    int butLeft = isInput ? 104 : 6;
    int x = butLeft;
    int y = 96;

    useMetaB = new ToggleButton(gui, ID_META + buttonIdOffset, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    useMetaB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchMetaData"));
    useMetaB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreMetaData"));
    useMetaB.setPaintSelectedBorder(false);

    x += 16;
    whiteListB = new IconButton(gui, -1, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));

    x += 16;
    stickyB = new ToggleButton(gui, ID_STICKY + buttonIdOffset, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    String[] lines = EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled");
    stickyB.setSelectedToolTip(lines);
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    if(!isInput) x += 16;
    useNbtB = new ToggleButton(gui, ID_NBT + buttonIdOffset, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
    useNbtB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchNBT"));
    useNbtB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreNBT"));
    useNbtB.setPaintSelectedBorder(false);

    x += 16;
    useOreDictB = new ToggleButton(gui, ID_ORE_DICT + buttonIdOffset, x, y, IconEIO.FILTER_ORE_DICT_OFF, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicEnabled"));
    useOreDictB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicDisabled"));
    useOreDictB.setPaintSelectedBorder(false);

    snapshotB = new GuiButton(ID_SNAPSHOT + buttonIdOffset, 0, 0, 56, 20, EnderIO.lang.localize("gui.conduit.button.snap"));
    mergeB = new GuiButton(ID_MERGE + buttonIdOffset, 0, 0, 40, 20, EnderIO.lang.localize("gui.conduit.button.merge"));
    clearB = new GuiButton(ID_CLEAR + buttonIdOffset, 0, 0, 56, 20, EnderIO.lang.localize("gui.conduit.button.clear"));
    showB = new GuiButton(ID_SHOW + buttonIdOffset, 0, 0, 40, 20, EnderIO.lang.localize("gui.conduit.button.show"));

    snapshotOverlay = new SnapshotOverlay();
    gui.addOverlay(snapshotOverlay);


  }

  @Override
  public void mouseClicked(int x, int y, int par3) {
  }

  @Override
  public void updateButtons() {

    ExistingItemFilter activeFilter = filter;

    useNbtB.onGuiInit();
    useNbtB.setSelected(activeFilter.isMatchNBT());

    useOreDictB.onGuiInit();
    useOreDictB.setSelected(activeFilter.isUseOreDict());

    if(!isInput) {
      stickyB.onGuiInit();
      stickyB.setSelected(activeFilter.isSticky());
    }

    useMetaB.onGuiInit();
    useMetaB.setSelected(activeFilter.isMatchMeta());

    whiteListB.onGuiInit();
    if (filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.blacklist"));
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));
    }

    int butLeft = gui.getGuiLeft() + (isInput ? 104 : 5);
    int x = butLeft;
    int y = gui.getGuiTop() + 96 + 20;

    snapshotB.xPosition = x;
    snapshotB.yPosition = y;

    x += 56;
    mergeB.xPosition = x;
    mergeB.yPosition = y;

    x = butLeft;
    y += 20;
    clearB.xPosition = x;
    clearB.yPosition = y;

    x += 56;
    showB.xPosition = x;
    showB.yPosition = y;

    clearB.enabled = filter.getSnapshot() != null;
    showB.enabled = clearB.enabled;
    mergeB.enabled = filter.getSnapshot() != null;

    gui.addButton(snapshotB);
    gui.addButton(clearB);
    gui.addButton(showB);
    gui.addButton(mergeB);
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    if(guiButton.id == ID_META + buttonIdOffset) {
      filter.setMatchMeta(useMetaB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_NBT + buttonIdOffset) {
      filter.setMatchNBT(useNbtB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_STICKY + buttonIdOffset) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_ORE_DICT + buttonIdOffset) {
      filter.setUseOreDict(useOreDictB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_SNAPSHOT + buttonIdOffset) {
      sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode.SET);
    } else if(guiButton.id == ID_CLEAR + buttonIdOffset) {
      sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode.CLEAR);
    } else if(guiButton.id == ID_MERGE + buttonIdOffset) {
      sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode.MERGE);
    } else if(guiButton.id == ID_SHOW + buttonIdOffset) {
      showSnapshotOverlay();
    } else if (guiButton == whiteListB) {
      filter.setBlacklist(!filter.isBlacklist());
      sendSnapshotPacket(filter.isBlacklist() ? PacketExistingItemFilterSnapshot.Opcode.SET_BLACK
          : PacketExistingItemFilterSnapshot.Opcode.UNSET_BLACK);
    }
  }

  private void showSnapshotOverlay() {
    snapshotOverlay.setVisible(true);
  }

  private void sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode opcode) {
    PacketHandler.INSTANCE.sendToServer(new PacketExistingItemFilterSnapshot(itemConduit, gui.getDir(),isInput,opcode));
  }

  private void sendFilterChange() {
    updateButtons();
    PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
  }

  @Override
  public void deactivate() {
    useNbtB.detach();
    useMetaB.detach();
    useOreDictB.detach();
    stickyB.detach();
    whiteListB.detach();
    gui.removeButton(snapshotB);
    gui.removeButton(clearB);
    gui.removeButton(showB);
    gui.removeButton(mergeB);
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
//    GL11.glColor3f(1, 1, 1);
//    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
//    gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 68, 0, 238, 18 * 5, 18);
//    if(filter.isAdvanced()) {
//      gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 86, 0, 238, 18 * 5, 18);
//    }
  }

  class SnapshotOverlay implements IGuiOverlay {

    boolean visible;

    @Override
    public void init(IGuiScreen screen) {
    }

    @Override
    public Rectangle getBounds() {
      return new Rectangle(0,0,gui.width,gui.height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTick) {
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glEnable(GL11.GL_BLEND);
      RenderUtil.renderQuad2D(4, 4, 0, gui.getXSize() - 9, gui.getYSize() - 8, new Vector4f(0,0,0,1));
      RenderUtil.renderQuad2D(6, 6, 0, gui.getXSize() - 13, gui.getYSize() - 12, new Vector4f(0.6,0.6,0.6,1));

      Minecraft mc = Minecraft.getMinecraft();
      RenderItem itemRenderer = new RenderItem();

      GL11.glEnable(GL11.GL_DEPTH_TEST);

      List<ItemStack> snapshot = filter.getSnapshot();
      int x = 15;
      int y = 10;
      int count = 0;
      for(ItemStack st : snapshot) {
        if(st != null) {
          itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), st, x, y);
          //itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), st, x, y, s);
        }
        x += 20;
        count++;
        if(count % 9 == 0) {
          x = 15;
          y += 20;
        }
      }
    }

    @Override
    public void setVisible(boolean visible) {
      this.visible = visible;
    }

    @Override
    public boolean isVisible() {
      return visible;
    }

    @Override
    public boolean handleMouseInput(int x, int y, int b) {
      return true;
    }

    @Override
    public boolean isMouseInBounds(int mouseX, int mouseY) {
      return getBounds().contains(mouseX, mouseY);
    }

  }
}
