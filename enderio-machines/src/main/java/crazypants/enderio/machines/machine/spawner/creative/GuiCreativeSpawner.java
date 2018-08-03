package crazypants.enderio.machines.machine.spawner.creative;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCreativeSpawner extends GuiCapMachineBase<TileCreativeSpawner> {

  public GuiCreativeSpawner(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileCreativeSpawner te,
      @Nonnull ContainerEnderCap<EnderInventory, TileCreativeSpawner> container) {
    super(te, container, "creative_spawner_admin", "creative_spawner_user");

    if (!isAdmin()) {
      redstoneButton.setEnabled(false);
      configB.setEnabled(false);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1, 1, 1);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    bindGuiTexture(isAdmin() ? 0 : 1);
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    if (isAdmin()) {
      getFontRenderer().drawStringWithShadow(Lang.GUI_CREATIVE_SPAWNER_SOUL.get(), sx + 26 + 20, sy + 12 + 4, ColorUtil.getRGB(Color.WHITE));
      getFontRenderer().drawStringWithShadow(Lang.GUI_CREATIVE_SPAWNER_TEMPLATE.get(), sx + 26 + 20, sy + 30 + 4, ColorUtil.getRGB(Color.WHITE));
      getFontRenderer().drawStringWithShadow(Lang.GUI_CREATIVE_SPAWNER_OFFERING.get(), sx + 26 + 20, sy + 48 + 4, ColorUtil.getRGB(Color.WHITE));
    } else {
      getFontRenderer().drawStringWithShadow(Lang.GUI_CREATIVE_SPAWNER_OFFERING.get(), sx + 26 + 20, sy + 30 + 4, ColorUtil.getRGB(Color.WHITE));
    }

    GlStateManager.color(1, 1, 1);
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected @Nonnull ResourceLocation getGuiTexture() {
    return super.getGuiTexture(isAdmin() ? 0 : 1);
  }

  private boolean isAdmin() {
    return getInventory() instanceof ContainerCreativeSpawner;
  }
}
