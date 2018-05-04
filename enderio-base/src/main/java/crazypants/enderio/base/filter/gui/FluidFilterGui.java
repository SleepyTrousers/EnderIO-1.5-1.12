package crazypants.enderio.base.filter.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.filter.fluid.FluidFilter;
import crazypants.enderio.base.filter.fluid.IFluidFilter;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.integration.jei.GhostSlotTarget;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidFilterGui extends AbstractFilterGui {

  private static final int ID_WHITELIST = FilterGuiUtil.nextButtonId();

  private final IconButton whiteListB;

  private final @Nonnull FluidFilter filter;

  private int xOffset;
  private int yOffset;

  public FluidFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IFluidFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn, "fluid_filter");

    xOffset = 13;
    yOffset = 34;

    filter = (FluidFilter) filterIn;

    int butLeft = xOffset + 98;

    int x = butLeft;
    int y = yOffset + 1;

    whiteListB = new IconButton(this, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
  }

  @Override
  public void initGui() {
    createFilterSlots();
    super.initGui();
  }

  public void createFilterSlots() {
    filter.createGhostSlots(getGhostSlotHandler().getGhostSlots(), xOffset + 1, yOffset + 1, new Runnable() {
      @Override
      public void run() {
        sendFilterChange();
      }
    });
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    whiteListB.onGuiInit();
    if (filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_BLACKLIST.get());
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
    }
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);
    if (button.id == ID_WHITELIST) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    }
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    int x = getGuiLeft() + xOffset;
    int y = getGuiTop() + yOffset;
    GlStateManager.color(1, 1, 1);
    bindGuiTexture();

    if (!filter.isEmpty()) {
      for (int i = 0; i < filter.size(); i++) {
        FluidStack f = filter.getFluidStackAt(i);
        if (f != null) {
          renderFluid(f, x + (i * 18), y);
        }
      }
    }
  }

  private void renderFluid(FluidStack f, int x, int y) {
    ResourceLocation iconKey = f.getFluid().getStill();
    TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
    if (icon != null) {
      RenderUtil.renderGuiTank(f, 1000, 1000, x + 1, y + 1, 0, 16, 16);
    }
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return Lang.GUI_FLUID_FILTER.get();
  }

  @Override
  public @Nonnull <I> List<GhostSlotTarget<I>> getTargetSlots() {
    List<GhostSlotTarget<I>> targets = new ArrayList<>();
    for (GhostSlot slot : getGhostSlotHandler().getGhostSlots()) {
      targets.add(new GhostSlotTarget<I>(filter, slot, getGuiLeft(), getGuiTop(), this));
    }
    return targets;
  }

}
