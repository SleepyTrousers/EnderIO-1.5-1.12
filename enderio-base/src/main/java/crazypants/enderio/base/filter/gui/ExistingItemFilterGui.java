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
  private static final int ID_WHITELIST = FilterGuiUtil.nextButtonId();

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

  public ExistingItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    super(playerInv, filterContainer, te);

    filter = (ExistingItemFilter) filterContainer.getItemFilter();

    isStickyModeAvailable = (filterContainer.filterIndex == FilterGuiUtil.INDEX_INPUT);

    int butLeft = 17;
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

    int x0 = getGuiLeft() + 246;
    int y0 = getGuiTop() + 84;

    snapshotB = new TooltipButton(this, ID_SNAPSHOT, 60, 64, 60, 20, EnderIO.lang.localize("gui.conduit.button.snap"));
    mergeB = new GuiButton(ID_MERGE, x0, y0, 40, 20, EnderIO.lang.localize("gui.conduit.button.merge"));

    x0 -= 64;
    y0 += 24;

    clearB = new GuiButton(ID_CLEAR, x0, y0, 60, 20, EnderIO.lang.localize("gui.conduit.button.clear"));

    x0 += 64;
    showB = new GuiButton(ID_SHOW, x0, y0, 40, 20, EnderIO.lang.localize("gui.conduit.button.show"));

    snapshotB.setToolTip(EnderIO.lang.localizeList("gui.conduit.button.snap.tooltip"));

    y -= 40;
    whiteListB = new IconButton(this, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
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

    snapshotB.onGuiInit();

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
    } else if (guiButton.id == ID_WHITELIST) {
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
    PacketHandler.INSTANCE
        .sendToServer(new PacketFilterUpdate(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1()));
  }

  class SnapshotOverlay implements IGuiOverlay {

    boolean visible;

    @Override
    public void init(@Nonnull IGuiScreen screen) {
    }

    @Override
    public @Nonnull Rectangle getBounds() {
      return new Rectangle(0, 0, xSize, ySize);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTick) {
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glEnable(GL11.GL_BLEND);
      RenderUtil.renderQuad2D(8, 8, 0, getXSize() - 11, getYSize() - 11, new Vector4f(0, 0, 0, 1));
      RenderUtil.renderQuad2D(10, 10, 0, getXSize() - 15, getYSize() - 15, new Vector4f(0.6, 0.6, 0.6, 1));

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
