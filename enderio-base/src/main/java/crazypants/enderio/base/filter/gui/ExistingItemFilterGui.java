package crazypants.enderio.base.filter.gui;

import java.awt.Rectangle;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.button.TooltipButton;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.filters.ExistingItemFilter;
import crazypants.enderio.base.filter.network.PacketExistingItemFilterSnapshot;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ExistingItemFilterGui extends AbstractGuiItemFilter {

  private static final int ID_NBT = FilterGuiUtil.nextButtonId();
  private static final int ID_META = FilterGuiUtil.nextButtonId();
  private static final int ID_ORE_DICT = FilterGuiUtil.nextButtonId();
  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();

  private static final int ID_SNAPSHOT = FilterGuiUtil.nextButtonId();
  private static final int ID_CLEAR = FilterGuiUtil.nextButtonId();
  private static final int ID_SHOW = FilterGuiUtil.nextButtonId();
  private static final int ID_MERGE = FilterGuiUtil.nextButtonId();

  private final @Nonnull ContainerFilter filterContainer;

  private @Nonnull ToggleButton useMetaB;
  private @Nonnull ToggleButton useNbtB;
  private @Nonnull ToggleButton useOreDictB;
  private @Nonnull ToggleButton stickyB;

  private final @Nonnull IconButton whiteListB;

  private @Nonnull TooltipButton snapshotB;
  private @Nonnull GuiButton clearB;
  private @Nonnull GuiButton showB;
  private @Nonnull GuiButton mergeB;
  private @Nonnull SnapshotOverlay snapshotOverlay;
  final boolean isStickyModeAvailable;

  private @Nonnull ExistingItemFilter filter;

  // TODO Remove isInput
  public ExistingItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    super(playerInv, filterContainer, te);
    this.filterContainer = filterContainer;

    filter = (ExistingItemFilter) filterContainer.getItemFilter();

    isStickyModeAvailable = (filterContainer.filterIndex == FilterGuiUtil.INDEX_INPUT);

    int butLeft = 37;
    int x = butLeft;
    int y = 68;

    useMetaB = new ToggleButton(this, ID_META, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    useMetaB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchMetaData"));
    useMetaB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreMetaData"));
    useMetaB.setPaintSelectedBorder(false);

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    String[] lines = EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled");
    stickyB.setSelectedToolTip(lines);
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = butLeft;

    x += 20;
    useNbtB = new ToggleButton(this, ID_NBT, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
    useNbtB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchNBT"));
    useNbtB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreNBT"));
    useNbtB.setPaintSelectedBorder(false);

    x = butLeft;
    useOreDictB = new ToggleButton(this, ID_ORE_DICT, x, y, IconEIO.FILTER_ORE_DICT_OFF, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicEnabled"));
    useOreDictB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicDisabled"));
    useOreDictB.setPaintSelectedBorder(false);

    snapshotB = new TooltipButton(this, ID_SNAPSHOT, 80, 65, 60, 20, EnderIO.lang.localize("gui.conduit.button.snap"));
    mergeB = new GuiButton(ID_MERGE, 0, 0, 40, 20, EnderIO.lang.localize("gui.conduit.button.merge"));
    clearB = new GuiButton(ID_CLEAR, 0, 0, 60, 20, EnderIO.lang.localize("gui.conduit.button.clear"));
    showB = new GuiButton(ID_SHOW, 0, 0, 40, 20, EnderIO.lang.localize("gui.conduit.button.show"));

    snapshotB.setToolTip(EnderIO.lang.localizeList("gui.conduit.button.snap.tooltip"));

    x -= 20;
    whiteListB = new IconButton(this, -1, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));

    snapshotOverlay = new SnapshotOverlay();
    addOverlay(snapshotOverlay);

  }

  @Override
  public void updateButtons() {
    super.updateButtons();

    ExistingItemFilter activeFilter = filter;

    useNbtB.onGuiInit();
    useNbtB.setSelected(activeFilter.isMatchNBT());

    useOreDictB.onGuiInit();
    useOreDictB.setSelected(activeFilter.isUseOreDict());

    if (isStickyModeAvailable) {
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

    int x0 = getGuiLeft() + 80;
    int y0 = getGuiTop() + 65;
    int x1 = x0 + 65;
    int y1 = y0 + 22;

    snapshotB.onGuiInit();

    mergeB.height = x1;
    mergeB.packedFGColour = y0;

    clearB.height = x0;
    clearB.packedFGColour = y1;

    showB.height = x1;
    showB.packedFGColour = y1;

    clearB.enabled = filter.getSnapshot() != null;
    showB.enabled = clearB.enabled;
    mergeB.enabled = filter.getSnapshot() != null;

    addButton(clearB);
    addButton(showB);
    addButton(mergeB);
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_META) {
      filter.setMatchMeta(useMetaB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_NBT) {
      filter.setMatchNBT(useNbtB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_STICKY) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_ORE_DICT) {
      filter.setUseOreDict(useOreDictB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_SNAPSHOT) {
      sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode.SET);
    } else if (guiButton.id == ID_CLEAR) {
      sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode.CLEAR);
    } else if (guiButton.id == ID_MERGE) {
      sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode.MERGE);
    } else if (guiButton.id == ID_SHOW) {
      showSnapshotOverlay();
    } else if (guiButton == whiteListB) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    }
  }

  private void showSnapshotOverlay() {
    snapshotOverlay.setIsVisible(true);
  }

  private void sendSnapshotPacket(PacketExistingItemFilterSnapshot.Opcode opcode) {
    PacketHandler.INSTANCE.sendToServer(
        new PacketExistingItemFilterSnapshot(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1(), opcode));
  }

  private void sendFilterChange() {
    updateButtons();
    filterContainer.onFilterChanged();
    PacketHandler.INSTANCE
        .sendToServer(new PacketFilterUpdate(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1()));
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    // GL11.glColor3f(1, 1, 1);
    // RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
    // gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 68, 0, 238, 18 * 5, 18);
    // if(filter.isAdvanced()) {
    // gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 86, 0, 238, 18 * 5, 18);
    // }
  }

  class SnapshotOverlay implements IGuiOverlay {

    boolean visible;

    @Override
    public void init(@Nonnull IGuiScreen screen) {
    }

    @Override
    public @Nonnull Rectangle getBounds() {
      return new Rectangle(0, 0, width, height);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTick) {
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glEnable(GL11.GL_BLEND);
      RenderUtil.renderQuad2D(4, 4, 0, getXSize() - 9, getYSize() - 8, new Vector4f(0, 0, 0, 1));
      RenderUtil.renderQuad2D(6, 6, 0, getXSize() - 13, getYSize() - 12, new Vector4f(0.6, 0.6, 0.6, 1));

      Minecraft mc = Minecraft.getMinecraft();
      RenderItem itemRenderer = mc.getRenderItem();

      GL11.glEnable(GL11.GL_DEPTH_TEST);

      NNList<ItemStack> snapshot = filter.getSnapshot();
      int x = 15;
      int y = 10;
      int count = 0;
      for (ItemStack st : snapshot) {
        if (!st.isEmpty()) {
          itemRenderer.renderItemAndEffectIntoGUI(st, x, y);
        }
        x += 20;
        count++;
        if (count % 9 == 0) {
          x = 15;
          y += 20;
        }
      }
    }

    @Override
    public void setIsVisible(boolean visible) {
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

    @Override
    public void guiClosed() {
    }

  }
}
